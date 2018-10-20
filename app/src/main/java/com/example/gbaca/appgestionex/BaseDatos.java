package com.example.gbaca.appgestionex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class BaseDatos extends SQLiteOpenHelper{

    Context contexto;
    static final String name = "datosGestionex.db";
    static final int version = 1;

    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BaseDatos(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE IF NOT EXISTS disposicion(" +
                "id INTEGER PRIMARY KEY," +
                "id_servicio INTEGER," +
                "id_usuario INTEGER," +
                "fecha_inicial TEXT," +
                "fecha_final TEXT," +
                "toneladas REAL," +
                "lugar TEXT," +
                "estado INTEGER" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS rastreo(" +
                "id_local INTEGER PRIMARY KEY," +
                "id_remoto INTEGER," +
                "id_usuario INTEGER," +
                "fecha TEXT," +
                "latitud REAL," +
                "longitud REAL," +
                "distancia REAL" +
                ")");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // Metodo de borrado
    public void borrar_tabla(String tabla){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(tabla,null,null);
            Log.i("DB","Borrada Tabla: " + tabla);
        }catch (SQLiteException e){
            Log.e("DB",e.toString());
            Toast.makeText(contexto,"Error borrando la tablas",Toast.LENGTH_SHORT).show();
        }
    }

    // Consultas...
    public ArrayList obtenerDisposicionNoEnviadas(){

        ArrayList<disposicion> disposiciones = new ArrayList<>();
        disposicion dispo;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db!=null){
            Cursor c = db.rawQuery("SELECT id_servicio,fecha_inicial,fecha_final,toneladas,lugar,id_usuario FROM disposicion WHERE estado=0",null);
            if(c.moveToFirst()){
                do{
                    dispo = new disposicion();
                    dispo.setId_servicio(c.getInt(0));
                    dispo.setFecha_inicial(c.getString(1));
                    dispo.setFecha_final(c.getString(2));
                    dispo.setToneladas(c.getFloat(3));
                    dispo.setLugar(c.getString(4));
                    dispo.setId_usuario(c.getInt(5));
                    disposiciones.add(dispo);
                }while(c.moveToNext());
            }
        }
        return disposiciones;
    }


    public void marcarRastreoComoEnviado(rastreo ras){

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            if(db!=null){
                ContentValues values = new ContentValues();
                values.put("id_local",ras.getId_local());
                values.put("id_remoto",ras.getId_remoto());
                db.update("rastreo",values,"id_local="+ras.getId_local(),null);
                Log.i("DB","Actualizada Rastreo, marcado como enviado");
            }

        }catch (SQLiteException e){
            Log.e("DB",e.toString());
            Toast.makeText(contexto,"Error actualizando en la db",Toast.LENGTH_SHORT).show();
        }

    }


    public rastreo obtenerUltimoRastreo(){
        rastreo ras = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db!=null){

            String query = " SELECT latitud," +
                    "        longitud" +
                    " FROM rastreo " +
                    " ORDER BY id_local DESC " +
                    " LIMIT 1";

            Cursor c = db.rawQuery(query,null);
            if(c.moveToFirst()){
                do{
                    ras = new rastreo();
                    ras.setLatitud(c.getDouble(0));
                    ras.setLongitud(c.getDouble(1));
                }while(c.moveToNext());
            }
        }
        return ras;
    }

    public ArrayList obtenerRastreosNoEnviados(){

        ArrayList<rastreo> rastreos = new ArrayList<>();
        rastreo ras;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db!=null){

            String query = " SELECT id_local," +
                           "        id_usuario," +
                           "        fecha," +
                           "        latitud," +
                           "        longitud," +
                           "        distancia " +
                           " FROM rastreo " +
                           " WHERE id_remoto = 0 ";

            Cursor c = db.rawQuery(query,null);
            if(c.moveToFirst()){
                do{
                    ras = new rastreo();
                    ras.setId_local(c.getInt(0));
                    ras.setId_usuario(c.getInt(1));
                    ras.setFecha(c.getString(2));
                    ras.setLatitud(c.getDouble(3));
                    ras.setLongitud(c.getDouble(4));
                    ras.setDistancia(c.getFloat(5));
                    rastreos.add(ras);
                }while(c.moveToNext());
            }
        }
        return rastreos;
    }



    // Metodos de insersion
    public void insertar_disposicion(disposicion dispo){

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            if(db!=null){
                ContentValues values = new ContentValues();
                values.put("id_servicio",dispo.getId_servicio());
                values.put("id_usuario",dispo.getId_usuario());
                values.put("fecha_inicial",dispo.getFecha_inicial());
                values.put("estado",0);
                db.insert("disposicion",null,values);
                Log.i("DB","Insertado Disposicion: " + dispo.getLugar());
            }

        }catch (SQLiteException e){
            Log.e("DB",e.toString());
            Toast.makeText(contexto,"Error insertando en la db",Toast.LENGTH_SHORT).show();
        }
    }


    public void insertar_rastreo(rastreo ras){

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            if(db!=null){
                ContentValues values = new ContentValues();
                values.put("id_remoto",ras.getId_remoto());
                values.put("id_usuario",ras.getId_usuario());
                values.put("fecha",ras.getFecha());
                values.put("latitud",ras.getLatitud());
                values.put("longitud",ras.getLongitud());
                values.put("distancia",ras.getDistancia());
                db.insert("rastreo",null,values);
                Log.i("DB","Insertado Rastreo");
            }

        }catch (SQLiteException e){
            Log.e("DB",e.toString());
            Toast.makeText(contexto,"Error insertando en la db",Toast.LENGTH_SHORT).show();
        }
    }



    public void cerrar_disposicion(disposicion dispo){

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            if(db!=null){
                ContentValues values = new ContentValues();
                values.put("fecha_final",dispo.getFecha_final());
                values.put("toneladas",dispo.getToneladas());
                values.put("lugar",dispo.getLugar());
                db.update("disposicion",values,"id_servicio="+dispo.getId_servicio(),null);
                Log.i("DB","Insertado Disposicion: " + dispo.getLugar());
            }

        }catch (SQLiteException e){
            Log.e("DB",e.toString());
            Toast.makeText(contexto,"Error actualizando en la db",Toast.LENGTH_SHORT).show();
        }

    }

    public void disposicion_enviada(Integer id){

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            if(db!=null){
                ContentValues values = new ContentValues();
                values.put("estado",1);
                db.update("disposicion",values,"id_servicio="+id,null);
                Log.i("DB","Disposicion marcada como Enviada: " + id);
            }

        }catch (SQLiteException e){
            Log.e("DB",e.toString());
            Toast.makeText(contexto,"Error marcando enviado en la db",Toast.LENGTH_SHORT).show();
        }

    }




}
