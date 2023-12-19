package org.example.analyse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainAnalyse {

    public static void main(String[] args) {


        String filePath = "/home/mathieu/Documents/EVOLUTION/logger-after/myLogs.xml"; // Spécifiez le chemin vers votre fichier

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line );


                parseLogLine(line); // Appel à la méthode pour analyser chaque ligne de log
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseLogLine(String logLine) {
        // Écrire ici la logique pour extraire les informations de chaque ligne de log
        // Utilisez des expressions régulières ou des opérations sur les chaînes pour obtenir les informations nécessaires

        // Exemple basique de traitement de ligne (à adapter à votre format réel)
        String[] parts = logLine.split(", "); // Divise la ligne par la virgule et l'espace
        String dateTime = parts[0].split(" WARN ")[0].trim(); // Récupère la date et l'heure
        String method = parts[1].split(" : ")[1].trim(); // Récupère le nom de la méthode

        // Affiche les informations récupérées (à adapter à votre besoin)
        System.out.println("Date/Time: " + dateTime);
        System.out.println("Method: " + method);
        // Ajoutez le code pour extraire d'autres informations comme l'ID utilisateur, l'ID du produit, etc.
    }


}
