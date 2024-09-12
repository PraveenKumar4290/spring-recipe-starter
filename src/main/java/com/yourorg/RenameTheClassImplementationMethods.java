//package com.yourorg;
//
//import org.openrewrite.Cursor;
//import org.openrewrite.ExecutionContext;
//import org.openrewrite.Recipe;
//import org.openrewrite.TreeVisitor;
//import org.openrewrite.java.JavaIsoVisitor;
//import org.openrewrite.java.JavaParser;
//import org.openrewrite.java.JavaTemplate;
//import org.openrewrite.java.tree.J;
//import org.openrewrite.java.tree.Statement;
//
//import java.util.List;
//
//public class RenameTheClassImplementationMethods extends Recipe {
//    @Override
//    public String getDisplayName() {
//        return "Class implementation for refactoring";
//    }
//
//    @Override
//    public String getDescription() {
//        return "Class implementation for refactoring.";
//    }
//
//    private static final String FQN = "com.imag.HelloDemoClass";
//
//    @Override
//    public TreeVisitor<?, ExecutionContext> getVisitor() {
//        return new JavaIsoVisitor<ExecutionContext>() {
//
//            @Override
//            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
//                J.ClassDeclaration visitedClassDecl = super.visitClassDeclaration(classDecl, ctx);
//
//                System.out.println(classDecl.getType().getFullyQualifiedName());
//                if (classDecl != null && FQN.equals(classDecl.getType().getFullyQualifiedName())) {
//                    List<Statement> statements = classDecl.getBody().getStatements();
//                    for (Statement statement : statements) {
//                        if (statement instanceof J.MethodDeclaration) {
//                            String name = ((J.MethodDeclaration) statement).getMethodType().getName();
//                            switch (name) {
//                                case "getForObject": {
//                                    String body="public void getObject(){\nSystem.out.println(\"Hello : getObject\");\n}";
//                                    J.MethodDeclaration methodDeclaration = (J.MethodDeclaration) statement;
//                                    JavaTemplate javaTemplate = JavaTemplate.builder(body)
//                                            .javaParser(JavaParser.fromJavaVersion())
//                                            .build();
//                                    Cursor cursor = new Cursor(getCursor(), classDecl.getBody());
//                                    System.out.println(methodDeclaration.getCoordinates().replace());
////                                    return javaTemplate.apply(getCursor(), methodDeclaration.getCoordinates().replace());
////                                    return classDecl.withBody( javaTemplate.apply(getCursor(), methodDeclaration.getCoordinates().replace()));
//                                    return visitedClassDecl.withBody(javaTemplate.apply(cursor, methodDeclaration.getCoordinates().replace()));
//                                }
//                                case "getForEntity": {
//                                    J.MethodDeclaration methodDeclaration = (J.MethodDeclaration) statement;
//                                    String body="public void getEntity(){\nSystem.out.println(\"Hello : getEntity\");\n}";
//                                    JavaTemplate javaTemplate = JavaTemplate.builder(body)
//                                            .javaParser(JavaParser.fromJavaVersion())
//                                            .build();
//                                    Cursor cursor = new Cursor(getCursor(), classDecl.getBody());
////                                    return javaTemplate.apply(getCursor(), methodDeclaration.getCoordinates().replace());
////                                    return classDecl.withBody( javaTemplate.apply(getCursor(), methodDeclaration.getCoordinates().replace()));
//                                    return visitedClassDecl.withBody(javaTemplate.apply(cursor, methodDeclaration.getCoordinates().replace()));
//
//                                }
//                                case "getExchange": {
//                                    J.MethodDeclaration methodDeclaration = (J.MethodDeclaration) statement;
//                                    String body="public void exchange(){\nSystem.out.println(\"Hello : exchange\");\n}";
//                                    JavaTemplate javaTemplate = JavaTemplate.builder(body)
//                                            .javaParser(JavaParser.fromJavaVersion())
//                                            .build();
//                                    Cursor cursor = new Cursor(getCursor(), classDecl.getBody());
////                                    return javaTemplate.apply(getCursor(), methodDeclaration.getCoordinates().replace());
////                                    return classDecl.withBody( javaTemplate.apply(getCursor(), methodDeclaration.getCoordinates().replace()));
//                                    return visitedClassDecl.withBody(javaTemplate.apply(cursor, methodDeclaration.getCoordinates().replace()));
//
//                                }
//                            }
//                        }
//                    }
//                }
//                return visitedClassDecl;
//            }
//        };
//    }
//}
