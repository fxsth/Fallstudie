package com.example.boxbase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetPointOnMapActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    LocationManager locationManager;
    GeoPoint ownLocation;
    IMapController mapController;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        setContentView(R.layout.activity_set_point_on_map);


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

        Button button_discard = findViewById(R.id.button_discard);
        Button button_location_confirm = findViewById(R.id.button_location_confirm);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);

        button_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPointOnMapActivity.this.finish();
            }
        });

        Marker desiredAddressMarker = new Marker(map);
        button_location_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desiredAddress = box_street.getText().toString() + " " +
                        box_number.getText().toString() + ", " +
                        box_postcode.getText().toString() + " " +
                        box_city.getText().toString();
                GeocoderNominatim geocoderNominatim = new GeocoderNominatim("TestUserAgent");
                List<Address> addresses = null;
                try {
                    addresses = geocoderNominatim.getFromLocationName(desiredAddress, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!addresses.isEmpty()) {
                    GeoPoint desiredAddressPoint = new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    desiredAddressMarker.setPosition(desiredAddressPoint);
                    desiredAddressMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    desiredAddressMarker.setIcon(getResources().getDrawable(R.drawable.icon_location_green));
                    desiredAddressMarker.setTitle("Some Point to show it's working");
                    map.getOverlays().add(desiredAddressMarker);
                    ArrayList<GeoPoint> positions = new ArrayList<GeoPoint>();
                    positions.add(ownLocation);
                    positions.add(desiredAddressPoint);
                    map.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(positions), true);
                    map.invalidate();   // MapView aktualisieren
                }
            }
        });

    }//onCreate

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    static int test = 0;
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
        }
        else{
            // Zugriffe bereits erlaubt
            mark_user_location();
        }
    }

    private void mark_user_location()
    {

        Marker userLocation = new Marker(map);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                ownLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.setZoom(16);
                userLocation.setPosition(ownLocation);
                userLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                userLocation.setIcon(getResources().getDrawable(R.drawable.icon_avatar));
                userLocation.setTitle("You are here");
                mapController.setCenter(ownLocation);
                map.getOverlays().add(userLocation);
                locationManager.removeUpdates(this);
                locationManager = null;
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

}

