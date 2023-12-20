package org.example.analyse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * TODO : Une méthode pour récuperer les id des produits qui
 * sont supérieur soit a la médiane soit quartile
 * poour une fois dans le parser save dans profil_expensive
 * les produit ligen qui sont consiudéré
 *
 *
 * TODO: parametrer le parse pour save les profil quand c'est le bon
 */

public class MainAnalyse {
    /**
     * meme db que dans le projet logger
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/logger";
    private static final String DB_USER = "mathieu";
    private static final String DB_PASSWORD = "12345678";

    /**
     * stat_prix_produit[0] => mediane
     * stat_prix_produit[1] => quantile sup
     */
    private static double[] stat_prix_produit;
    private static final SqlService SQL_SERVICE = new SqlService();

    public static void main(String[] args) throws SQLException {

        /**
         * Creation des tables si besoin pour sauver les profils
         */
//        newTable();

        /**
         * Objetnir les statistique de la table product
         */
        stats();



        /**
         * chemin vers le fichier de log du projet logger-after
         * ce projet contient les logs inseré automatiquement par Spooon
         */
        String filePath = "/home/mathieu/Documents/EVOLUTION/logger-after/myLogs.xml";

        // ouverture du fichier
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // lecture ligne par ligne
            while ((line = br.readLine()) != null) {
//                System.out.println(line );


                parseLogLine(line); // Appel à la méthode pour analyser chaque ligne de log
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Methode qui permet de parser la ligne et de save les information dans les profils désiré
     * @param logLine
     */
    private static void parseLogLine(String logLine) {
        // Écrire ici la logique pour extraire les informations de chaque ligne de log
        // Utilisez des expressions régulières ou des opérations sur les chaînes pour obtenir les informations nécessaires

        // Exemple basique de traitement de ligne (à adapter à votre format réel)
        String[] parts = logLine.split(", "); // Divise la ligne par la virgule et l'espace
        String dateTime = parts[0].split(" WARN ")[0].trim(); // Récupère la date et l'heure
        String method = parts[1].split(" : ")[1].trim(); // Récupère le nom de la méthode

        // Affiche les informations récupérées (à adapter à votre besoin)
//        System.out.println("Date/Time: " + dateTime);
//        System.out.println("Method: " + method);
        // Ajoutez le code pour extraire d'autres informations comme l'ID utilisateur, l'ID du produit, etc.
    }


    /**
     * Uniquement pour creer tes tables
     */
    private static void newTable()
    {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             SQL_SERVICE.createTables(connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void stats()
    {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stat_prix_produit = SQL_SERVICE.calculateMedianAndUpperQuartile(connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Insertion dans le profil_read
     */
    private static void insertIntoProfilRead( long userId, long productId, String method, String jour_heure)
    {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);


            SQL_SERVICE.insertIntoProfilRead(
                    connection,
                    userId,
                    productId,
                    method,
                    jour_heure
            );
            // Fermeture des ressources
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insertion dans le profil_write
     */
    private static void insertIntoProfilWrite(long userId, long productId, String method, String type, String jour_heure)
    {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            SQL_SERVICE.insertIntoProfilWrite(
                    connection,
                    userId,
                    productId,
                    method,
                    type,
                    jour_heure
            );
            // Fermeture des ressources
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Insertion dans le profil_expensive
     */
    private static void insertIntoProfilExpensive(long userId, long productId, String method, int prix, String jour_heure)
    {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            SQL_SERVICE.insertIntoProfilExpensive(
                    connection,
                    userId,
                    productId,
                    method,
                    prix,
                    jour_heure
            );
            // Fermeture des ressources
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
