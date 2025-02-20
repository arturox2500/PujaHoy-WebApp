package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Transaccion {

    @Id
    private long producto_id;

    private String vendedor_id;
    private String comprador_id;
    private double coste;

    protected Transaccion(){

    }

    protected Transaccion(String vendedor_id, String comprador_id, long producto_id, double coste){
        this.vendedor_id = vendedor_id;
        this.comprador_id = comprador_id;
        this.producto_id = producto_id;
        this.coste = coste;
    }

    public String getVendedor_id() {
        return vendedor_id;
    }

    public void setVendedor_id(String vendedor_id) {
        this.vendedor_id = vendedor_id;
    }

    public String getComprador_id() {
        return comprador_id;
    }

    public void setComprador_id(String comprador_id) {
        this.comprador_id = comprador_id;
    }

    public long getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(long producto_id) {
        this.producto_id = producto_id;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }
    
}
