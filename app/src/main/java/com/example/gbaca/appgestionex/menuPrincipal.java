package com.example.gbaca.appgestionex;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class menuPrincipal extends AppCompatActivity {

    private FusedLocationProviderClient client;
    LocationRequest mLocationRequest = new LocationRequest(); // nueva
    private LocationCallback mLocationCallback; //nueva
    private int contador = 0;
    private String contenidoArchivoSinDir = "";
    private String contenidoArchivoConDir = "";
    private String contenidoArchivoFull = "";

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates(); // nueva
    }

    /*nueva*/
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(menuPrincipal.this, ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(mLocationRequest, mLocationCallback,null);
    }
    /*fin-nueva*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

//        generateNoteOnSD(getApplicationContext(),"coordenadas.txt","Prueba 1\n");


        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest.setInterval(10000); // nueva
        mLocationRequest.setFastestInterval(5000); // nueva
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // nueva

        final TextView texto2 = (TextView) findViewById(R.id.texto2);
        texto2.setMovementMethod(new ScrollingMovementMethod());

        /*nueva*/
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    contador++;
                    Geocoder geocoder = new Geocoder(getApplicationContext(),
                            Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                1);


                        //"{lat: 37.772, lng: -122.214},"

                        String dir = "Direccion: " + addresses.get(0).getAddressLine(0) + "\n";
                        dir += "Administracion: " + addresses.get(0).getAdminArea() + "\n";
                        dir +="Localidad: " + addresses.get(0).getLocality() + "\n";
                        dir += "Pais: " + addresses.get(0).getCountryName() + "\n";
                        dir += "Latitud: " + addresses.get(0).getLatitude() + "\n";
                        dir += "Longitud: " + addresses.get(0).getLongitude() + "\n";

//                        String dirNoFormato = "\n\n" + addresses.toString() + "\n";
                        String cadena = texto2.getText() + "\n";
                        cadena += "Medicion " + contador + "\n";
                        cadena += "Latitud: " + location.getLatitude() + "\n";
                        cadena += "Longitud: " + location.getLongitude() + "\n\n";
                        cadena += dir;

                        contenidoArchivoSinDir += "{lat: " + location.getLatitude() + ", lng: " + location.getLongitude() + "},";
                        contenidoArchivoConDir += "{lat: " + addresses.get(0).getLatitude() + ", lng: " + addresses.get(0).getLongitude() + "},";
                        contenidoArchivoFull += cadena;

                        texto2.setText(cadena);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   // texto2.setText(texto2.getText() + "\n" +contador+ ". Latitud: " + location.getLatitude() + "\n" + "Longitud: " + location.getLongitude() + "\n");
//                    Toast.makeText(getApplicationContext(),"cambio",
//                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        /*nueva-fin*/
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //nueva
        stopLocationUpdates();
    }

    /*nueva*/
    private void stopLocationUpdates() {
        client.removeLocationUpdates(mLocationCallback);
    }
    /*fin-nueva*/

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }

    public void Ubicar(View view) {

        if (ActivityCompat.checkSelfPermission(menuPrincipal.this, ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(menuPrincipal.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location!=null){
                    TextView texto = (TextView) findViewById(R.id.texto);
                    texto.setText("Latitud: " + location.getLatitude() + "\n" + "Longitud: " + location.getLongitude());
                }

            }
        });


    }

    public void guardar(View view) {

        generateNoteOnSD(getApplicationContext(),"coordenadasSinDir.txt",contenidoArchivoSinDir);
        generateNoteOnSD(getApplicationContext(),"coordenadasConDir.txt",contenidoArchivoConDir);
        generateNoteOnSD(getApplicationContext(),"coordenadasFull.txt",contenidoArchivoFull);

    }

    public void ir(View view) {
        Intent menu = new Intent(getApplicationContext(), menuRecoleccion.class);
        startActivity(menu);
    }




}
