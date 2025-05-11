/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.manager;

import java.sql.*;
import projetexam.connection.DBconnection;
import projetexam.constructor.FactureData;

/**
 *
 * @author calvindev
 */
public class FactureDAO {

    public static FactureData getFactureDataByCeremonieId(int idCeremonie) {
        FactureData data = null;

        try (Connection conn = DBconnection.connect()) {
            String sql = "SELECT f.nom AS nom_famille, d.nom AS nom_defunt, c.date_ceremonie AS date_ceremonie, c.heure_ceremonie AS heure_ceremonie " +
                         "FROM ceremonie c " +
                         "JOIN defunt d ON c.id_defunt = d.id " +
                         "JOIN famille f ON d.id_famille = f.id " +
                         "WHERE c.id = ?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idCeremonie);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                data = new FactureData(
                    rs.getString("nom_famille"),
                    rs.getString("nom_defunt"),
                    rs.getString("date_ceremonie"),
                    rs.getString("heure_ceremonie")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
