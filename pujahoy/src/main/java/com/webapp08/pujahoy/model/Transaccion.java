package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Transaccion {

    @Id
    private long id;

    @OneToOne
    private Producto producto;

    @OneToOne
    private Usuario vendedor;

    @OneToOne
    private Usuario comprador;
    private double coste;

    protected Transaccion(){

    }

    public Transaccion(Producto producto, Usuario vendedor, Usuario comprador, double coste){
        this.producto = producto;
        this.vendedor = vendedor;
        this.comprador = comprador;
        this.coste = coste;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }    
}
