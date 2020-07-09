package com.example.boxbase.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.InsertOrtMutation;
import com.example.RedirectMutation;
import com.example.UserQuery;
import com.example.boxbase.R;
import com.example.boxbase.SetPointOnMapActivity;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;

public class RedirectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ConstraintLayout button_point_on_map;
    ConstraintLayout button_home_address;

    // Declaration for dropdown menus
    private static final String[] paths_day_selection = {"today", "tomorrow", "in 2 days", "in 3 days", "in 4 days", "in 5 days"};
    private static final String[] paths_time_selection_from = {"6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm"};
    private static final String[] paths_time_selection_to = {"7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm"};


    // Ergebnisse aus dem Geocoding
    String destinationAddress;
    double lat, lng;
    int wunschortid;
    boolean erfolgreicheQuery;
    int paketid;
    int LAUNCH_SETPOINTONMAP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_package);
        LinearLayout delivery_to_redirect = findViewById(R.id.delivery_to_redirect);
        TextView delivery_sender = findViewById(R.id.delivery_sender);
        TextView delivery_destination = findViewById(R.id.delivery_destination);
        TextView delivery_status = findViewById(R.id.delivery_status);
        ImageView delivery_status_image = findViewById(R.id.delivery_status_icon);
        ImageView arrow_to_open_box = findViewById(R.id.arrow_to_open_box);
        button_point_on_map = findViewById(R.id.button_point_on_map);
        button_home_address = findViewById(R.id.button_home_address);
        Button button_redirection_confirm = findViewById(R.id.button_redirection_confirm);
        Button button_discard = findViewById(R.id.button_discard);

        //location_selection_mobile_delivery_base
        ImageView arrow_to_close_mdb_box = findViewById(R.id.arrow_to_close_mdb_box);
        ImageView arrow_to_open_mdb_box = findViewById(R.id.arrow_to_open_mdb_box);
        ConstraintLayout cl_selection_mobile_delivery_base = findViewById(R.id.cl_selection_mobile_delivery_base);
        arrow_to_close_mdb_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_selection_mobile_delivery_base.setVisibility(View.GONE);
                arrow_to_close_mdb_box.setVisibility(View.INVISIBLE);
                arrow_to_open_mdb_box.setVisibility(View.VISIBLE);
            }
        });
        arrow_to_open_mdb_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_selection_mobile_delivery_base.setVisibility(View.VISIBLE);
                arrow_to_close_mdb_box.setVisibility(View.VISIBLE);
                arrow_to_open_mdb_box.setVisibility(View.INVISIBLE);
            }
        });
        button_point_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);

                Intent SetPointOnMapIntent = new Intent(RedirectActivity.this, SetPointOnMapActivity.class);
                startActivityForResult(SetPointOnMapIntent, LAUNCH_SETPOINTONMAP);
            }
        });
        button_home_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                destinationAddress = "";    // Heimadresse wird als Destination übernommen
            }
        });


        button_redirection_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erfolgreicheQuery = false;
                // Prüfen ob gültige PaketId übergeben wurde
                if(paketid != -1) {
                    LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
                    OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
                    ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
                    // Bei gültiger Adresse -> Wunschort eintragen, ansonsten -> Wunschort löschen
                    if(destinationAddress!= null && !destinationAddress.isEmpty()) {
                        InsertOrtMutation insertOrtMutation = InsertOrtMutation.builder().adresse(destinationAddress).lat(lat).lng(lng).build();
                        apolloClient.mutate(insertOrtMutation).enqueue(new ApolloCall.Callback<InsertOrtMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<InsertOrtMutation.Data> response) {
                                if (response.hasErrors()) {
                                    Log.d("GraphQL", "Mutation fehlerhaft");
                                    Log.d("GraphQL", response.getErrors().get(0).getMessage());
                                } else {
                                    wunschortid = response.getData().insert_ort_one().id();
                                    Log.d("GraphQL", "Mutation erfolgreich");

                                    // Im erfolgreichen Fall die Update-Mutation
                                    RedirectMutation redirectMutation = RedirectMutation.builder().paketid(paketid).wunschortid(wunschortid).build();
                                    apolloClient.mutate(redirectMutation).enqueue(new ApolloCall.Callback<RedirectMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<RedirectMutation.Data> response) {
                                            if (response.hasErrors()) {
                                                Log.d("GraphQL", "Mutation fehlerhaft");
                                                Log.d("GraphQL", response.getErrors().get(0).getMessage());
                                                Toast.makeText(RedirectActivity.this, "Redirection  not successful", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("GraphQL", "Mutation erfolgreich");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(RedirectActivity.this, "Redirection successful", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {
                                            Log.d("GraphQL", "Mutation fehlerhaft");
                                            Toast.makeText(RedirectActivity.this, "Redirection not successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.d("GraphQL", "Mutation fehlerhaft");
                                Toast.makeText(RedirectActivity.this, "Redirection not successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                    {
                        UserQuery userQuery = UserQuery.builder().userid(user.getUserId()).build();
                        apolloClient.query(userQuery).enqueue(new ApolloCall.Callback<UserQuery.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<UserQuery.Data> response) {
                                if (response.hasErrors()) {
                                    Log.d("GraphQL", "Mutation fehlerhaft");
                                    Log.d("GraphQL", response.getErrors().get(0).getMessage());
                                } else {
                                    // Prüfe vorher, ob Person in der Db vorhanden und auch eine Adresse vorhanden
                                    if(response.getData().person().size()>0 && !response.getData().person().get(0).ort().adresse().isEmpty()) {
                                        wunschortid = response.getData().person().get(0).ort().id();
                                        Log.d("GraphQL", "Mutation erfolgreich");

                                        // Im erfolgreichen Fall die Update-Mutation
                                        RedirectMutation redirectMutation = RedirectMutation.builder().paketid(paketid).wunschortid(wunschortid).build();
                                        apolloClient.mutate(redirectMutation).enqueue(new ApolloCall.Callback<RedirectMutation.Data>() {
                                            @Override
                                            public void onResponse(@NotNull Response<RedirectMutation.Data> response) {
                                                if (response.hasErrors()) {
                                                    Log.d("GraphQL", "Mutation fehlerhaft");
                                                    Log.d("GraphQL", response.getErrors().get(0).getMessage());
                                                    Toast.makeText(RedirectActivity.this, "Redirection  not successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.d("GraphQL", "Mutation erfolgreich");
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(RedirectActivity.this, "Redirection successful", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NotNull ApolloException e) {
                                                Log.d("GraphQL", "Mutation fehlerhaft");
                                                Toast.makeText(RedirectActivity.this, "Redirection not successful", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        Log.d("GraphQL", "Person nicht gefunden");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.d("GraphQL", "Mutation fehlerhaft");
                                Toast.makeText(RedirectActivity.this, "Redirection not successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        button_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        paketid = intent.getIntExtra("paketid", -1);
        delivery_sender.setText(intent.getStringExtra("sender"));
        delivery_destination.setText(intent.getStringExtra("destination"));
        delivery_status.setText(intent.getStringExtra("status"));
        delivery_status_image.setImageDrawable(RedirectActivity.this.getResources().getDrawable(intent.getIntExtra("statusImage", 0)));
        arrow_to_open_box.setVisibility(View.INVISIBLE);


        // Function of the dropdown menus
        Spinner spinner_day_selection = (Spinner) findViewById(R.id.time_slot_selection_day);
        Spinner spinner_time_selection_from = (Spinner) findViewById(R.id.time_slot_selection_from);
        Spinner spinner_time_selection_to = (Spinner) findViewById(R.id.time_slot_selection_to);
        // day selection
        ArrayAdapter<String> adapter_day_selection = new ArrayAdapter<>(RedirectActivity.this,
                R.layout.spinner_layout, paths_day_selection);
        adapter_day_selection.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_day_selection.setAdapter(adapter_day_selection);
        spinner_day_selection.setOnItemSelectedListener(this);
        // time "from" selection
        ArrayAdapter<String> adapter_time_selection_from = new ArrayAdapter<>(RedirectActivity.this,
                R.layout.spinner_layout, paths_time_selection_from);
        adapter_time_selection_from.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_time_selection_from.setAdapter(adapter_time_selection_from);
        spinner_time_selection_from.setOnItemSelectedListener(this);
        // day "to" selection
        ArrayAdapter<String> adapter_time_selection_to = new ArrayAdapter<>(RedirectActivity.this,
                R.layout.spinner_layout, paths_time_selection_to);
        adapter_time_selection_to.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_time_selection_to.setAdapter(adapter_time_selection_to);
        spinner_time_selection_to.setOnItemSelectedListener(this);
    }


    // Cases of the dropdown menus
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                // TODO Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // TODO Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // TODO Whatever you want to happen when the third item gets selected
                break;

        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SETPOINTONMAP) {
            if(resultCode == Activity.RESULT_OK){
                destinationAddress = data.getStringExtra("adress");
                lat=data.getDoubleExtra("lat", 0);
                lng=data.getDoubleExtra("lng", 0);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                lat = 0.0;
                lng = 0.0;
                destinationAddress ="";
            }
        }
    }//onActivityResult
}