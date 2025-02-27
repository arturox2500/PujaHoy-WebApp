package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Producto {
        
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;
    
        private String nombre;
        private String descripcion;
        private double valorini;
        private Date horaIni;
        private Date horaFin;
        private String estado;
        private Blob imagen;

        @OneToMany(mappedBy="producto")
        private List<Oferta> ofertas;

        @ManyToOne
        private Usuario vendedor;
    
        protected Producto(){
    
        }

        public Producto(String nombre,String descripcion,double valorini, String estado, Usuario vendedor){
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.valorini = valorini;
            this.horaIni = null;//horaIni;
            this.horaFin = null;//horaFin;
            this.estado = estado;
            this.imagen = null;//imagen;
            this.vendedor = vendedor;
            this.ofertas = new ArrayList<Oferta>();
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        
        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

       
        public double getValorini() {
            return valorini;
        }

        public void setValorini(double valorini) {
            this.valorini = valorini;
        }

        public Date getHoraIni() {
            return horaIni;
        }

        public void setHoraIni(Date horaIni) {
            this.horaIni = horaIni;
        }

        public Date getHoraFin() {
            return horaFin;
        }

        public void setHoraFin(Date horaFin) {
            this.horaFin = horaFin;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public Blob getImagen() {
            return imagen;
        }

        public void setImagen(Blob imagen) {
            this.imagen = imagen;
        }

        public Usuario getVendedor() {
            return vendedor;
        }

        public void setVendedor(Usuario vendedor) {
            this.vendedor = vendedor;
        }

        public List<Oferta> getOfertas() {
            return ofertas;
        }

        public void setOfertas(List<Oferta> ofertas) {
            this.ofertas = ofertas;
        }

        public void addOferta(Oferta oferta){
            this.ofertas.add(oferta);
        }

}
