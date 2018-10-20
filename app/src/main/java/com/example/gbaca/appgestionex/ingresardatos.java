package com.example.gbaca.appgestionex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ingresardatos extends AppCompatActivity {

    Context contexto;
    private EditText etTonelada, etLugar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresardatos);
        contexto = this;
        etTonelada = (EditText) findViewById(R.id.etTonelada);
        etLugar = (EditText) findViewById(R.id.etLugar);
    }

    public void IngresarDatos(View view) {

        String toneladas =etTonelada.getText().toString();
        String lugar =etLugar.getText().toString();
        guardar_datos_disposicion(toneladas,lugar);
        Intent menu = new Intent(getApplicationContext(), mdisposicion.class);
        startActivity(menu);
        overridePendingTransition(R.anim.goup,R.anim.godown);

    }

    public void guardar_datos_disposicion(String toneladas,String lugar){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constantes.PREF_FILE_KEY_TONELADAS,toneladas);
        editor.putString(Constantes.PREF_FILE_KEY_LUGAR,lugar);
        editor.apply();

    }

    // Cuando pulsamos Back Button
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        // Quitamos la funcionalidad Back Button
        return false;
    }

    public void atras(View view) {

        //super.onBackPressed();
        Intent menu = new Intent(getApplicationContext(), mdisposicion.class);
        startActivity(menu);
        overridePendingTransition(R.anim.goup,R.anim.godown);


    }
}
