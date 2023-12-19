package org.example;

public class CodeProcessor extends SpoonProcessor {

    public CodeProcessor(String projectPath) {
        super(projectPath);

    }

    public void apply(LogAnnotationProcessor codeGenerator) {
        parser.addProcessor(codeGenerator);
        parser.run();
    }

}
