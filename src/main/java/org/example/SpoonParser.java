package org.example;


import spoon.Launcher;

public class SpoonParser extends Parser<Launcher> {

    public SpoonParser(String projectPath) {
        super(projectPath);
    }

    public void setLauncher(String sourceOutputPath, String binaryOutputPath,
                            boolean autoImports, boolean commentsEnabled) {
        parser = new Launcher();
        parser.addInputResource(getProjectSrcPath());
        // pour les projet maven
        parser.getEnvironment().setSourceClasspath(new String[] {
                getProjectBinPath()});
        parser.setSourceOutputDirectory(sourceOutputPath);
        parser.setBinaryOutputDirectory(binaryOutputPath);
        parser.getEnvironment().setAutoImports(autoImports);
        parser.getEnvironment().setCommentEnabled(commentsEnabled);
    }

    public void configure() {
        String originalProjectPath = projectPath;
        String transformedProjectPath = originalProjectPath + "___spooned_test";
        setLauncher(transformedProjectPath + "/src", transformedProjectPath + "/spooned/target", true, true);
    }

    public void addProcessor(LogAnnotationProcessor processor) {
        parser.addProcessor(processor);
    }

    public void run() {
        parser.run();
    }
}