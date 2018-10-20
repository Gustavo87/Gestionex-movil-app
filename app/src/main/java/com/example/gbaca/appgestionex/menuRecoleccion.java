package com.example.gbaca.appgestionex;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class menuRecoleccion extends AppCompatActivity {

    private LocationManager manager;
    private LocationListener locationListener;
//    private String archivo1 = "";
//    private String archivo2 = "";
//    private int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_recoleccion);

//        final TextView texto2 = (TextView) findViewById(R.id.texto2);
//        texto2.setMovementMethod(new ScrollingMovementMethod());

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //get the latitude and longitude from the location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get the location name from latitude and longitude
//                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
///                    List<Address> addresses =
//                            geocoder.getFromLocation(latitude, longitude, 1);
//                    String result = addresses.get(0).getSubLocality()+":";
//                    result += addresses.get(0).getLocality()+":";
//                    result += addresses.get(0).getCountryCode();
//                    LatLng latLng = new LatLng(latitude, longitude);
 //                   contador++;
 //                   String cadena = texto2.getText() + "\n";
 //                   cadena += contador + ".Latitud y Longitud" + latLng.toString() + "\n";
 //                   cadena += result;
 //                   texto2.setText(cadena);

 //                   archivo1 += "{lat: " + latitude + ", lng: " + longitude + "},";
 //                   archivo2 += cadena;

//                    mMap.addMarker(new MarkerOptions().position(latLng).title(result));
//                    mMap.setMaxZoomPreference(20);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    /** Codigo para prueba de estress  **/
                    String cad = "{lat: " + latitude + ", lng: " + longitude + "},";
                    generateNoteOnSD(getApplicationContext(),"archivo-de-coordenadas-pe.txt",cad);
                    /** Fin de codigo prueba de estress**/
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(locationListener);
        Log.i("onPause...","paused");
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, sBody, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    public void guardar(View view) {

    //    generateNoteOnSD(getApplicationContext(),"archivo-de-coordenadas.txt",archivo1);
    //    generateNoteOnSD(getApplicationContext(),"archivo-de-direcciones.txt",archivo2);
    }
*/


}
