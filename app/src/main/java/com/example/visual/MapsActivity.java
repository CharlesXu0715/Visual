package com.example.visual;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private static final String TAG = "TAG";
    private static final int FILE_SELECTOR_CODE = 10005;
    private final static String mLogTag = "GeoJsonVisualise";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView mGreetingTextView;
    private Button mImportButton;
    private Button mClearButton;
    private GeoJsonLayer layer;
    private String filePath;
    private List<GeoJsonLayer> mGeoJsonLayers = new ArrayList<GeoJsonLayer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestReadPermission();    //request permission for load local json files

        mGreetingTextView = findViewById(R.id.maps_textview_greeting);
        mImportButton = findViewById(R.id.maps_button_import);
        mClearButton = findViewById(R.id.maps_button_clear);
        mImportButton.setEnabled(false);
        mClearButton.setEnabled(false);

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
        mClearButton.setOnClickListener(this);

    }

    //create geojson layer via the file local and load it on the map
    private void addMarker() {
//        try {
//            //load file stocked in raw
//            layer = new GeoJsonLayer(mMap, R.raw.geojsonfile1, this);
//            addGeoJsonLayerToMap(layer);
//        } catch (IOException e) {
//            Log.e(mLogTag, "GeoJSON file could not be read");
//        } catch (JSONException e) {
//            Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
//        }
//        Log.i("IMPORT", "success");

        //move center of the map to the first point
        String lat = "", lng = "";
        int count = 0;
        for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature != null) {
                String geo = feature.getGeometry().getGeometryObject().toString();
                lat = geo.substring(geo.indexOf("(") + 1, geo.indexOf(","));
                lng = geo.substring(geo.indexOf(",") + 1, geo.indexOf(")"));
                LatLng firstPoint = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                mMap.addMarker(new MarkerOptions().position(firstPoint).title("Marker of Point No" + count));
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
//            //from files in raw
//            try {
//                //load file stocked in raw
//                layer = new GeoJsonLayer(mMap, R.raw.geojsonfile1, this);
//                addGeoJsonLayerToMap(layer);
//            } catch (IOException e) {
//                Log.e(mLogTag, "GeoJSON file could not be read");
//            } catch (JSONException e) {
//                Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
//            }
//            Log.i("IMPORT", "success");
//            addMarker();


            //from files in storage
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, FILE_SELECTOR_CODE);



//            mImportButton.setEnabled(false);
            mClearButton.setEnabled(true);
        } else if (view == mClearButton) {
            for (GeoJsonLayer l : mGeoJsonLayers) {
                l.removeLayerFromMap();
            }
            mMap.clear();

            mClearButton.setEnabled(false);
//            mImportButton.setEnabled(true);
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
            Toast.makeText(this, "uri:" + uri.toString(), Toast.LENGTH_SHORT).show();
            filePath = uri.getPath().toString();
            Log.e("path", filePath);
            if (uri != null) {
                Log.e("1", "");
                String path = getFileAbsolutePath(this, uri);
                if (path != null) {
                    Log.e("2", "");
                    File file = new File(path);
                    if (file.exists()) {
                        Log.e("3", "");
                        String upLoadFilePath = file.toString();
                        String upLoadFileName = file.getName();
                        Log.e("filepath", upLoadFilePath);
                        Log.e("filename", upLoadFileName);
                        String content  = "";
                        if (file.getName().endsWith(".json")) {//make sure json files
                            try {
                                InputStream instream = new FileInputStream(file);
                                if (instream != null) {
                                    InputStreamReader inputreader = new InputStreamReader(instream, "GBK");
                                    BufferedReader buffreader = new BufferedReader(inputreader);
                                    String line="";
                                    //read them line by line!
                                    while (( line = buffreader.readLine()) != null) {
                                        content += line + "\n";
                                    }
                                    instream.close();

                                    JSONObject jsonObject=new JSONObject(content);
                                    layer = new GeoJsonLayer(mMap,jsonObject);
                                    addGeoJsonLayerToMap(layer);
                                    mGeoJsonLayers.add(layer);
                                    addMarker();
                                }
                            }
                            catch (java.io.FileNotFoundException e) {
                                Log.d("TestFile-eee", "The File doesn't not exist.");
                            }
                            catch (IOException e)  {
                                Log.d("TestFile-eee", e.getMessage());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(this, "Not a json file!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    public static String getFileAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return getRealFilePath(context, imageUri);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return uriToFileApiQ(context,imageUri);
        }
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    //此方法 只能用于4.4以下的版本
    private static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * Android 10 以上适配 另一种写法
     * @param context
     * @param uri
     * @return
     */
    private static String getFileFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return filePath;
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    /**
     * Android 10 以上适配
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }


}