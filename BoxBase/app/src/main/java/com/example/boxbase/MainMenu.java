package com.example.boxbase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.IncomingQuery;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpClientBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainMenu extends AppCompatActivity {

    List<incoming_deliveries> incoming_deliveriesList;
    ListView incoming_deliveries_ListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        final Button sendPackageButton = findViewById(R.id.button_send_package);

        sendPackageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendPackageIntent = new Intent(MainMenu.this, SendPackageActivity.class);
                MainMenu.this.startActivity(sendPackageIntent);
            }
        });

        // Bei eingeloggten Usern aktualisiere die eingehenden Pakete
        if(LoginRepository.getInstance(new LoginDataSource()).isLoggedIn()) {
            LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
            OkHttpClient httpClient = HttpClientBuilder.getHttpClient(user.getToken());
            ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://roman.technology:8080/v1/graphql").okHttpClient(httpClient).build();
            ApolloQueryCall<IncomingQuery.Data> query = apolloClient.query(new IncomingQuery());
            query.enqueue(new ApolloCall.Callback<IncomingQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<IncomingQuery.Data> response) {
                    if (response.getData() != null) {
                        Log.d("GraphQLAntwort", response.getData().toString());
                        response.getData();
                        MainMenu.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateIncomingDeliveryList(response.getData().pakete());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("GraphQlFehler", e.toString());
                }
            });
        }
    }

    public void updateIncomingDeliveryList(List<IncomingQuery.Pakete> pakete)
    {
        List<incoming_deliveries> incoming_deliveriesList = new ArrayList<>();
        for(IncomingQuery.Pakete paket : pakete)
        {
            int drawable;
            String delivery_status;
            if(paket.zustellbasis_id() != null)
            {
                if(paket.fach_nummer() != null)
                {
                    delivery_status = "ready for pickup";
                    drawable = R.drawable.ic_delivery_status_icon_boxbase;
                }
                else {
                    delivery_status = "delivery is pending";
                    drawable = R.drawable.ic_delivery_status_icon_truck;
                }
            }
            else {
                delivery_status = "home delivery";
                drawable = R.drawable.ic_delivery_status_icon_home;
            }
            incoming_deliveriesList.add(
                    new incoming_deliveries(
                            drawable,
                            paket.absender(),
                            paket.empfaenger().ort().adresse(),
                            delivery_status)
            );
        }
        ListView incoming_deliveries_ListView = findViewById(R.id.incoming_deliveries_ListView);
        incoming_deliveries_list adapter = new incoming_deliveries_list(this, R.layout.incoming_delivery_list, incoming_deliveriesList);
        incoming_deliveries_ListView.setAdapter(adapter);
    }
}
