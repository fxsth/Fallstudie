package com.example.boxbase;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.InsertEmpfaengerMutation;
import com.example.InsertOrtMutation;
import com.example.InsertSendPackageMutation;
import com.example.UserIdByNameAndAddressQuery;
import com.example.UserQuery;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;
import com.example.boxbase.ui.RedirectActivity;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;


public class SendPackageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    // Variablen für Mutation
    String name;
    String destinationAddress;
    int reciever_id;
    int reciever_ort_id;
    int package_size = 1;

    // Variablen für Timestamp
    Date von, bis;
    Spinner spinner_day_selection;
    Spinner spinner_time_selection_from;
    Spinner spinner_time_selection_to;

    // Ergebnisse aus dem Geocoding
    String desiredAddress;
    double lat, lng;
    int wunschortid = -1;
    int LAUNCH_SETPOINTONMAP = 1;

    // Declaration for dropdown menus
    private static final String[] paths_day_selection = {"today", "tomorrow", "in 2 days", "in 3 days", "in 4 days", "in 5 days"};
    private static final String[] paths_time_selection_from = {"6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm"};
    private static final String[] paths_time_selection_to = {"7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm"};
    ConstraintLayout button_point_on_map;
    ConstraintLayout button_home_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_package);
        final Button goToPaymentButton = findViewById(R.id.button_send_package_confirm);
        final Button discardButton = findViewById(R.id.button_discard);
        ConstraintLayout button_size_s = findViewById(R.id.button_package_size_s);
        ConstraintLayout button_size_m = findViewById(R.id.button_package_size_m);
        ConstraintLayout button_size_l = findViewById(R.id.button_package_size_l);
        ConstraintLayout button_point_on_map = findViewById(R.id.button_point_on_map);
        ConstraintLayout button_home_address = findViewById(R.id.button_home_address);
        EditText box_receiver = findViewById(R.id.box_receiver);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);
        EditText box_country = findViewById(R.id.box_country);

        reciever_id = -1;

        // Function of the dropdown menus
        spinner_day_selection = (Spinner) findViewById(R.id.time_slot_selection_day);
        spinner_time_selection_from = (Spinner) findViewById(R.id.time_slot_selection_from);
        spinner_time_selection_to = (Spinner) findViewById(R.id.time_slot_selection_to);
        // day selection
        ArrayAdapter<String> adapter_day_selection = new ArrayAdapter<>(SendPackageActivity.this,
                R.layout.spinner_layout, paths_day_selection);
        adapter_day_selection.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_day_selection.setAdapter(adapter_day_selection);
        spinner_day_selection.setOnItemSelectedListener(this);
        // time "from" selection
        ArrayAdapter<String> adapter_time_selection_from = new ArrayAdapter<>(SendPackageActivity.this,
                R.layout.spinner_layout, paths_time_selection_from);
        adapter_time_selection_from.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_time_selection_from.setAdapter(adapter_time_selection_from);
        spinner_time_selection_from.setOnItemSelectedListener(this);
        // day "to" selection
        ArrayAdapter<String> adapter_time_selection_to = new ArrayAdapter<>(SendPackageActivity.this,
                R.layout.spinner_layout, paths_time_selection_to);
        adapter_time_selection_to.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_time_selection_to.setAdapter(adapter_time_selection_to);
        spinner_time_selection_to.setOnItemSelectedListener(this);


        // package size selection
        button_size_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color change
                button_size_s.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_size_m.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_l.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                package_size = 1;
            }
        });
        button_size_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color change
                button_size_s.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_m.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_size_l.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                package_size = 2;
            }
        });
        button_size_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color change
                button_size_s.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_m.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_l.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                package_size = 3;
            }
        });


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
                Intent SetPointOnMapIntent = new Intent(SendPackageActivity.this, SetPointOnMapActivity.class);
                startActivityForResult(SetPointOnMapIntent, LAUNCH_SETPOINTONMAP);
            }
        });
        button_home_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                // TODO: set up home address as desired address
            }
        });

        goToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Date nowUTC = Date.from(Instant.now().minus(Duration.ofHours(3)));
                if(bis.before(nowUTC))
                {
                    Toast.makeText(SendPackageActivity.this, "invalid time", Toast.LENGTH_SHORT).show();
                    return;
                }
                name = box_receiver.getText().toString().trim();
                destinationAddress = box_street.getText().toString().trim() + " " +
                        box_number.getText().toString().trim() + ", " +
                        box_postcode.getText().toString().trim() + " " +
                        box_city.getText().toString().trim();

                LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
                OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
                ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();

                // Erst prüfen ob es bereits eine Person in der Datenbank mit dieser Adresse gibt
                UserIdByNameAndAddressQuery userQuery = UserIdByNameAndAddressQuery.builder().adresse(destinationAddress).name(name).build();
                apolloClient.query(userQuery).enqueue(new ApolloCall.Callback<UserIdByNameAndAddressQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UserIdByNameAndAddressQuery.Data> response) {
                        if (response.hasErrors()) {
                            Log.d("GraphQL", "Query fehlerhaft");
                            Log.d("GraphQL", response.getErrors().get(0).getMessage());
                        } else {
                            Log.d("GraphQL", "Query erfolgreich");
                            if (response.getData().person().size() > 0) {
                                reciever_id = response.getData().person().get(0).id();
                                setWunschortidAndInsertPackage(reciever_id, user.getUserId(), von.toString(), bis.toString(), package_size);
                            } else
                            {
                                // Wenn es den Empfänger noch nciht gibt, fügen wir ihn hinzu
                                InsertEmpfaengerMutation insertEmpfaengerMutation = InsertEmpfaengerMutation.builder().adresse(destinationAddress).lat(lat).lng(lng).name(name).build();
                                apolloClient.mutate(insertEmpfaengerMutation).enqueue(new ApolloCall.Callback<InsertEmpfaengerMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<InsertEmpfaengerMutation.Data> response) {
                                        if (response.hasErrors()) {
                                            Log.d("GraphQL", "InsertEmpfänger Mutation fehlerhaft");
                                            Log.d("GraphQL", response.getErrors().get(0).getMessage());
                                        } else {
                                            Log.d("GraphQL", "InsertEmpfänger Mutation erfolgreich");
                                            reciever_id = response.getData().insert_person_one().id();
                                            setWunschortidAndInsertPackage(reciever_id, user.getUserId(), von.toString(), bis.toString(), package_size);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {
                                        Log.d("GraphQL", "InsertEmpfänger Mutation fehlerhaft");
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("GraphQL", "UserIdByNameAndAddressQuery fehlerhaft");
                    }
                });
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // Cases of the dropdown menus
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinner_day_selection != null && spinner_time_selection_from != null && spinner_time_selection_to != null) {
            Date now = Date.from(Instant.now());
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(now);
            c2.setTime(now);

            c1.add(Calendar.DATE, spinner_day_selection.getSelectedItemPosition());
            c2.add(Calendar.DATE, spinner_day_selection.getSelectedItemPosition());

            c1.set(Calendar.HOUR_OF_DAY, spinner_time_selection_from.getSelectedItemPosition() + 4);   // +4 entspricht +6h und Umrechnung auf UTC
            c1.set(Calendar.MINUTE, 0);
            c1.set(Calendar.SECOND, 0);

            c2.set(Calendar.HOUR_OF_DAY, spinner_time_selection_to.getSelectedItemPosition() + 5);
            c2.set(Calendar.MINUTE, 0);
            c2.set(Calendar.SECOND, 0);

            von = c1.getTime();
            bis = c2.getTime();
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
                desiredAddress = data.getStringExtra("adress");
                lat=data.getDoubleExtra("lat", 0);
                lng=data.getDoubleExtra("lng", 0);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                lat = 0.0;
                lng = 0.0;
                desiredAddress ="";
            }
        }
    }//onActivityResult

    void insertPackage(int reciever_id, int userid, int wunschortid, String timestamp_von, String timestamp_bis, int groesse)
    {
        LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
        OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
        ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
        // Anschließend das Paket in die Datenbank mit allen benötigten Daten eintragen
        InsertSendPackageMutation insertSendPackageMutation =
                InsertSendPackageMutation
                        .builder()
                        .empfaenger_id(reciever_id)
                        .absender_id(userid)
                        .wunschort_id(wunschortid)
                        .interaktion_von(timestamp_von)
                        .interaktion_bis(timestamp_bis)
                        .groesse(groesse)
                        .build();
        apolloClient.mutate(insertSendPackageMutation).enqueue(new ApolloCall.Callback<InsertSendPackageMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<InsertSendPackageMutation.Data> response) {
                if (response.hasErrors()) {
                    Log.d("GraphQL", "Mutation fehlerhaft");
                    Log.d("GraphQL", response.getErrors().get(0).getMessage());
                } else {
                    Log.d("GraphQL", "Mutation erfolgreich");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SendPackageActivity.this, "Send package successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("GraphQL", "InsertSendPackageMutation fehlerhaft");
            }
        });
    }

    void setWunschortidAndInsertPackage(int reciever_id, int userid, String timestamp_von, String timestamp_bis, int groesse)
    {
        LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
        OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
        ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
        // Prüfen welcher Drop-Off-Wunschort eingetragen wurde und entsprechende OrtId aus der Datenbank nehmen
        if(desiredAddress != null && !desiredAddress.isEmpty()) {
            InsertOrtMutation insertOrtMutation = InsertOrtMutation.builder().adresse(desiredAddress).lat(lat).lng(lng).build();
            apolloClient.mutate(insertOrtMutation).enqueue(new ApolloCall.Callback<InsertOrtMutation.Data>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(@NotNull Response<InsertOrtMutation.Data> response) {
                    if (response.hasErrors()) {
                        Log.d("GraphQL", "Wunschort eintragen - Mutation fehlerhaft");
                        Log.d("GraphQL", response.getErrors().get(0).getMessage());
                    } else {
                        wunschortid = response.getData().insert_ort_one().id();
                        Log.d("GraphQL", "Mutation erfolgreich");
                        insertPackage(reciever_id, user.getUserId(), wunschortid, timestamp_von, timestamp_bis, package_size);
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("GraphQL", "Wunschort eintragen - Mutation fehlerhaft");
                }
            });
        } else
        {
            UserQuery userQuery = UserQuery.builder().userid(user.getUserId()).build();
            apolloClient.query(userQuery).enqueue(new ApolloCall.Callback<UserQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<UserQuery.Data> response) {
                    if (response.hasErrors()) {
                        Log.d("GraphQL", "Query fehlerhaft");
                        Log.d("GraphQL", response.getErrors().get(0).getMessage());
                    } else {
                        // Prüfe vorher, ob Person in der Db vorhanden und auch eine Adresse vorhanden
                        if(response.getData().person().size()>0 && !response.getData().person().get(0).ort().adresse().isEmpty()) {
                            wunschortid = response.getData().person().get(0).ort().id();
                            Log.d("GraphQL", "Query erfolgreich");
                            insertPackage(reciever_id, user.getUserId(), wunschortid, timestamp_von, timestamp_bis, package_size);
                        }else {
                            Log.d("GraphQL", "Benutzer nicht gefunden");
                        }
                    }
                }
                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("GraphQL", "Mutation fehlerhaft");
                }
            });
        }
    }
}