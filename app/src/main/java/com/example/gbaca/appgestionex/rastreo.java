package com.example.gbaca.appgestionex;

/**
 * Created by GBaca on 7/3/2018.
 */

public class rastreo {

    private Integer id_local;
    private Integer id_remoto;
    private Integer id_usuario;
    private String fecha;
    private Double latitud;
    private Double longitud;
    private float distancia;

    public rastreo() {
    }

    public float getId_local() {
        return id_local;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public void setId_local(Integer id_local) {
        this.id_local = id_local;
    }

    public Integer getId_remoto() {
        return id_remoto;
    }

    public void setId_remoto(Integer id_remoto) {
        this.id_remoto = id_remoto;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
