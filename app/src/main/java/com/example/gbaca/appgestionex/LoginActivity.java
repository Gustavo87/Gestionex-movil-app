package com.example.gbaca.appgestionex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    private Context contexto;
    private EditText etUsername, etPassword;
    private TextInputLayout inputLayoutName, inputLayoutPassword;
    private String tvUsuario;
    private String tvPassword;
    private String id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        contexto = this;
        // Seteamos los views
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

    }


    public void Login(View view) {
        //Tomamos las credenciales de la UI
        tvUsuario=etUsername.getText().toString();
        tvPassword=etPassword.getText().toString();
        Log.i("Login",tvUsuario + " " + tvPassword);
        if(EscribioUsuario() && EscribioPassword()){
            try {

//                if ("gestionex".equals(tvUsuario) && "gestionex".equals(tvPassword)) {
//                    inputLayoutName.setErrorEnabled(false);
//                    GuardarCredencialesUsuarioLogeado();
//                    Intent menu = new Intent(getApplicationContext(), mprincipal.class);
//                    startActivity(menu);
//                    overridePendingTransition(R.anim.goup,R.anim.godown);
//
//                } else {
//                    inputLayoutPassword.setError(getString(R.string.credenciales_invalidas_msg));
//                }
                AutenticarUsuario(tvUsuario,tvPassword);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void AutenticarUsuario(String nombre, String clave) {

        //String url = "http://chat-nodejs-20180430.herokuapp.com/login?usuario="+nombre+"&clave=" + clave;
        String url = "https://www.app.gestionex.co/login.php?usuario=" + nombre + "&clave=" + clave;

        Log.i("HTTP",url);
        //Hacemos la peticion HTTP
        new ConexionHTTP(contexto,getString(R.string.autenticando_msg),url, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {

            // Interface para tomar el resultado del GET al servicioUbicacion de Autenticación
            @Override
            public void doProcess(String output) {
                Log.i("Login",output);
                CredencialesValidas(output);
            }
        }).execute();

    }

    public String LimpiarString(String texto){

        texto=texto.replace("\"","");
        texto=texto.replace("\n","");
        //Remueve todos los caracteres de control del string
        texto=texto.replaceAll("[\u0000-\u001f]", "");
        id_usuario=texto;
        return texto;
    }

    private void CredencialesValidas(String login) {

        login=LimpiarString(login);
        //Log.i("Respuesta1",loginExitoso);
        //Log.i("Respuesta2",Constantes.AUTENTICACION_EXITOSA);

        boolean CredencialesNOValidas=new String(Constantes.AUTENTICACION_FALLIDA).equals(login);
        //Log.i("Respuesta3","Res: " + CredencialesValidas);
        if (CredencialesNOValidas) {
            inputLayoutPassword.setError(getString(R.string.credenciales_invalidas_msg));
        } else {
            inputLayoutName.setErrorEnabled(false);
            GuardarCredencialesUsuarioLogeado();
            Intent menu = new Intent(getApplicationContext(), mprincipal.class);
            startActivity(menu);
            overridePendingTransition(R.anim.goup,R.anim.godown);
        }
    }

    private void GuardarCredencialesUsuarioLogeado(){

        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+Constantes.PREF_FILE_CREDENCIALES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(Constantes.PREF_FILE_KEY_NAME,tvUsuario);
        editor.putString(Constantes.PREF_FILE_KEY_PASSWORD,tvPassword);
        editor.putString(Constantes.PREF_FILE_KEY_ID_USUARIO,id_usuario);
        editor.apply();

    }


    private boolean EscribioUsuario() {

        try {
            if (tvUsuario.isEmpty()) {
                inputLayoutName.setError(getString(R.string.username_validation_msg));
                return false;
            } else {
                inputLayoutName.setErrorEnabled(false);
                return true;
            }
        }catch (Exception e){
            Log.i("Ex",e.getMessage());
            return false;
        }

    }

    private boolean EscribioPassword() {

        if (tvPassword.isEmpty() ) {
            inputLayoutPassword.setError(getString(R.string.pwd_validation_msg));
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
            return true;
        }
    }


}
