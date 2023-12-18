package org.example;


import org.slf4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.*;

public class LogAnnotationProcessor extends AbstractProcessor<CtClass<?>> {
    private static final String LOG4J_IMPORT = "import org.apache.logging.log4j.LogManager;";
    private static final String LOGGER_IMPORT = "import org.apache.logging.log4j.Logger;";


    @Override
    public void process(CtClass<?> ctClass) {


        for (CtMethod<?> method : ctClass.getMethods()) {
            String annotation = method.getAnnotations().toString();

            if (annotation.contains("GetMapping") || annotation.contains("PostMapping") || annotation.contains("PutMapping") || annotation.contains("DeleteMapping")) {
                System.out.println(method.getSimpleName());
                CtCodeSnippetStatement logCode = getFactory().Code().createCodeSnippetStatement(generateLogStatement());
                method.getBody().insertBegin(logCode);

            }
        }



    }

    private String generateLogStatement() {
        return "logger.info(\"Hello, World!\")";
    }


}
