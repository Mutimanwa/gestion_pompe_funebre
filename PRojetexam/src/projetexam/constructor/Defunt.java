/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.constructor;

/**
 *
 * @author calvindev
 */
public class Defunt {
    private int id ;
    private String nom;
    private String prenom;
    private String date_naissance;
    private String date_deces;
    private String lieu_deces;
    private int id_famille;
    
    public Defunt(int id , String nom ,String prenom ,String date_naissance , String date_deces, String lieu_deces ,int id_famille){
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.date_naissance = date_naissance;
        this.date_deces = date_deces;
        this.lieu_deces = lieu_deces;
        this.id_famille = id_famille;
    }
    public Defunt(){}

    //getteur et setteur
    public int getId(){return id ;}
    public void setId(int id){this.id = id ;}


     public String getNom(){return nom ;}
    public void setNom(String nom){this.nom = nom ;}

     public String getPrenom(){return prenom ;}
    public void setPrenom(String prenom){this.prenom = prenom ;}

    public String getDate_naissance(){return date_naissance ;}
    public void setDate_naissance(String date_naissance){this.date_naissance = date_naissance ;}

    public String getDate_deces(){return date_deces ;}
    public void setId(String date_deces){this.date_deces = date_deces ;}
    
    public String getLieuDeces(){return lieu_deces ;}
    public void setLieuDeces(String lieu_deces){this.lieu_deces = lieu_deces ;}

    public int getId_famille(){return id_famille ;}
    public void setId_famille(int id_famille){this.id_famille = id_famille ;}
}
