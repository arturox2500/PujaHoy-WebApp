package com.webapp08.pujahoy.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity(name = "USERS")
public class Usuario{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;
    private double reputacion;
    private String nombreVisible;
    private String contacto;
    private String descripcion;
    private boolean activo;
    private Blob fotoPerfil;
    private int codigoPostal;

    @OneToMany(mappedBy="vendedor",cascade = CascadeType.ALL)
    private List<Producto> productos;

    private String encodedPassword;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    protected Usuario(){

    }

    public Usuario(String nombre, double reputacion, String nombreVisible, String contacto, int codigoPostal,String descripcion, boolean activo, String encodedPassword, String... roles){
        this.nombre = nombre;
        this.reputacion = reputacion;
        this.encodedPassword = encodedPassword;
        this.roles = List.of(roles);
        this.contacto = contacto;
        this.descripcion = descripcion;
        this.activo = activo;
        this.nombreVisible = nombreVisible;
        this.productos = null;
        this.fotoPerfil = cargarFotoPerfilEstandar();
        this.codigoPostal = codigoPostal;
    }

    public Long getId() {
        return id;
    }

    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public void setFotoPerfil(Blob fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getFotoPerfil() {
        return fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getReputacion() {
        return reputacion;
    }

    public void setReputacion(double reputacion) {
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

    public void changeActivo(){
        this.activo = !this.activo;
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
    
    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
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

    private Blob cargarFotoPerfilEstandar() {
        try {
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/img/default-profile.jpg");
            if (imageStream != null) {
                byte[] imagenBytes = imageStream.readAllBytes();
                return new SerialBlob(imagenBytes);
            }
            return null;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null; // Si hay un error, deja el blob como null
        }
    }
    
}
