package com.example.gbaca.appgestionex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.support.annotation.IntegerRes;
import android.support.annotation.RequiresApi;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class mdisposicion extends AppCompatActivity {

    private FusedLocationProviderClient client;
    Context contexto;
    private BaseDatos db;
    Gson gson;

    public void verDisposicionesPendientes(){

        ArrayList disposiciones = db.obtenerDisposicionNoEnviadas();
        TextView disposicionesPendientes = (TextView) findViewById(R.id.disposicionesPendientes);
        if(disposiciones.size()>0){
            disposicionesPendientes.setText("Hay "+disposiciones.size()+" disposiciones pendientes. Si ya termino operaciones, favor, pulse en la imagen para enviarlas");
        }else{
            disposicionesPendientes.setText("");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        verDisposicionesPendientes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdisposicion);

        db = new BaseDatos(getApplicationContext());
        gson=new GsonBuilder().serializeNulls().create();

        contexto = this;
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        if(ObtenerDato(Constantes.PREF_FILE_KEY_INICIO_DISPOSICION).equals(Constantes.DATO_ENVIADO)){
            desabilitarBoton(R.id.btnInicioDisposicion);
        }

        if(ObtenerDato(Constantes.PREF_FILE_KEY_FIN_DISPOCION).equals(Constantes.DATO_ENVIADO)){
            desabilitarBoton(R.id.btnFinDisposicion);
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }

    public void MandarUbicacion() {

        if (ActivityCompat.checkSelfPermission(mdisposicion.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(mdisposicion.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                Log.i("Log","Eventosss");
                Log.i("Log", "Datos: "+ location);
                if(location!=null){
                    String mensaje ="Latitud: " + location.getLatitude() + "\n" + "Longitud: " + location.getLongitude();
                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                }

            }
        });


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


    public String fecha_actual(){

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        }else{
//            Toast.makeText(contexto,"El sistema detecto que su version de android es muy baja, favor actualice", Toast.LENGTH_LONG).show();
//        }
//        return "";
    }

    public void InicioDisposicion(View view) {

        final String fecha_inicial = fecha_actual();
        String url = null;
        try {
            url = "https://www.app.gestionex.co/guardar_inicio_disposicion.php?id_usuario=" + ObtenerIdUsuario() + "&fecha_inicio=" + URLEncoder.encode(fecha_inicial, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("url",url);
        new ConexionHTTP(contexto,getString(R.string.enviando_msg),url, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {

            // Interface para tomar el resultado del GET al servicioUbicacion de Autenticación
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void doProcess(String output) {
                Log.i("Dato",output);
                output = LimpiarString(output);
                if(Long.valueOf(output)>0){
                    guardar_id_disposicion(output);
                    // Creamos el objeto en memoria
                    disposicion dispo = new disposicion();
                    dispo.setId_servicio(Integer.parseInt(output));
                    dispo.setFecha_inicial(fecha_inicial);
                    dispo.setId_usuario(Integer.parseInt(ObtenerIdUsuario()));
                    db.insertar_disposicion(dispo);

                    marcarDato(Constantes.PREF_FILE_KEY_INICIO_DISPOSICION, Constantes.DATO_ENVIADO);
                    desabilitarBoton(R.id.btnInicioDisposicion);
                }else{
                    Toast.makeText(getApplicationContext(),"Problema de red, favor, intente otra vez",Toast.LENGTH_SHORT).show();
                }

            }
        }).execute();

    }



    public void FinDisposicion(View view) {

        if("".equals(Obtener_id_disposicion()) || "".equals(Obtener_toneladas()) || "".equals(Obtener_lugar())){
            Toast.makeText(contexto, getString(R.string.sin_id_disposicion_msg), Toast.LENGTH_SHORT).show();
        }else{

            Log.i("Datos",Obtener_lugar());
            Log.i("Datos",Obtener_toneladas());

            disposicion dispo = new disposicion();
            dispo.setId_servicio(Integer.parseInt(Obtener_id_disposicion()));
            dispo.setFecha_final(fecha_actual());
            dispo.setToneladas(Float.parseFloat(Obtener_toneladas()));
            dispo.setLugar(Obtener_lugar());

            Log.i("Datos",dispo.getToneladas().toString());
            Log.i("Datos",dispo.getLugar());


            db.cerrar_disposicion(dispo);

            ArrayList disposiciones = db.obtenerDisposicionNoEnviadas();
            String disposiciones_string = gson.toJson(disposiciones);

            Log.i("arreglo",disposiciones_string);



            String url = "https://www.app.gestionex.co/guardar_fin_disposicion.php";
            Log.i("url",url);
            new ConexionHTTP(contexto,"Enviando",url, Constantes.POST, disposiciones_string, new ConnectionInterface() {

                // Interface para tomar el resultado del POST al servicio de Enviar Apuntes
                @Override
                public void doProcess(String output) {

                    if(output!=null){

                        String respuesta = LimpiarString(output);
                        Log.i("Respuesta",respuesta);

                        String[] parts = respuesta.split("\\|");
                        Log.i("respuesta",String.valueOf(parts.length));

                        for (int i = 0; i < parts.length; i++){

                            Log.i("respuesta",parts[i]);
                            db.disposicion_enviada(Integer.parseInt(parts[i]));
                        }


                    }

                    verDisposicionesPendientes();

                    guardar_id_disposicion("");
                    guardar_datos_disposicion("","");
                    marcarDato(Constantes.PREF_FILE_KEY_INICIO_DISPOSICION, Constantes.DATO_NO_ENVIADO);
                    habilitarBoton(R.id.btnInicioDisposicion);

                }
            }).execute();


            //MandarUbicacion();
//            String url = "https://www.app.gestionex.co/guardar_fin_disposicion.php?id_disposicion=" + Obtener_id_disposicion() + "&toneladas=" + Obtener_toneladas() + "&lugar=" + Obtener_lugar();
//            new ConexionHTTP(contexto,getString(R.string.enviando_msg),url, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {
//
//                // Interface para tomar el resultado del GET al servicioUbicacion de Autenticación
//                @Override
//                public void doProcess(String output) {
//                    output = LimpiarString(output);
//                    if(output.equals(Obtener_id_disposicion())){
//                        guardar_id_disposicion("");
//                        guardar_datos_disposicion("","");
//                        marcarDato(Constantes.PREF_FILE_KEY_INICIO_DISPOSICION, Constantes.DATO_NO_ENVIADO);
//                        habilitarBoton(R.id.btnInicioDisposicion);
//                    }else{
//                        Toast.makeText(getApplicationContext(),"Problema de red, favor, intente otra vez",Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }).execute();
        }
    }

    public void guardar_datos_disposicion(String toneladas,String lugar){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constantes.PREF_FILE_KEY_TONELADAS,toneladas);
        editor.putString(Constantes.PREF_FILE_KEY_LUGAR,lugar);
        editor.apply();

    }

    public void IngresarDatosDisposicion(View view) {

        Intent menu = new Intent(getApplicationContext(), ingresardatos.class);
        startActivity(menu);
        overridePendingTransition(R.anim.goup,R.anim.godown);

    }

    public  String Obtener_id_disposicion(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_ID_DISPOSICION,"");
    }

    public  String Obtener_toneladas(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_TONELADAS,"");
    }

    public  String Obtener_lugar(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_LUGAR,"");
    }

    public  String ObtenerIdUsuario(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_ID_USUARIO,"");
    }

    public void guardar_id_disposicion(String id){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constantes.PREF_FILE_KEY_ID_DISPOSICION,id);
        editor.apply();

    }

    public String LimpiarString(String texto){

        texto=texto.replace("\"","");
        texto=texto.replace("\n","");
        //Remueve todos los caracteres de control del string
        texto=texto.replaceAll("[\u0000-\u001f]", "");
        return texto;
    }

    public void atras(View view) {

        //super.onBackPressed();
        Intent menu = new Intent(getApplicationContext(), mprincipal.class);
        startActivity(menu);
        overridePendingTransition(R.anim.goup,R.anim.godown);
        //overridePendingTransition(R.anim.goup,R.anim.godown);

    }

    // Cuando pulsamos Back Button
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        // Quitamos la funcionalidad Back Button
        return false;
    }

    public void enviarDisposicionesPendientes(View view) {

        ArrayList disposiciones = db.obtenerDisposicionNoEnviadas();
        String disposiciones_string = gson.toJson(disposiciones);

        Log.i("arreglo",disposiciones_string);


        String url = "https://www.app.gestionex.co/guardar_fin_disposicion.php";
        Log.i("url",url);
        new ConexionHTTP(contexto,"Enviando",url, Constantes.POST, disposiciones_string, new ConnectionInterface() {

            // Interface para tomar el resultado del POST al servicio de Enviar Apuntes
            @Override
            public void doProcess(String output) {

                if(output!=null){

                    String respuesta = LimpiarString(output);
                    Log.i("Respuesta",respuesta);

                    String[] parts = respuesta.split("\\|");
                    Log.i("respuesta",String.valueOf(parts.length));

                    for (int i = 0; i < parts.length; i++){

                        Log.i("respuesta",parts[i]);
                        db.disposicion_enviada(Integer.parseInt(parts[i]));
                    }


                }

                verDisposicionesPendientes();

                guardar_id_disposicion("");
                guardar_datos_disposicion("","");
                marcarDato(Constantes.PREF_FILE_KEY_INICIO_DISPOSICION, Constantes.DATO_NO_ENVIADO);
                habilitarBoton(R.id.btnInicioDisposicion);

            }
        }).execute();


    }
}
