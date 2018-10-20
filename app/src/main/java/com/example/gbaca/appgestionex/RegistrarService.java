package com.example.gbaca.appgestionex;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.gbaca.appgestionex.R.id.log;


public class RegistrarService extends Service
{
    private static Timer timer = new Timer();
    private Context ctx;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        timer = new Timer(); timer.scheduleAtFixedRate(new mainTask(), 0, 60000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy()
    {

        timer.cancel();
        Toast.makeText(this, "Detenido Servicio Guardar.",
                Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            //String mensaje = "A pasado 1 minuto ";
            //SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss") ;
            //mensaje += df.format(new Date());
            //mensaje += " Latitud: " + ObtenerLatitud();
            //mensaje += " Longitud: " + ObtenerLongitud();
            //mensaje += "\n";
            //Toast.makeText(getApplicationContext(), "A pasado 1 minuto", Toast.LENGTH_SHORT).show();
            //generateNoteOnSD("minutox1-gestionex.txt",mensaje);
            enviarPosicion();
        }
    };

    public String fecha_actual(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private void enviarPosicion() {

//
//        BaseDatos db = new BaseDatos(getApplicationContext());
//
//        ArrayList rastreos = db.obtenerRastreosNoEnviados();
//        Gson gson=new GsonBuilder().serializeNulls().create();
//        String rastreos_string = gson.toJson(rastreos);
//
//        Log.i("arreglo",rastreos_string);
//
//
//
//        String url = "http://www.app.gestionex.co/guardar_rastreo.php";
//        Log.i("url",url);
//        new ConexionHTTP(ctx,"Enviando",url, Constantes.POST, rastreos_string, new ConnectionInterface() {
//
//            // Interface para tomar el resultado del POST
//            @Override
//            public void doProcess(String output) {
//
//                if(output!=null){
//
//                    String respuesta = LimpiarString(output);
//                    Log.i("Respuesta",respuesta);
//
////                    String[] parts = respuesta.split("\\|");
////                    Log.i("respuesta",String.valueOf(parts.length));
////
////                    for (int i = 0; i < parts.length; i++){
////
////                        Log.i("respuesta",parts[i]);
////                        db.disposicion_enviada(Integer.parseInt(parts[i]));
////                    }
//
//
//                }
//
//            }
//        }).execute();

//        String latitud    =  ObtenerLatitud(); //"12.438448";
//        String longitud   = ObtenerLongitud(); //"-86.880546";
//        String id_usuario = ObtenerIdUsuario();
//
//        if(new String("").equals(latitud) && new String("").equals(longitud)){
//
//            Toast.makeText(getApplicationContext(), "Sin datos GPS para enviar!", Toast.LENGTH_SHORT).show();
//
//        }else{
//            final String fecha_envio = fecha_actual();
//            String url_rastreo = "";
//            try {
//                url_rastreo = "http://www.app.gestionex.co/guardar_rastreo.php?id_usuario=" + id_usuario + "&latitud=" + latitud + "&longitud=" + longitud+ "&fecha_envio=" + URLEncoder.encode(fecha_envio, "utf-8");
//                Log.i("url",url_rastreo);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            final String servicio = url_rastreo;
//            Toast.makeText(getApplicationContext(), "Latitud: " + latitud + " Longitud: " + longitud, Toast.LENGTH_SHORT).show();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        URL url = new URL(servicio);
//                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                        urlConnection.setRequestMethod("GET");
//                        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
//                        InputStream stream = urlConnection.getInputStream();
//                        InputStreamReader reader = new InputStreamReader(stream);
//                        BufferedReader bufferedReader = new BufferedReader(reader);
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            System.out.println(line);
//                        }
//                        bufferedReader.close();
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }

        /*
        new ConexionHTTP(getApplicationContext(),getString(R.string.autenticando_msg),url, Constantes.GET, Constantes.CADENA_VACIA, new ConnectionInterface() {
            @Override
            public void doProcess(String output) {
               Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
            }
        }).execute();*/

    }

    public String LimpiarString(String texto){

        texto=texto.replace("\"","");
        texto=texto.replace("\n","");
        //Remueve todos los caracteres de control del string
        texto=texto.replaceAll("[\u0000-\u001f]", "");
        return texto;
    }

    public  String ObtenerIdUsuario(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_ID_USUARIO,"");
    }

    public  String ObtenerLatitud(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_LATITUD,"");
    }

    public  String ObtenerLongitud(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName()+Constantes.PREF_FILE_CREDENCIALES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constantes.PREF_FILE_KEY_LONGITUD,"");
    }

    public void generateNoteOnSD(String sFileName, String sBody) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

