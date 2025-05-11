/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.manager;
import java.sql.*;
import projetexam.connection.DBconnection;
import projetexam.constructor.Defunt;
/**
 *
 * @author calvindev
 */
public class DefuntManager {

    
    public static boolean ajouterDefunt(Defunt defunt){
        try(Connection conn = DBconnection.connect()){
            
            String sql = "INSERT INTO defunt (nom, prenom, date_naissance, date_deces ,lieu_deces , id_famile) VALUES (?, ?, ?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, defunt.getNom());
            pst.setString(2, defunt.getPrenom());
            pst.setDate(3, java.sql.Date.valueOf(defunt.getDate_naissance()));
            pst.setDate(4, java.sql.Date.valueOf(defunt.getDate_deces()));
            pst.setString(6, defunt.getLieuDeces());
            pst.setInt(6, defunt.getId_famille());
            
            return pst.executeUpdate() > 0 ;
                  
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
}
