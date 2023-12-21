package org.example.analyse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @Note : Récapitulatif du travail qu'il reste a réaliser
 *
 * TODO: continuer le parse pour save les profil quand c'est le bon
 * TODO : 1 -> Filter pour le cas profil_expensive
 * TODO : 2 -> Continuer pour les méthodes POST
 * TODO : 3 -> Réflexion sur la structure de l'api/des arguments/ des logs ( pour rapport) pour pb d'analyse
 * !! TODO : refactor parseLogLine(..) car beaucoup trop long
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
                System.out.println(line );


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


        String[] variables = {"id", "id_user", "productDTO", "userDTO"};

        // Exemple basique de traitement de ligne (à adapter à votre format réel)
        String[] parts = logLine.split(" , "); // Divise la ligne par la virgule et l'espace
        String typeRequest = parts[0].split(" ")[7].replace("[", "").replace("]", "");; // Type de la requete : get, post etc..
        String dateTime = parts[0].split(" WARN ")[0].trim(); // Récupère la date et l'heure
        String method = parts[1].split(" : ")[1].trim(); // Récupère le nom de la méthode


        /**
         * Ici je save les params dans une hashmap
         */
        HashMap<String,String> myHashMap = new HashMap<>();
        for (int i =2; i < parts.length; i++)
        {
            String[] key_val = parts[i].replace("[", "").replace("]", "").split(" : ");

            /**
             * Si pas un DTO -> ajout
             */
            if (!key_val[0].equals("productDTO") & !key_val[0].equals("userDTO"))
            {
                myHashMap.put(key_val[0],key_val[1]);
                continue;
            }

            /**
             * Si DTO  ->  je cut pour recuperer les attributs
             */
            String[] dtoList = key_val[1].split("\\(")[1].substring(0,key_val[1].split("\\(")[1].length()-1).split(", ");
            // parcours la liste d'attributs
            for (String  e : dtoList)
            {
                // Si mes clés ne sont pas deja contenue (gestion duc cas ou id deja trouvé par la roude)
                // car Probleme pour methode POst (dans DTO id_produit = null car c'est la bd qui gère le numéro
                // TODO : optimisation de ce point !! ?
                if (!myHashMap.containsKey(e.split("=")[0]) && !e.split("=")[1].equals("null"))
                {
                    /**
                     * j'ajoute chaque attribut et les valeurs associé
                     */
                    myHashMap.put(e.split("=")[0],e.split("=")[1]);
                }
            }


        }

//        myHashMap.forEach((k,v) -> System.out.println(k +" | "+v));

//        System.out.println(myHashMap.get("id") );
        Long id_u = null;
        Long id_p = null;

        /**
         * Gestion du cas qui fais chier (ligne 112)
         */
        if(myHashMap.get("id_user") != null)
        {
             id_u =Long.parseLong(myHashMap.get("id_user").replace(" ", ""));
        }

        if(myHashMap.get("id") != null)
        {
             id_p =  Long.parseLong(myHashMap.get("id").replace(" ", ""));
        }

        switch (typeRequest){
            case "GET":
                insertIntoProfilRead(
                        id_u,
                        id_p,
                        method,
                        dateTime
                );
                break;
            case  "PUT", "DELETE":
                insertIntoProfilWrite(
                        id_u,
                        id_p,
                        method,
                        typeRequest,
                        dateTime
                );
                break;

        }
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
    private static void insertIntoProfilRead( Long userId, Long productId, String method, String jour_heure)
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
    private static void insertIntoProfilWrite(Long userId, Long productId, String method, String type, String jour_heure)
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
    private static void insertIntoProfilExpensive(Long userId, Long productId, String method, int prix, String jour_heure)
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
