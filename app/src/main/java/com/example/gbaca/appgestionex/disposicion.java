package com.example.gbaca.appgestionex;

/**
 * Created by GBaca on 6/30/2018.
 */

public class disposicion {

    private Integer id_servicio;
    private Integer id_usuario;
    private String fecha_inicial;
    private String fecha_final;
    private Float toneladas;
    private String lugar;
    private Integer estado;

    public disposicion() {
    }

    public Integer getEstado() {
        return estado;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getId_servicio() {
        return id_servicio;
    }

    public void setId_servicio(Integer id_servicio) {
        this.id_servicio = id_servicio;
    }

    public String getFecha_inicial() {
        return fecha_inicial;
    }

    public void setFecha_inicial(String fecha_inicial) {
        this.fecha_inicial = fecha_inicial;
    }

    public String getFecha_final() {
        return fecha_final;
    }

    public void setFecha_final(String fecha_final) {
        this.fecha_final = fecha_final;
    }

    public Float getToneladas() {
        return toneladas;
    }

    public void setToneladas(Float toneladas) {
        this.toneladas = toneladas;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
