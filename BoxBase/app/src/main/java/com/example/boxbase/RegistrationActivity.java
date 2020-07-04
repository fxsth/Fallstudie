package com.example.boxbase;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.RegisterMutation;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;
import com.example.boxbase.ui.login.LoginViewModel;
import com.example.boxbase.ui.login.LoginViewModelFactory;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;

public class RegistrationActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Für Geocoding
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_registration);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        final Button createAccountButton = findViewById(R.id.button_create_account);
        final Button discardButton = findViewById(R.id.button_discard);
        EditText box_first_name = findViewById(R.id.box_first_name);
        EditText box_last_name = findViewById(R.id.box_last_name);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);
        EditText box_email = findViewById(R.id.box_email);
        EditText box_set_password = findViewById(R.id.box_set_password);
        EditText box_repeat_password = findViewById(R.id.box_repeat_password);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = box_first_name.getText().toString().trim() + " " + box_last_name.getText().toString().trim();
                String address = box_street.getText().toString().trim() + " " +
                        box_number.getText().toString().trim() + ", " +
                        box_postcode.getText().toString().trim() + " " +
                        box_city.getText().toString().trim();
                String username = box_email.getText().toString().trim();
                String password = box_set_password.getText().toString().trim();
                GeocoderNominatim geocoderNominatim = new GeocoderNominatim("userAgent");
                double lat, lng;
                try {
                    List<Address> addresses = geocoderNominatim.getFromLocationName(address, 1);
                    if(address.length()>0) {
                        lat = addresses.get(0).getLatitude();
                        lng = addresses.get(0).getLongitude();
                    } else
                    {
                        Log.d("Geocoder", "Adresse nicht gefunden");
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                loginViewModel.register(username, password, name);
                loginViewModel.login(username,password);
                if (LoginRepository.getInstance(new LoginDataSource()).isLoggedIn()) {
                    LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
                    Log.d("JWT", user.getToken());
                    OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
                    ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
                    RegisterMutation registerMutation = RegisterMutation.builder().name(name).adresse(address).lat(lat).lng(lng).userid(user.getUserId()).build();
                    apolloClient.mutate(registerMutation).enqueue(new ApolloCall.Callback<RegisterMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RegisterMutation.Data> response) {
                            if(response.hasErrors())
                            {
                                Log.d("GraphQL", "Mutation fehlerhaft");
                                Log.d("GraphQL", response.getErrors().get(0).getMessage());
                            }else {
                                Log.d("GraphQL", "Mutation erfolgreich");
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.d("GraphQL", "Mutation fehlerhaft");
                        }
                    });
                }else{
                    return;
                }

                // TODO: Insert Adresse, Insert Person

                Intent mainIntent = new Intent(RegistrationActivity.this, MainMenuActivity.class);
                RegistrationActivity.this.startActivity(mainIntent);
                RegistrationActivity.this.finish();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean is_input_valid() {
        EditText box_first_name = findViewById(R.id.box_first_name);
        EditText box_last_name = findViewById(R.id.box_last_name);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);
        EditText box_email = findViewById(R.id.box_email);
        EditText box_set_password = findViewById(R.id.box_set_password);
        EditText box_repeat_password = findViewById(R.id.box_repeat_password);
        // Gültig wenn:
        // Alle Felder nicht leer
        if (isEmpty(box_first_name, box_last_name, box_street, box_number, box_postcode, box_city, box_email, box_set_password, box_repeat_password)) {
            return false;
        }
        // Benutzername ist Email
        if (!loginViewModel.isUserNameValid(box_email.getText().toString()))
            return false;
        // Passwort zweimal gleich eingegeben
        if (box_set_password.getText().toString().trim() != box_repeat_password.getText().toString().trim()) {
            return false;
        }
        return true;
    }

    private boolean isEmpty(EditText... etTexts) {
        for (EditText editText : etTexts) {
            if (editText.getText().toString().trim().length() == 0)
                return true;
        }
        return false;

    }
}