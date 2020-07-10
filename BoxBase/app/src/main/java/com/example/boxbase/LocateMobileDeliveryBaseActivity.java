package com.example.boxbase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.LocationMobileDeliveryBaseQuery;
import com.example.boxbase.data.LoginDataSource;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class LocateMobileDeliveryBaseActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    MapView map;
    LocationManager locationManager;
    LocationListener locationListener;
    IMapController mapController;
    GeoPoint ownLocationPoint;
    GeoPoint destinationPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_mobile_delivery_base);

        int paketid;

        TextView delivery_sender = findViewById(R.id.delivery_sender);
        TextView delivery_destination = findViewById(R.id.delivery_destination);
        TextView delivery_status = findViewById(R.id.delivery_status);
        ImageView delivery_status_image = findViewById(R.id.delivery_status_icon);
        ImageView arrow_to_open_box = findViewById(R.id.arrow_to_open_box);
        Button button_open_compartment = findViewById(R.id.button_open_compartment);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);
        Button button_close = findViewById(R.id.button_close);

        Intent intent = getIntent();
        paketid = intent.getIntExtra("paketid", -1);
        delivery_sender.setText(intent.getStringExtra("sender"));
        delivery_destination.setText(intent.getStringExtra("destination"));
        delivery_status.setText(intent.getStringExtra("status"));
        delivery_status_image.setImageDrawable(LocateMobileDeliveryBaseActivity.this.getResources().getDrawable(intent.getIntExtra("statusImage", 0)));
        arrow_to_open_box.setVisibility(View.INVISIBLE);
        if(!delivery_destination.getText().equals("") && isValidAddress(delivery_destination.getText().toString())) {
            String[] felder = getAddressFields(delivery_destination.getText().toString());
            box_street.setText(felder[0]);
            box_number.setText(felder[1]);
            box_postcode.setText(felder[2]);
            box_city.setText(felder[3]);
        } else
        {

        }

        button_open_compartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCompartmentIntent = new Intent(LocateMobileDeliveryBaseActivity.this, OpenCompartmentActivity.class);
                openCompartmentIntent.putExtra("statusImage", intent.getIntExtra("statusImage", 0));
                openCompartmentIntent.putExtra("sender", intent.getStringExtra("sender"));
                openCompartmentIntent.putExtra("destination", intent.getStringExtra("destination"));
                openCompartmentIntent.putExtra("status", intent.getStringExtra("status"));
                LocateMobileDeliveryBaseActivity.this.startActivity(openCompartmentIntent);
            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocateMobileDeliveryBaseActivity.this.finish();
            }
        });

        // Benötigt für Geocoder
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = (MapView) findViewById(R.id.map);
        // MapView und Scrollview streiten sich um vertikales Scrollen
        // Lösung: Bei Touch auf MapView wird Scrollview abgeschaltet
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        map.setTileSource(TileSourceFactory.MAPNIK);
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(51.51, 7.4684);
        mapController.setCenter(startPoint);

        Marker desiredAddressMarker = new Marker(map);


        LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
        OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(user.getToken());
        ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
        LocationMobileDeliveryBaseQuery locationMobileDeliveryBaseQuery = LocationMobileDeliveryBaseQuery.builder().paketid(paketid).build();
        apolloClient.query(locationMobileDeliveryBaseQuery).enqueue(new ApolloCall.Callback<LocationMobileDeliveryBaseQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<LocationMobileDeliveryBaseQuery.Data> response) {
                if (response.hasErrors()) {
                    Log.d("GraphQL", "Query fehlerhaft");
                    Log.d("GraphQL", response.getErrors().get(0).getMessage());
                } else {
                    // Ist überhaupt eine Zustellbasis-Id bei dem Paket eingetragen?
                    if(response.getData().pakete_by_pk() != null) {
                        {
                            BigDecimal latBD = (BigDecimal) response.getData().pakete_by_pk().zustellbasis().lat();
                            BigDecimal lngBD = (BigDecimal) response.getData().pakete_by_pk().zustellbasis().long_();
                            double lat = latBD.doubleValue();
                            double lng = lngBD.doubleValue();
                            if(lat != 0.0 && lng != 0.0) {
                                destinationPoint = new GeoPoint(lat, lng);
                                desiredAddressMarker.setPosition(destinationPoint);
                                desiredAddressMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                desiredAddressMarker.setIcon(getResources().getDrawable(R.drawable.icon_location_green));
                                desiredAddressMarker.setTitle(delivery_destination.getText().toString());
                                map.getOverlays().add(desiredAddressMarker);
                                if(ownLocationPoint != null && destinationPoint != null) {
                                    ArrayList<GeoPoint> positions = new ArrayList<GeoPoint>();
                                    positions.add(ownLocationPoint);
                                    positions.add(destinationPoint);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            map.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(positions), true, 100, 17, 1500L);
                                        }
                                    });
                                }
                                map.invalidate();   // MapView aktualisieren
                            }
                        }
                    }else {
                        Log.d("GraphQL", "Person nicht gefunden");
                    }
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("GraphQL", "Mutation fehlerhaft");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Warte bis Zugriffe erlaubt wurden
        mark_user_location();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            // Zugriffe bereits erlaubt
            mark_user_location();
        }
    }

    private void mark_user_location() {

        Marker userLocation = new Marker(map);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                ownLocationPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.setZoom(16);
                userLocation.setPosition(ownLocationPoint);
                userLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                userLocation.setIcon(getResources().getDrawable(R.drawable.icon_avatar));
                userLocation.setTitle("You are here");
                mapController.setCenter(ownLocationPoint);
                map.getOverlays().add(userLocation);
                locationManager.removeUpdates(this);
                locationManager = null;
                if(ownLocationPoint != null && destinationPoint != null) {
                    ArrayList<GeoPoint> positions = new ArrayList<GeoPoint>();
                    positions.add(ownLocationPoint);
                    positions.add(destinationPoint);
                    map.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(positions), true, 100, 17, 1500L);
                }
                map.invalidate();   // MapView aktualisieren
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }

    public String[] getAddressFields(String address)
    {
        String[] felder = new String[4];
        if (address.contains(",")) {
            int index = address.indexOf(",");
            String streetandnr = address.substring(0, index).trim();
            for(int i = 0; i<streetandnr.length(); i++)
            {
                if(Character.isDigit(streetandnr.charAt(i)))
                {
                    felder[0] = streetandnr.substring(0,i-1);
                    felder[1] = streetandnr.substring(i);
                    break;
                }
            }
            String postcodeandcity = address.substring(index + 1, address.length()).trim();
            int i = postcodeandcity.indexOf(' ');
            felder[2] = postcodeandcity.substring(0,i-1);
            felder[3] = postcodeandcity.substring(i);
            return felder;
        }
        return null;
    }

    private boolean isValidAddress(String address)
    {
        return address.contains(",");
    }
}