/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.constructor;

/**
 *
 * @author calvindev
 */
public class FactureData {
    public String nomFamille;
    public String nomDefunt;
    public String dateCeremonie;
    public String heureCeremonie;

    public FactureData(String nomFamille, String nomDefunt, String dateCeremonie, String heureCeremonie) {
        this.nomFamille = nomFamille;
        this.nomDefunt = nomDefunt;
        this.dateCeremonie = dateCeremonie;
        this.heureCeremonie = heureCeremonie;
    }
}
