package com.example.visual;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.visual.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView mGreetingTextView;
    private Button mImportButton;
    private Button mDeleteButton;
    private GeoJsonLayer layer;

    private final static String mLogTag = "GeoJsonVisualise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mGreetingTextView = findViewById(R.id.maps_textview_greeting);
        mImportButton = findViewById(R.id.maps_button_import);
        mDeleteButton = findViewById(R.id.maps_button_delete);
        mImportButton.setEnabled(false);
        mDeleteButton.setEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mImportButton.setEnabled(true);
        mImportButton.setOnClickListener(this);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    //create geojson layer via the file local and load it on the map
    private void createLayer() {
        try {
            layer = new GeoJsonLayer(mMap, R.raw.geojsonfile_69, this);
            GeoJsonPolygonStyle geoJsonPolygonStyle = layer.getDefaultPolygonStyle();
            geoJsonPolygonStyle.setStrokeWidth(10);
            geoJsonPolygonStyle.setStrokeColor(Color.RED);
            addGeoJsonLayerToMap(layer);

        } catch (IOException e) {
            Log.e(mLogTag, "GeoJSON file could not be read");
        } catch (JSONException e) {
            Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
        }
        Log.i("IMPORT","success");
        String lat="",lng="";
        for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature != null) {
                String geo = feature.getGeometry().getGeometryObject().toString();
                lat = geo.substring(geo.indexOf("(")+1,geo.indexOf(","));
                lng = geo.substring(geo.indexOf(",")+1,geo.indexOf(")"));
                break;
            }
        }
        //move center of the map to the first point
        LatLng firstPoint = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstPoint));

        mDeleteButton.setOnClickListener(this);
    }

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {

//        addColorsToMarkers(layer);
        layer.addLayerToMap();
        // Demonstrate receiving features via GeoJsonLayer clicks.
//        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
//            @Override
//            public void onFeatureClick(Feature feature) {
//                Toast.makeText(MapsActivity.this,
//                        "Feature clicked: " + feature.getProperty("title"),
//                        Toast.LENGTH_SHORT).show();
//            }
//
//        });
    }

    private void addColorsToMarkers(GeoJsonLayer layer) {

        for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature.getGeometry()!=null) {
                Log.e("info",feature.getGeometry().toString());
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mImportButton) {
            createLayer();
            mImportButton.setEnabled(false);
            mDeleteButton.setEnabled(true);
        }
        else if (view == mDeleteButton) {
            layer.removeLayerFromMap();
            mDeleteButton.setEnabled(false);
            mImportButton.setEnabled(true);
        }
    }
}