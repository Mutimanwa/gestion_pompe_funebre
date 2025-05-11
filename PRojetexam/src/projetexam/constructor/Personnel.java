/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.constructor;

/**
 *
 * @author calvindev
 */
public class Personnel {
    private int id ;
    private String nom ;
    private String poste;
    private String telephone;
    private String statut;
    
    
    public Personnel(int id , String nom , String poste , String telephone){
        this.id = id;
        this.nom = nom;
        this.poste = poste ;
        this.telephone = telephone;
        this.statut = "actif"; // Valeur par défaut

    }

    public Personnel(int id , String nom , String poste , String telephone, String statut){
        this.id = id;
        this.nom = nom;
        this.poste = poste ;
        this.telephone = telephone;
        this.statut = statut;
        
    }
    
    public int getId(){return id;}
    public String getNom(){return nom;}
    public String getPoste(){return poste;}
    public String getTelephone(){return telephone;}
    public String getStatut(){return statut;}

    
    public void setId(int id){this.id = id;}
    public void setNom(String nom){this.nom = nom;}
    public void setPoste(String poste){this.poste = poste;}
    public void setTelephone(String telephone){this.telephone = telephone;}
    public void setStatut(String statut){this.statut = statut;}

}
