package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Usuario{
    
    @Id
    private String id;

    private String nombre;
    private int reputacion;
    private String tipo;

    protected Usuario(){

    }

    public Usuario(String id, String nombre, int reputacion, String tipo){
        this.id = id;
        this.nombre = nombre;
        this.reputacion = reputacion;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getReputacion() {
        return reputacion;
    }

    public void setReputacion(int reputacion) {
        this.reputacion = reputacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
}
