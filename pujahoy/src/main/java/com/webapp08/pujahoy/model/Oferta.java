package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

//import org.springframework.stereotype.Indexed;

@Entity
public class Oferta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long usuario_id;
    private long producto_id;
    private double coste;
    private Date hora;

    protected Oferta(){

    }

    public Oferta(long usuario_id, long producto_id, double coste, Date hora){
        this.usuario_id = usuario_id;
        this.producto_id = producto_id;
        this.coste = coste;
        this.hora = hora;
    }

    public long getId(){
        return id;
    }

    public long getUsuarioId(){
        return usuario_id;
    }

    public long getProductoId(){
        return producto_id;
    }

    public double getCoste(){
        return coste;
    }

    public Date getHora(){
        return hora;
    }

    public void setUsuarioId(long usuario_id){
        this.usuario_id = usuario_id;
    }

    public void setProductoId(long producto_id){
        this.producto_id = producto_id;
    }

    public void setCoste(double coste){
        this.coste = coste;
    }

    public void setHora(Date hora){
        this.hora = hora;
    }
}
