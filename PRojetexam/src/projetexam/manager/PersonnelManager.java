/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.manager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import projetexam.connection.DBconnection;
import projetexam.constructor.Personnel;

/**
 *
 * @author calvindev
 */
public class PersonnelManager {
    
    public static boolean AddPersonnel(Personnel personnel){
        try(Connection conn = DBconnection.connect()){
            String sql = "INSERT INTO employe (nom, poste, telephone, statut) VALUES (?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, personnel.getNom());
            pst.setString(2, personnel.getPoste());
            pst.setString(3, personnel.getTelephone());
            pst.setString(4, personnel.getStatut());
            
            return pst.executeUpdate() > 0;
            
        }catch(SQLException e){
            System.err.println("Erreur lors de l'ajout du personnel : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Personnel> getAllPersonnels(){
        List<Personnel> list = new ArrayList<>();
        try(Connection conn = DBconnection.connect()){
            String sql = "SELECT * FROM employe ORDER BY nom ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                Personnel p = new Personnel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("poste"),
                    rs.getString("telephone"),
                    rs.getString("statut")
                );
                list.add(p);
            }
        }catch(SQLException e){
            System.err.println("Erreur lors de la récupération des personnels : " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    public static boolean updatePersonnel(Personnel personnel){
        try(Connection conn = DBconnection.connect()){
            String sql = "UPDATE employe SET nom = ?, poste = ?, telephone = ?, statut = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, personnel.getNom());
            pst.setString(2, personnel.getPoste());
            pst.setString(3, personnel.getTelephone());
            pst.setString(4, personnel.getStatut());
            pst.setInt(5, personnel.getId());
            
            return pst.executeUpdate() > 0;
        }catch(SQLException e){
            System.err.println("Erreur lors de la mise à jour du personnel : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deletePersonnel(int id) {
        try (Connection conn = DBconnection.connect()) {
            String sql = "DELETE FROM employe WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du personnel : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
}
