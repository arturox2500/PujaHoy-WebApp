package com.webapp08.pujahoy.model;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario{//Hola
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;
    private String nombreVisible;
    private int reputacion;
    private String tipo;
    private String descripcion;
    private String contacto;
    private String pass;
    private Boolean estatus;
    private ArrayList<String> roles;

    protected Usuario(){

    }

    public Usuario(String nombre, String nombreVisible, int reputacion, String descripcion, String contacto, String pass, Boolean estatus, ArrayList<String> roles){
        this.nombre = nombre;
        this.nombreVisible = nombreVisible;
        this.reputacion = reputacion;
        this.descripcion = descripcion;
        this.contacto = contacto;
        this.pass = pass;
        this.estatus = estatus;
        this.roles = new ArrayList<String>(roles);    
    }

    public Long getId() {
        return this.id;
    }

    public String getPass(){
        return this.pass;
    }

    public void setPass(String pass){
        this.pass = pass;
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

    public String getNombreVisible() {
        return nombreVisible;
    }

    public void setNombreVisible(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public ArrayList<String> getRoles(){
        return this.roles;
    }
    
}
