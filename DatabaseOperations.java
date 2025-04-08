import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseOperations {

    private static final String URL = "jdbc:mysql://localhost:3306/test?";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Charger le pilote
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("Connexion réussie.");

                // Exemple d'insertion
                // String sqlInsert = "INSERT INTO personne (nom, prenom) VALUES (?, ?)";
                // try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
                //     preparedStatement.setString(1, "Nouveau Client");
                //     preparedStatement.setString(2, "nouveau.client@email.com");
                //     int rows = preparedStatement.executeUpdate();
                //     System.out.println(rows + " ligne(s) insérée(s).");
                // }
                System.out.println("Requete suivants creation de table");
                String sqlCreateaTable = "CREATE TABLE clients (id int primary key auto_increment, nom varchar(100), email varchar(100))";
                Statement stmt = connection.createStatement(); ResultSet rs = stmt.execute(sqlCreateaTable);
                
                System.out.println("Requete suivants");
                // Exemple de lecture
                String sqlSelect = "SELECT id, nom, email FROM clients";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect);
                     ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("Liste des clients :");
                    while (resultSet.next()) {
                        System.out.println("ID: " + resultSet.getInt("id") +
                                           ", Nom: " + resultSet.getString("nom") +
                                           ", Email: " + resultSet.getString("email"));
                    }
                }

                // Autres opérations CRUD ici...

            } catch (SQLException e) {
                System.err.println("Erreur de base de données : " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC introuvable : " + e.getMessage());
        }
    }
}