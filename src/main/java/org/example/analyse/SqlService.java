package org.example.analyse;


import com.mysql.cj.xdevapi.StreamingSqlResultBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlService {

    public SqlService() {
    }

    public void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        // Requête pour créer une table
        String createReadTable = "CREATE TABLE profil_read ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_user BIGINT NULL, " +
                "id_produit BIGINT NULL, " +
                "method VARCHAR(50), " +
                "jour_heure VARCHAR(100),"+
                "FOREIGN KEY (id_user) REFERENCES user(id)," +
                "FOREIGN KEY (id_produit) REFERENCES product(id)" +
                ");";

        String createWriteTable = "CREATE TABLE profil_write ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_user BIGINT NULL, " +
                "id_produit BIGINT NULL, " +
                "method VARCHAR(50), " +
                "type VARCHAR(50), " +
                "jour_heure VARCHAR(100),"+
                "FOREIGN KEY (id_user) REFERENCES user(id)," +
                "FOREIGN KEY (id_produit) REFERENCES product(id)" +
                ");";


        String createExpensiveTable = "CREATE TABLE profil_expensive ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_user BIGINT, " +
                "id_produit BIGINT, " +
                "method VARCHAR(50), " +
                "jour_heure VARCHAR(100),"+
                "FOREIGN KEY (id_user) REFERENCES user(id)," +
                "FOREIGN KEY (id_produit) REFERENCES product(id)" +
                ");";


        // Exécution de la requête de création de table
        statement.executeUpdate(createReadTable);
        statement.executeUpdate(createWriteTable);
        statement.executeUpdate(createExpensiveTable);
        statement.close();
        System.out.println("Table créée avec succès !");


    }


    public static void insertIntoProfilRead(Connection connection, Long userId, Long productId, String method, String jour_heure) throws SQLException {
        String insertQuery = "INSERT INTO profil_read (id_user, id_produit, method, jour_heure) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            if (userId != null) {
                preparedStatement.setObject(1, userId);
            } else {
                preparedStatement.setNull(1, Types.BIGINT); // Si userId est null, définir la valeur de la colonne comme NULL
            }

            if (productId != null) {
                preparedStatement.setObject(2, productId);
            } else {
                preparedStatement.setNull(2, Types.BIGINT); // Si productId est null, définir la valeur de la colonne comme NULL
            }
            preparedStatement.setString(3, method);
            preparedStatement.setString(4, jour_heure);

            preparedStatement.executeUpdate();
        }
    }

    public static void insertIntoProfilWrite(Connection connection, Long userId, Long productId, String method, String type, String jour_heure) throws SQLException {
        String insertQuery = "INSERT INTO profil_write (id_user, id_produit, method, type, jour_heure) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            if (userId != null) {
                preparedStatement.setObject(1, userId);
            } else {
                preparedStatement.setNull(1, Types.BIGINT); // Si userId est null, définir la valeur de la colonne comme NULL
            }

            if (productId != null) {
                preparedStatement.setObject(2, productId);
            } else {
                preparedStatement.setNull(2, Types.BIGINT); // Si productId est null, définir la valeur de la colonne comme NULL
            }
            preparedStatement.setString(3, method);
            preparedStatement.setString(4, type);
            preparedStatement.setString(5, jour_heure);

            preparedStatement.executeUpdate();
        }
    }

    public static void insertIntoProfilExpensive(Connection connection, Long userId, Long productId, String method, String jour_heure) throws SQLException {
        String insertQuery = "INSERT INTO profil_expensive (id_user, id_produit, method,jour_heure) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            if (userId != null) {
                preparedStatement.setObject(1, userId);
            } else {
                preparedStatement.setNull(1, Types.BIGINT); // Si userId est null, définir la valeur de la colonne comme NULL
            }

            if (productId != null) {
                preparedStatement.setObject(2, productId);
            } else {
                preparedStatement.setNull(2, Types.BIGINT); // Si productId est null, définir la valeur de la colonne comme NULL
            }
            preparedStatement.setString(3, method);
            preparedStatement.setString(4, jour_heure);

            preparedStatement.executeUpdate();
        }
    }

    public static double[] calculateMedianAndUpperQuartile(Connection connection) throws SQLException {
        String query = "SELECT " +
                "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY price) OVER () AS mediane, " +
                "PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY price) OVER () AS quartile_superieur " +
                "FROM product";




        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                double mediane = resultSet.getDouble("mediane");
                double quartileSuperieur = resultSet.getDouble("quartile_superieur");

                System.out.println("Mediane : " + mediane);
                System.out.println("Quartile supérieur : " + quartileSuperieur);


                String query2 = "SELECT id " +
                        "FROM product WHERE price >= ?";




                return new double[]{mediane, quartileSuperieur};
            }


        }
        return null;
    }

    public static List<Long>  productExpensive(Connection connection, double value) throws SQLException {
        String query = "SELECT " +
                "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY price) OVER () AS mediane, " +
                "PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY price) OVER () AS quartile_superieur " +
                "FROM product";


                String query2 = "SELECT id " +
                        "FROM product WHERE price >= ?";

                List<Long> producstId = new ArrayList<>();


                // Préparation de la deuxième requête avec un PreparedStatement
                try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                    preparedStatement2.setDouble(1, value);
                    ResultSet resultSet2 = preparedStatement2.executeQuery();
                    while (resultSet2.next()) {
                        producstId.add((long) resultSet2.getInt("id"));
                        System.out.println(resultSet2.getInt("id"));
                    }
                }






        return producstId;
    }



}
