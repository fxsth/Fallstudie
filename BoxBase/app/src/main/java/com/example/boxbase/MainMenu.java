package com.example.boxbase;

import android.content.Intent;
import android.os.AsyncTask;
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

        // Wenn der Benutzer eingeloggt ist -> Frage die eingehenden Sendungen ab und aktualisier die deliveriesList
        if(LoginRepository.getInstance(new LoginDataSource()).isLoggedIn())
            new IncomingQueryTask().execute();

        sendPackageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendPackageIntent = new Intent(MainMenu.this, SendPackageActivity.class);
                MainMenu.this.startActivity(sendPackageIntent);
            }
        });

        incoming_deliveriesList = new ArrayList<>();

        /* these are just example; later entries will come from the database */

        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));

        incoming_deliveries_ListView = findViewById(R.id.incoming_deliveries_ListView);

        incoming_deliveries_list adapter = new incoming_deliveries_list(this, R.layout.incoming_delivery_list, incoming_deliveriesList);

        incoming_deliveries_ListView.setAdapter(adapter);

    }
}

class IncomingQueryTask extends AsyncTask<Void, Void, Void> {
    protected Void doInBackground(Void... voids) {
        LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
        OkHttpClient httpClient = HttpClientBuilder.getHttpClient(user.getToken());
        ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://roman.technology:8080/v1/graphql").okHttpClient(httpClient).build();
        ApolloQueryCall<IncomingQuery.Data> query = apolloClient.query(new IncomingQuery());
        query.enqueue(new ApolloCall.Callback<IncomingQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<IncomingQuery.Data> response) {
                if(response.getData() !=null)
                {
                    Log.d("GraphQLAntwort", response.getData().toString() );
                    response.getData();
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("GraphQlFehler", e.toString() );
            }
        });
        return null;
    }
}