package org.example;

import spoon.Launcher;
import spoon.reflect.CtModel;


public class Main {
    public static void main(String[] args) {
        // Création du lanceur Spoon
        Launcher spoonLauncher = new Launcher();

        // Ajout du processor LogAnnotationProcessor
        spoonLauncher.addProcessor(new LogAnnotationProcessor());

        // Chemin ou package contenant vos classes à analyser
        spoonLauncher.addInputResource("/home/mathieu/Documents/logger");
        spoonLauncher.setSourceOutputDirectory("/home/mathieu/Documents/test");

        // Lancement de l'analyse avec Spoon
        spoonLauncher.run();

        // Obtention du modèle Spoon
        CtModel model = spoonLauncher.getModel();

    }
}