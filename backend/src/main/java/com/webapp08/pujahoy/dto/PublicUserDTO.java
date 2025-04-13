package com.webapp08.pujahoy.dto;
import java.util.List;
import java.util.Objects;

public class PublicUserDTO {
    private Long id;
    private String name;
    private double reputation;
    private boolean active;
    private String visibleName;
    private String contact;
    private String description;
    private int zipCode;
    private String image;
    private List<String> rols;



    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public List<String> getRols() {
        return rols;
    }
    public void setRols(List<String> rols) {
        this.rols = rols;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getReputation() {
        return reputation;
    }
    public void setReputation(double reputation) {
        this.reputation = reputation;
    }
    public String getVisibleName() {
        return visibleName;
    }
    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getZipCode() {
        return zipCode;
    }
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
    public int changes(PublicUserDTO old) {
        int cambios = 0;

        if (!Objects.equals(this.name, old.name)) cambios++;
        if (Double.compare(this.reputation, old.reputation) != 0) cambios++;
        if (!Objects.equals(this.visibleName, old.visibleName)) cambios++;
        if (!Objects.equals(this.contact, old.contact)) cambios++;
        if (!Objects.equals(this.description, old.description)) cambios++;
        if (this.zipCode != old.zipCode) cambios++;
        if (!Objects.equals(this.image, old.image)) cambios++;

        return cambios;
    }
}
