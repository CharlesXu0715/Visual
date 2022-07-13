package com.example.visual;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
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
import com.google.android.gms.maps.model.Marker;
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
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private static final String TAG = "TAG";
    private static final int FILE_SELECTOR_CODE = 42;
    private final static String mLogTag = "GeoJsonVisualise";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView mGreetingTextView;
    private Button mImportButton;
    private Button mDeleteButton;
    private GeoJsonLayer layer;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestReadPermission();    //request permission for load local json files

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
        mDeleteButton.setOnClickListener(this);

    }

    //create geojson layer via the file local and load it on the map
    private void createLayer() {
        try {
            //load file stocked in raw
            layer = new GeoJsonLayer(mMap, R.raw.geojsonfile1, this);
            addGeoJsonLayerToMap(layer);
        } catch (IOException e) {
            Log.e(mLogTag, "GeoJSON file could not be read");
        } catch (JSONException e) {
            Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
        }
        Log.i("IMPORT","success");

        //move center of the map to the first point
        String lat="",lng="";
        int count=0;
        for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature != null) {
                String geo = feature.getGeometry().getGeometryObject().toString();
                lat = geo.substring(geo.indexOf("(")+1,geo.indexOf(","));
                lng = geo.substring(geo.indexOf(",")+1,geo.indexOf(")"));
                LatLng firstPoint = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
                mMap.addMarker(new MarkerOptions().position(firstPoint).title("Marker of Point No"+count));
                if (count == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(firstPoint));
                }
                count++;
            }
        }
    }

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {
        addColorToLayer(layer);
        layer.addLayerToMap();
        //Demonstrate receiving features via GeoJsonLayer clicks
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Toast.makeText(MapsActivity.this,
                        "Commune:" + feature.getProperty("commune"),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addColorToLayer(GeoJsonLayer layer) {
        GeoJsonPolygonStyle geoJsonPolygonStyle = layer.getDefaultPolygonStyle();
        geoJsonPolygonStyle.setStrokeWidth(10);
        geoJsonPolygonStyle.setStrokeColor(Color.RED);
    }

    @Override
    public void onClick(View view) {
        if (view == mImportButton) {
            //from files in raw
            createLayer();

            //from files in storage
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("*/*");
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            startActivityForResult(intent, FILE_SELECTOR_CODE);

            mImportButton.setEnabled(false);
            mDeleteButton.setEnabled(true);
        }
        else if (view == mDeleteButton) {
            layer.removeLayerFromMap();
            mMap.clear();
            mDeleteButton.setEnabled(false);
            mImportButton.setEnabled(true);
        }
    }

    private void requestReadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //request
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: permission granted");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECTOR_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Toast.makeText(this, "path：" + uri.toString(), Toast.LENGTH_SHORT).show();
            filePath=uri.getPath().toString();
            Log.e("path",filePath);
            try {
                FileInputStream fin = new FileInputStream(filePath);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                String content = new String(buffer);
                JSONObject jsonObject=new JSONObject(content);
                layer = new GeoJsonLayer(mMap,jsonObject);
                addGeoJsonLayerToMap(layer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}