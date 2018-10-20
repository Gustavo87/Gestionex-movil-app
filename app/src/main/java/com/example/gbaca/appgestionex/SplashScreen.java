package com.example.gbaca.appgestionex;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        contexto = this;
        Thread mythread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    // Verificamos si las credenciales están guardadas
                    boolean hayCredencialesGuardadas = !ObtenerCredenciales(contexto).getUser().equals(Constantes.SIN_CREDENCIALES);

                    // Si hay credenciales guardadas abrimos la pantalla principal
                    if(hayCredencialesGuardadas){
                        Intent intent=new Intent(contexto,mprincipal.class);
                        startActivity(intent);
                    }
                    else{
                        Intent menu = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(menu);
                    }
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mythread.start();

    }

    public static Credenciales ObtenerCredenciales(Context contexto_actual){

        Credenciales credenciales=new Credenciales();
        SharedPreferences sharedPreferences=contexto_actual.getSharedPreferences(contexto_actual.getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        credenciales.setUser(sharedPreferences.getString(Constantes.PREF_FILE_KEY_NAME,Constantes.SIN_CREDENCIALES));
        credenciales.setPassword(sharedPreferences.getString(Constantes.PREF_FILE_KEY_PASSWORD,Constantes.SIN_CREDENCIALES));
        return credenciales;
    }

}
