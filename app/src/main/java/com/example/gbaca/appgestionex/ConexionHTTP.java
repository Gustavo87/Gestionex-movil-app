package com.example.gbaca.appgestionex;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GBaca on 5/12/2017.
 */

public class ConexionHTTP extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private String url;
    private ConnectionInterface connectionInterface;
//    private ProgressDialog progressDialog;
    private String mensajeProgressDialog;
    String method=Constantes.GET;
    String parametros_post;

    public ConexionHTTP(Context context, String mensajeProgressDialog, String urlServer, String method, String parametros_post, ConnectionInterface connectionInterface){

        this.mContext=context;
        this.url=urlServer;
        this.connectionInterface = connectionInterface;
        this.mensajeProgressDialog = mensajeProgressDialog;
        this.method=method;
        this.parametros_post=parametros_post;
    }


    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr;
        try {
            String Datos = parametros_post;
            URL url=new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            if(method.equals(Constantes.POST)){

                  urlConnection.setDoOutput(method.equals(Constantes.POST));
                  urlConnection.setDoInput(method.equals(Constantes.POST));
                  urlConnection.setRequestProperty("Content-Type", "application/json");
                  urlConnection.setRequestProperty("Accept", "application/json");
                  OutputStreamWriter streamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                  streamWriter.write(Datos);
                  streamWriter.flush();
            }
            else{
                urlConnection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            }

            urlConnection.connect();
            boolean isError = urlConnection.getResponseCode() >= 400;
            InputStream inputStream =isError ? urlConnection.getErrorStream() : urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            return forecastJsonStr;

        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Mostramos el cargando...
//        progressDialog=new ProgressDialog(mContext);
//        progressDialog.setMessage(mensajeProgressDialog);
//        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        // Ocultamos el cargando...
//        if (progressDialog!=null && progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }

        // Regresamos los datos a la actividad usando la interface...
        connectionInterface.doProcess(s);

    }
}
