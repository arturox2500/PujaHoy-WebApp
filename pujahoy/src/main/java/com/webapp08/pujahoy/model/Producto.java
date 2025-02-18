package com.webapp08.pujahoy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Blob;
import java.util.Date;

@Entity
public class Producto {
        
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;
    
        private String datos;
        private Date horaIni;
        private Date horaFin;
        private String estado;
        private Blob imagen;
        private String vendedor_id;
    
        protected Producto(){
    
        }

        protected Producto(String datos, Date horaIni, Date horaFin, String estado, Blob imagen, String vendedor_id){
            this.datos = datos;
            this.horaIni = horaIni;
            this.horaFin = horaFin;
            this.estado = estado;
            this.imagen = imagen;
            this.vendedor_id = vendedor_id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getDatos() {
            return datos;
        }

        public void setDatos(String datos) {
            this.datos = datos;
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

        public String getVendedor_id() {
            return vendedor_id;
        }

        public void setVendedor_id(String vendedor_id) {
            this.vendedor_id = vendedor_id;
        }

        

}
