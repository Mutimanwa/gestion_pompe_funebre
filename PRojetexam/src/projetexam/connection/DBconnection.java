package projetexam.connection;
import java.sql.*;

public class DBconnection {

    private static final String URL = "jdbc:mysql://localhost:3306/pompefunebre";
    private static final String pswd = "";
    private static final String user = "root";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, user, pswd);
            System.out.println("Connexion réussie à la base de données.");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        return conn;
    }
}
