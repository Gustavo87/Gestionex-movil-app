package com.example.gbaca.appgestionex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class mrecoleccion extends AppCompatActivity {

    private FusedLocationProviderClient client;
    Context contexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrecoleccion);
        contexto = this;
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);

        if(ObtenerDato(Constantes.PREF_FILE_KEY_INICIO_RECOLECCION).equals(Constantes.DATO_ENVIADO)){
            desabilitarBoton(R.id.btnInicioRecoleccion);
        }

        if(ObtenerDato(Constantes.PREF_FILE_KEY_FIN_RECOLECCION).equals(Constantes.DATO_ENVIADO)){
            desabilitarBoton(R.id.btnFinRecoleccion);
        }

    }

    public void marcarDato(String dato,String estado){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(dato,estado);
        editor.apply();

    }

    public void desabilitarBoton(int id){
        Button btn = (Button) findViewById(id);
        btn.setEnabled(false);
        btn.setTextColor(Color.parseColor("#cbcbcb"));
    }

    public void habilitarBoton(int id){
        Button btn = (Button) findViewById(id);
        btn.setEnabled(true);
        btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }

    public String ObtenerDato(String dato){

        SharedPreferences sharedPreferences= getSharedPreferences( getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(dato,Constantes.DATO_NO_ENVIADO);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }

    public void InicioRecoleccion(View view) {

        final String fecha_inicial = fecha_actual();
        String url = null;
        try {
            url = "https://www.app.gestionex.co/guardar_inicio_recoleccion.php?id_usuario=" + ObtenerIdUsuario()+ "&fecha_inicio=" + URLEncoder.encode(fecha_inicial, "utf-8");
            Log.i("url",url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new ConexionHTTP(contexto,getString(R.string.enviando_msg),url, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {

            // Interface para tomar el resultado del GET al servicioUbicacion de Autenticación
            @Override
            public void doProcess(String output) {
                output = LimpiarString(output);
                if(Long.valueOf(output)>0){
                    guardar_id_recoleccion(LimpiarString(output));
                    marcarDato(Constantes.PREF_FILE_KEY_INICIO_RECOLECCION, Constantes.DATO_ENVIADO);
                    desabilitarBoton(R.id.btnInicioRecoleccion);
                }else{
                    Toast.makeText(getApplicationContext(),"Problema de red, favor, intente otra vez",Toast.LENGTH_SHORT).show();
                }

            }
        }).execute();

    }

    public void FinRecoleccion(View view) {

        if("".equals(Obtener_id_recoleccion())){
            Toast.makeText(contexto, getString(R.string.sin_id_recoleccion_msg), Toast.LENGTH_SHORT).show();
        }else{
            final String fecha_final = fecha_actual();
            String url = null;
            try {
                url = "https://www.app.gestionex.co/guardar_fin_recoleccion.php?id_recoleccion=" + Obtener_id_recoleccion()+ "&fecha_final=" + URLEncoder.encode(fecha_final, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ;
            new ConexionHTTP(contexto,getString(R.string.enviando_msg),url, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {

                // Interface para tomar el resultado del GET al servicioUbicacion de Autenticación
                @Override
                public void doProcess(String output) {

                    output = LimpiarString(output);
                    if(output.equals(Obtener_id_recoleccion())){
                        guardar_id_recoleccion("");
                        marcarDato(Constantes.PREF_FILE_KEY_INICIO_RECOLECCION, Constantes.DATO_NO_ENVIADO);
                        habilitarBoton(R.id.btnInicioRecoleccion);
                    }else{
                        Toast.makeText(getApplicationContext(),"Problema de red, favor, intente otra vez",Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute();
        }

    }

    public void guardar_id_recoleccion(String id){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constantes.PREF_FILE_KEY_ID_RECOLECCION,id);
        editor.apply();

    }

    public  String Obtener_id_recoleccion(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_ID_RECOLECCION,"");
    }

    public  String ObtenerIdUsuario(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_ID_USUARIO,"");
    }

    public String fecha_actual(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String LimpiarString(String texto){

        texto=texto.replace("\"","");
        texto=texto.replace("\n","");
        //Remueve todos los caracteres de control del string
        texto=texto.replaceAll("[\u0000-\u001f]", "");
        return texto;
    }

    public void MandarUbicacion() {

        if (ActivityCompat.checkSelfPermission(mrecoleccion.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(mrecoleccion.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location!=null){
                    String mensaje ="Latitud: " + location.getLatitude() + "\n" + "Longitud: " + location.getLongitude();
                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void atras(View view) {

        super.onBackPressed();
        overridePendingTransition(R.anim.goup,R.anim.godown);

    }

    // Cuando pulsamos Back Button
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        // Quitamos la funcionalidad Back Button
        return false;
    }


}
