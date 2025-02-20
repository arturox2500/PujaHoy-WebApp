package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vendedor_id;
    private long producto_id;
    private int puntuacion;
    private String comentario;

    protected Valoracion(){

    }   

    public Valoracion(String vendedor_id, long producto_id, int puntuacion, String comentario){
        this.vendedor_id = vendedor_id;
        this.producto_id = producto_id;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVendedor_id() {
        return vendedor_id;
    }

    public void setVendedor_id(String vendedor_id) {
        this.vendedor_id = vendedor_id;
    }

    public long getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(long producto_id) {
        this.producto_id = producto_id;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    
}
