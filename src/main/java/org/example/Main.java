package org.example;

import spoon.Launcher;
import spoon.reflect.CtModel;


public class Main {
    public static void main(String[] args) {
        // Création du lanceur Spoon
        CodeProcessor processor = new CodeProcessor("/home/mathieu/Documents/EVOLUTION/logger-before/logger");
//        CodeProcessor processor = new CodeProcessor("C:\\Users\\victo\\Documents\\GitHub\\logger");

        CodeProcessor codeGenerationProcessor = processor;
        codeGenerationProcessor.apply(new LogAnnotationProcessor());



    }
}