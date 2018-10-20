package com.example.gbaca.appgestionex;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyService extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0; // 1000 60000
    private static final float LOCATION_DISTANCE = 0; //10f


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;
        public LocationListener(String provider) { mLastLocation = new Location(provider);}
        @Override
        public void onLocationChanged(Location location)
        {
            Toast.makeText(getApplicationContext(), "locationChanged", Toast.LENGTH_SHORT).show();
            if(location!=null){
                mLastLocation.set(location);
                Toast.makeText(getApplicationContext(), "Latitud: " + location.getLatitude() + " Longitud: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                final BaseDatos db = new BaseDatos(getApplicationContext());
                rastreo ras_anterior = db.obtenerUltimoRastreo();
                if(ras_anterior==null){
                    final rastreo ras = new rastreo();
                    ras.setId_remoto(0);
                    ras.setId_usuario(Integer.parseInt(ObtenerIdUsuario()));
                    ras.setFecha(fecha_actual());
                    ras.setLatitud(location.getLatitude());
                    ras.setLongitud(location.getLongitude());
                    ras.setDistancia(0);
                    db.insertar_rastreo(ras);
                }else{
                    final rastreo ras = new rastreo();
                    ras.setId_remoto(0);
                    ras.setId_usuario(Integer.parseInt(ObtenerIdUsuario()));
                    ras.setFecha(fecha_actual());
                    ras.setLatitud(location.getLatitude());
                    ras.setLongitud(location.getLongitude());
                    Location location_anterior = new Location("");
                    location_anterior.setLatitude(ras_anterior.getLatitud());
                    location_anterior.setLongitude(ras_anterior.getLongitud());
                    float Distancia = location.distanceTo(location_anterior);
                    ras.setDistancia(Distancia);
                    db.insertar_rastreo(ras);

                }
                final ArrayList rastreos = db.obtenerRastreosNoEnviados();
                Gson gson=new GsonBuilder().serializeNulls().create();
                String rastreos_string = gson.toJson(rastreos);
                String url = "http://www.app.gestionex.co/guardar_rastreo.php";
                Log.i("url",url);
                new ConexionHTTP(getApplicationContext(),"Enviando",url, Constantes.POST, rastreos_string, new ConnectionInterface() {

                    // Interface para tomar el resultado del POST
                    @Override
                    public void doProcess(String output) {
                        try{
                            if(output!=null){
                                String respuesta = LimpiarString(output);
                                Log.i("Respuesta",respuesta);
                                //Respuesta: 7-533|6-532|5-531|2-528|3-529|4-530|1-527|0-0
                                String[] parts = respuesta.split("\\|");
                                for (int i = 0; i < parts.length; i++){
                                    String[] arreglo = parts[i].split("-");
                                    rastreo ras = new rastreo();
                                    ras.setId_local(Integer.parseInt(arreglo[0]));
                                    ras.setId_remoto(Integer.parseInt(arreglo[1]));
                                    db.marcarRastreoComoEnviado(ras);
                                }
                            }
                        }catch (Exception e){
                        }

                    }
                }).execute();

            }else{
                Toast.makeText(getApplicationContext(),"Sin cobertura de señal GPS",Toast.LENGTH_SHORT).show();
            }

        }

        public String LimpiarString(String texto){

            texto=texto.replace("\"","");
            texto=texto.replace("\n","");
            //Remueve todos los caracteres de control del string
            texto=texto.replaceAll("[\u0000-\u001f]", "");
            return texto;
        }

        public String fecha_actual(){
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

        public  String ObtenerIdUsuario(){

            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
            return sharedPreferences.getString(Constantes.PREF_FILE_KEY_ID_USUARIO,"");
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);

        }
    }

    LocationListener[] mLocationListeners = null;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;//START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            Toast.makeText(getApplicationContext(), "Create", Toast.LENGTH_SHORT).show();
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    new LocationListener(LocationManager.GPS_PROVIDER));

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(getApplicationContext(),"Detenido Servicio Ubicacion",Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
