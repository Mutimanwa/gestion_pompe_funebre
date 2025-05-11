/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.constructor;

/**
 *
 * @author calvindev
 */
public class Materiel {
    private int id;
    private String nom;
    private String description;
    private double prix;
    private int stock;

    // Constructeur
    public Materiel(int id, String nom, String description, double prix, int stock) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public double getPrix() { return prix; }
    public int getStock() { return stock; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setPrix(double prix) { this.prix = prix; }
    public void setStock(int stock) { this.stock = stock; }
}
