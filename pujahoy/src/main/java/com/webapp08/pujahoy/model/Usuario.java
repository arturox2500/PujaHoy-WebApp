package com.webapp08.pujahoy.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "USERS")
public class Usuario{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;
    private int reputacion;
    private String nombreVisible;
    private String contacto;
    private String descripcion;
    private boolean activo;

    private String encodedPassword;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    protected Usuario(){

    }

    public Usuario(String nombre, int reputacion, String nombreVisible, String contacto, String descripcion, boolean activo, String encodedPassword, String... roles){
        this.nombre = nombre;
        this.reputacion = reputacion;
        this.encodedPassword = encodedPassword;
        this.roles = List.of(roles);
        this.contacto = contacto;
        this.descripcion = descripcion;
        this.activo = activo;
        this.nombreVisible = nombreVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setPass(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }    

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String determinarTipoUsuario() {
        if (this.getRoles().contains("ADMIN")) {
            return "Administrador";
        } else if (this.getRoles().contains("USER")) {
            return "Usuario Registrado";
        } else {
            return "Desconocido";
        }
    }
    
    
}
