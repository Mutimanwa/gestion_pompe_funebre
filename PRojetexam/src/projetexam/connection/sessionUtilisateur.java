/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.connection;

/**
 *
 * @author calvindev
 */
public class sessionUtilisateur {
    private static String nomUtilisateur;
    private static String prenom;
    private static String type;
    
    public static void setSession(String nom, String prenomUser, String typeUser) {
        nomUtilisateur = nom;
        prenom = prenomUser;
        type = typeUser;
    }

    public static String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public static String getPrenom() {
        return prenom;
    }

    public static String getType() {
        return type;
    }

    public static void clearSession() {
        nomUtilisateur = null;
        prenom = null;
        type = null;
    }
}
