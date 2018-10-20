package com.example.gbaca.appgestionex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class mprincipal extends AppCompatActivity {

    Context contexto;
    TextView log;
    Intent servicioUbicacion;
    Intent servicioGuardar;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(getApplicationContext(),"Saliendo",Toast.LENGTH_SHORT).show();
    //    Log.i("Actividad","Actividad destruida...");
    //    stopService(servicioUbicacion);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mprincipal);
        // Para inspeccionar la db SQLite desde Chrome
        Stetho.initializeWithDefaults(this);
        contexto = this;
        log = (TextView) findViewById(R.id.log);
        // Iniciamos los servicios
        servicioUbicacion = new Intent(this,MyService.class);
        startService(servicioUbicacion);
        servicioGuardar = new Intent(this, RegistrarService.class);
        startService(servicioGuardar);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText(this, "No hay GPS", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public void Recoleccion(View view) {

        Intent menu = new Intent(getApplicationContext(), mrecoleccion.class);
        startActivity(menu);
        // Animacion
        overridePendingTransition(R.anim.goup,R.anim.godown);


    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public void Disposicion(View view) {

        Intent menu = new Intent(getApplicationContext(), mdisposicion.class);
        startActivity(menu);
        overridePendingTransition(R.anim.goup,R.anim.godown);

    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void Salir(View view) {

        showDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void showDialog() throws Resources.NotFoundException {

        new AlertDialog.Builder(contexto,R.style.DialogoTema)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(contexto.getString(R.string.titulo_confirm_cerrar_app))
                .setMessage(contexto.getString(R.string.mensaje_confirm_cerrar_app))
                .setPositiveButton(contexto.getString(R.string.Aceptar), new DialogInterface.OnClickListener()
                {
                    // Si confirma la alerta cerramos la sesión
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesion();
                    }

                }) // Si cancela la alerta continuamos en la pantalla principal
                .setNegativeButton(contexto.getString(R.string.Cancelar), null)
                .show();

    }

    public  void cerrarSesion(){

        // Detenemos los servicios
        stopService(servicioUbicacion);
        stopService(servicioGuardar);
        // Borramos la credenciales
        BorrarCredenciales();
        // Regresamos a la actividad anterior
        Intent intent = new Intent(this, LoginActivity.class);
        // Este flag borra todas las actividades encima de la actividad destino
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        overridePendingTransition(R.anim.goup,R.anim.godown);

    }

    // Metodos para operar con las SharedPreferences

    public void BorrarCredenciales(){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    // Cuando pulsamos Back Button
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        // Quitamos la funcionalidad Back Button
        return false;
    }

    public String LimpiarString(String texto){

        texto=texto.replace("\"","");
        texto=texto.replace("\n","");
        //Remueve todos los caracteres de control del string
        texto=texto.replaceAll("[\u0000-\u001f]", "");
        return texto;
    }

    public void test(View view) {

        String url_google_api = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=12.431917,-86.870375&destinations=12.432218,-86.869809&key=AIzaSyCT65ymAmhcS9GzeBVFyxj9TGs1pga3_S0";

        new ConexionHTTP(getApplicationContext(),"",url_google_api, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {

            // Interface para tomar el resultado del GET al servicioUbicacion de Autenticación
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void doProcess(String output) {
                Log.i("Dato",output);
                JsonObject jsonObject = new JsonParser().parse(output).getAsJsonObject();
                Log.i("JSON",jsonObject.get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject().get("value").getAsString());


            }
        }).execute();

    }
}
