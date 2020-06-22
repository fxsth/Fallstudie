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
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.example.IncomingQuery;
import com.example.IncomingSubSubscription;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;

public class MainMenuActivity extends AppCompatActivity {

    List<incoming_deliveries> incoming_deliveriesList;
    ListView incoming_deliveries_ListView;

    List<outgoing_deliveries> outgoing_deliveriesList;
    ListView outgoing_deliveries_ListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);




        outgoing_deliveriesList = new ArrayList<>();

        /* these are just example; later entries will come from the database */
        outgoing_deliveriesList.add(new outgoing_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Amazon", "ready for drop off"));
        outgoing_deliveriesList.add(new outgoing_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "ready for drop off"));
        outgoing_deliveriesList.add(new outgoing_deliveries(R.drawable.ic_delivery_status_icon_truck, "Peter Müller", "arrive tomorrow"));

        outgoing_deliveries_ListView = findViewById(R.id.outgoing_deliveries_ListView);

        outgoing_deliveries_list adapter = new outgoing_deliveries_list(this, R.layout.outgoing_delivery_list, outgoing_deliveriesList);

        outgoing_deliveries_ListView.setAdapter(adapter);





        final Button sendPackageButton = findViewById(R.id.button_send_package);
        final Button refreshButton = findViewById(R.id.button_refresh);

        sendPackageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendPackageIntent = new Intent(MainMenuActivity.this, SendPackageActivity.class);
                MainMenuActivity.this.startActivity(sendPackageIntent);
            }
        });

        
        // Bei eingeloggten Usern aktualisiere die eingehenden Pakete

        refreshLayout();
    }

    private void refreshLayout() {

        if(LoginRepository.getInstance(new LoginDataSource()).isLoggedIn()) {
            LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
            OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
            ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory(HttpUtilities.getGraphQLUrl(), httpClient)).build();
            ApolloSubscriptionCall<IncomingSubSubscription.Data> sub = apolloClient.subscribe(new IncomingSubSubscription());
            sub.execute(new ApolloSubscriptionCall.Callback<IncomingSubSubscription.Data>() {
                @Override
                public void onResponse(@NotNull Response<IncomingSubSubscription.Data> response) {
                    if (response.getData() != null) {
                        Log.d("GraphQLAntwort", response.getData().toString());
                        response.getData();
                        MainMenuActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateIncomingDeliveryList(response.getData().pakete());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onTerminated() {
                    Log.d("GraphQlFehler", "Terminiert");
                }

                @Override
                public void onConnected() {

                }
            });
    }}

    public void updateIncomingDeliveryList(List<IncomingSubSubscription.Pakete> pakete)
    {
        List<incoming_deliveries> incoming_deliveriesList = new ArrayList<>();
        for(IncomingSubSubscription.Pakete paket : pakete)
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
