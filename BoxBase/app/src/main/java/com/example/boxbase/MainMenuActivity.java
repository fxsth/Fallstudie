package com.example.boxbase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.example.IncomingSubSubscription;
import com.example.OutgoingSubSubscription;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;
import com.example.boxbase.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /* BUTTONS */
        final Button sendPackageButton = findViewById(R.id.button_send_package);
        final Button refreshButton = findViewById(R.id.button_refresh);
        final ImageView imageViewAvatar = findViewById(R.id.top_bar_avatar);
        final TabLayout packagesTabLayout = findViewById(R.id.packagesTabLayout);
        ListView incoming_deliveries_ListView = findViewById(R.id.incoming_deliveries_ListView);
        ListView outgoing_deliveries_ListView = findViewById(R.id.outgoing_deliveries_ListView);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView settings = findViewById(R.id.settings_view);
        final Button settings_button_logout = findViewById(R.id.settings_button_logout);

        settings_button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRepository.getInstance(new LoginDataSource()).logout();
                Intent backToLogInIntent = new Intent(MainMenuActivity.this, LoginActivity.class);
                MainMenuActivity.this.startActivity(backToLogInIntent);
                MainMenuActivity.this.finish();
            }
        });

        sendPackageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendPackageIntent = new Intent(MainMenuActivity.this, SendPackageActivity.class);
                MainMenuActivity.this.startActivity(sendPackageIntent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(settings);
            }
        });

        packagesTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position % 2 == 0) {
                    incoming_deliveries_ListView.setVisibility(View.VISIBLE);
                    outgoing_deliveries_ListView.setVisibility(View.GONE);
                } else {
                    incoming_deliveries_ListView.setVisibility(View.GONE);
                    outgoing_deliveries_ListView.setVisibility(View.VISIBLE);

                }
                refreshTab(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        refreshTab(0);

        // Bei eingeloggten Usern aktualisiere die eingehenden Pakete
    }

    private void refreshTab(int position) {

        if (LoginRepository.getInstance(new LoginDataSource()).isLoggedIn()) {
            LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
            OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
            ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).subscriptionTransportFactory(
                    new WebSocketSubscriptionTransport.Factory(HttpUtilities.getGraphQLUrl(), httpClient)
            ).build();
            if (position == 0) {   // Position 0 entspricht dem Incoming-Tab
                IncomingSubSubscription incomingsubscription = IncomingSubSubscription.builder().user(user.getUserId()).build();
                ApolloSubscriptionCall<IncomingSubSubscription.Data> sub = apolloClient.subscribe(incomingsubscription);
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
                        Log.d("GraphQlFehler", e.getMessage().toString());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("GraphQl", "Completed");
                    }

                    @Override
                    public void onTerminated() {
                        Log.d("GraphQlFehler", "Terminiert");
                    }

                    @Override
                    public void onConnected() {
                        Log.d("GraphQl", "Connected");
                    }
                });
            } else {  // Position 1 entspricht dem Outgoing-Tab
                OutgoingSubSubscription outgoingsubscription = OutgoingSubSubscription.builder().user(user.getUserId()).build();
                ApolloSubscriptionCall<OutgoingSubSubscription.Data> sub = apolloClient.subscribe(outgoingsubscription);
                sub.execute(new ApolloSubscriptionCall.Callback<OutgoingSubSubscription.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<OutgoingSubSubscription.Data> response) {
                        if (response.getData() != null) {
                            Log.d("GraphQLAntwort", response.getData().toString());
                            response.getData();
                            MainMenuActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateOutgoingDeliveryList(response.getData().pakete());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("GraphQlFehler", e.getMessage().toString());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("GraphQl", "Completed");
                    }

                    @Override
                    public void onTerminated() {
                        Log.d("GraphQlFehler", "Terminiert");
                    }

                    @Override
                    public void onConnected() {
                        Log.d("GraphQl", "Connected");
                    }
                });
            }
        }
    }

    public void updateIncomingDeliveryList(List<IncomingSubSubscription.Pakete> pakete) {
        List<incoming_deliveries> incoming_deliveriesList = new ArrayList<>();
        for (IncomingSubSubscription.Pakete paket : pakete) {
            int drawable;
            String delivery_status;
            String destination;
            if (paket.zustellbasis_id() != null) {
                if (paket.fach_nummer() != null) {
                    delivery_status = "ready for pickup";
                    drawable = R.drawable.icon_delivery_status_boxbase;
                } else {
                    delivery_status = "delivery is pending";
                    drawable = R.drawable.icon_delivery_status_truck;
                }
                destination = "Zustellbasis " + paket.zustellbasis_id();
            } else {
                delivery_status = "delivery is pending";
                drawable = R.drawable.icon_delivery_status_home;
                destination = paket.empfaenger().ort().adresse();
            }
            if(paket.zugestellt())
                delivery_status = "delivered";
            incoming_deliveriesList.add(
                    new incoming_deliveries(
                            drawable,
                            paket.sender().name(),
                            destination,
                            delivery_status)
            );
        }
        ListView incoming_deliveries_ListView = findViewById(R.id.incoming_deliveries_ListView);
        incoming_deliveries_ListView.post(new Runnable() {
                                              @Override
                                              public void run() {
                                                  incoming_deliveries_list adapter = new incoming_deliveries_list(
                                                          MainMenuActivity.this, R.layout.main_menu_incoming_delivery, incoming_deliveriesList
                                                  );
                                                  incoming_deliveries_ListView.setAdapter(adapter);
                                              }
                                          }
        );
    }

    public void updateOutgoingDeliveryList(List<OutgoingSubSubscription.Pakete> pakete) {
        List<outgoing_deliveries> outgoing_deliveriesList = new ArrayList<>();
        for (OutgoingSubSubscription.Pakete paket : pakete) {
            int drawable;
            String delivery_status;
            if (paket.zustellbasis_id() != null) {
                if (paket.fach_nummer() != null) {
                    delivery_status = "ready for pickup";
                    drawable = R.drawable.icon_delivery_status_boxbase;
                } else {
                    delivery_status = "delivery is pending";
                    drawable = R.drawable.icon_delivery_status_truck;
                }
            } else {
                delivery_status = "home delivery";
                drawable = R.drawable.icon_delivery_status_home;
            }
            outgoing_deliveriesList.add(
                    new outgoing_deliveries(
                            drawable,
                            paket.empfaenger().name(),
                            delivery_status)
            );
        }
        ListView outgoing_deliveries_ListView = findViewById(R.id.outgoing_deliveries_ListView);
        outgoing_deliveries_ListView.post(new Runnable() {
                                              @Override
                                              public void run() {
                                                  outgoing_deliveries_list adapter = new outgoing_deliveries_list(MainMenuActivity.this, R.layout.main_menu_outgoing_delivery, outgoing_deliveriesList);
                                                  outgoing_deliveries_ListView.setAdapter(adapter);
                                              }
                                          }
        );
    }
}
