package org.example;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.*;

import java.util.ArrayList;
import java.util.Set;

public class LogAnnotationProcessor extends AbstractProcessor<CtClass<?>> {
    private static final String LOG4J_IMPORT = "import org.apache.logging.log4j.LogManager;";
    private static final String LOGGER_IMPORT = "import org.apache.logging.log4j.Logger;";

    @Override
    public void process(CtClass<?> ctClass) {


        boolean isFind = false ;

        for (CtMethod<?> method : ctClass.getMethods()) {
            String annotation = method.getAnnotations().toString();

            if (annotation.contains("GetMapping") || annotation.contains("PostMapping") || annotation.contains("PutMapping") || annotation.contains("DeleteMapping")) {
                System.out.println(method.getSimpleName());
                method.getParameters().forEach(e -> System.out.println(e.getSimpleName())); //

//                CtCodeSnippetStatement logCode = getFactory().Code().createCodeSnippetStatement(generateLogStatement());

                String type = "?";
                if (annotation.contains("GetMapping")) {
                    type = "GET";
                } else if (annotation.contains("PostMapping")) {
                    type = "POST";
                } else if (annotation.contains("PutMapping")) {
                    type = "PUT";
                } else if (annotation.contains("DeleteMapping")) {
                    type = "DELETE";
                }

                ArrayList<String> paramList = new ArrayList<>();
                method.getParameters().forEach(e -> paramList.add(e.getSimpleName()));
                CtCodeSnippetStatement logCode = getFactory().Code().createCodeSnippetStatement(generateLogStatement2(type,method.getSimpleName(),paramList));
                
                
                method.getBody().insertBegin(logCode);

                isFind = true;
            }
        }


        if (isFind)
        {
            String loggerField = "logManager.getLogger(\""+ctClass.getSimpleName()+"\");";
            CtField<?> field = getFactory().createField();
            field.setSimpleName("logger");
            field.setModifiers(Set.of(ModifierKind.PRIVATE,ModifierKind.STATIC,ModifierKind.FINAL));
            field.setDefaultExpression(getFactory().createCodeSnippetExpression(loggerField));
            field.setType(getFactory().Type().createReference("org.apache.logging.log4j.Logger"));
            ctClass.addFieldAtTop(field);

            CtField<?> field2 = getFactory().createField();
            field2.setSimpleName("logManager");
            field2.setModifiers(Set.of(ModifierKind.PRIVATE,ModifierKind.STATIC));
            field2.setType(getFactory().Type().createReference("org.apache.logging.log4j.LogManager"));
            ctClass.addFieldAtTop(field2);
        }

    }

    private String generateLogStatement2(String type, String method, ArrayList<String> paramlist) {
        String params = String.join(", ", paramlist);
        return String.format("logger.info(\"[%s] Method : %s, Parameters : %s\")", type, method, params);
    }

    private String generateLogStatement() {
        return "logger.info(\"Hello, Worldos!\")";
    }

}
