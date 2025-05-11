package projetexam.manager;

import projetexam.connection.DBconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import projetexam.constructor.Materiel;

public class MaterielManager {

    // Ajouter un matériel
    public static boolean addMateriel(Materiel materiel) {
        try (Connection conn = DBconnection.connect()) {
            String sql = "INSERT INTO materiel (nom, type_materiel, prix, stock) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, materiel.getNom());
            pst.setString(2, materiel.getDescription());
            pst.setDouble(3, materiel.getPrix());
            pst.setInt(4, materiel.getStock());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du matériel : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lire tous les matériels
    public static List<Materiel> getAllMateriels() {
        List<Materiel> list = new ArrayList<>();
        try (Connection conn = DBconnection.connect()) {
            String sql = "SELECT * FROM materiel ORDER BY nom ASC";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Materiel m = new Materiel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("type_materiel"),
                    rs.getDouble("prix"),
                    rs.getInt("stock")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des matériels : " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Lire seulement les matériels en stock
    public static List<Materiel> getAvailableMateriels() {
        List<Materiel> list = new ArrayList<>();
        try (Connection conn = DBconnection.connect()) {
            String sql = "SELECT * FROM materiel WHERE stock > 0 ORDER BY nom ASC";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Materiel m = new Materiel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("type_materiel"),
                    rs.getDouble("prix"),
                    rs.getInt("stock")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des matériels disponibles : " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Modifier un matériel
    public static boolean updateMateriel(Materiel materiel) {
        try (Connection conn = DBconnection.connect()) {
            String sql = "UPDATE materiel SET nom = ?, type_materiel = ?, prix = ?, stock = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, materiel.getNom());
            pst.setString(2, materiel.getDescription());
            pst.setDouble(3, materiel.getPrix());
            pst.setInt(4, materiel.getStock());
            pst.setInt(5, materiel.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du matériel : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Mettre à jour le stock
    public static boolean updateStock(int id, int newStock) {
        try (Connection conn = DBconnection.connect()) {
            String sql = "UPDATE materiel SET stock = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, newStock);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un matériel
    public static boolean deleteMateriel(int id) {
        try (Connection conn = DBconnection.connect()) {
            String sql = "DELETE FROM materiel WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du matériel : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
