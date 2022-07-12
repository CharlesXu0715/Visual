package com.example.visual;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.geojsonfile1, this);

//// Create a new feature containing a linestring
//            List<LatLng> lineStringArray = new ArrayList<LatLng>();
//            lineStringArray.add(new LatLng(0, 0));
//            lineStringArray.add(new LatLng(50, 50));
//            GeoJsonLineString lineString = new GeoJsonLineString(lineStringArray);
//            GeoJsonFeature lineStringFeature = new GeoJsonFeature(lineString, null, null, null);
//
//// Set the color of the linestring to red
//            GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
//            lineStringStyle.setColor(Color.RED);
//
//// Set the style of the feature
//            lineStringFeature.setLineStringStyle(lineStringStyle);

//            for (GeoJsonFeature feature : layer.getFeatures()) {
//                GeoJsonPolygonStyle polygonStyle = new GeoJsonPolygonStyle();
//                polygonStyle.setFillColor(android.graphics.Color.RED);
//                polygonStyle.setStrokeColor(android.graphics.Color.RED);
//                feature.setPolygonStyle(polygonStyle);
//            }
//            GeoJsonPolygonStyle polygonStyle = new GeoJsonPolygonStyle();
//            polygonStyle.setFillColor(android.graphics.Color.RED);
//            polygonStyle.setStrokeColor(android.graphics.Color.RED);
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("IMPORT","success");



        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}