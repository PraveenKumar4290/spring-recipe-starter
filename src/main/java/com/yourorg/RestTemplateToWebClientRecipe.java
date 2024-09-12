package com.yourorg;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.*;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;

import java.util.List;

import static org.openrewrite.java.MethodMatcher.methodPattern;

@Value
@EqualsAndHashCode(callSuper = false)
public class RestTemplateToWebClientRecipe extends Recipe {
    @Override
    public String getDisplayName() {
        return "Refactoring rest-template to webclient";
    }

    @Override
    public String getDescription() {
        return "Refactoring rest-template to webclient.";
    }

    private static final MethodMatcher REST_TEMPLATE_WITH_METHOD = new MethodMatcher("org.springframework.web.client.RestTemplate *()");


    private static final MethodMatcher REST_TEMPLATE_WITH_NEW_CONSTRUCTOR = new MethodMatcher("org.springframework.web.client.RestTemplate <constructor>()");

    private static final MethodMatcher REST_TEMPLATE_WITH_GET_FOR_OBJECT = new MethodMatcher("org.springframework.web.client.RestTemplate getForObject(..)");
    private static final MethodMatcher webClientGetMatcher = new MethodMatcher("org.springframework.web.reactive.function.client.WebClient <constructor>(..)");
    private static final MethodMatcher webClientUriMatcher = new MethodMatcher("org.springframework.web.reactive.function.client.WebClient uri(..)");
    private static final MethodMatcher webClientRetrieveMatcher = new MethodMatcher("org.springframework.web.reactive.function.client.WebClient retrieve(..)");
    private static final MethodMatcher webClientBodyToMonoMatcher = new MethodMatcher("org.springframework.web.reactive.function.client.WebClient bodyToMono(..)");

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new Preconditions.Check(new UsesType<>("org.springframework.web.client.RestTemplate", false),
                new JavaVisitor<ExecutionContext>() {

                    @Override
                    public J visitNewClass(J.NewClass newClass, ExecutionContext ctx) {
                        J visitedNewClass = super.visitNewClass(newClass, ctx);

                        if (TypeUtils.isOfClassType(((J.NewClass) visitedNewClass).getType(), "org.springframework.web.client.RestTemplate")) {

                            if (REST_TEMPLATE_WITH_NEW_CONSTRUCTOR.matches(newClass)) {
                                maybeAddImport("org.springframework.web.reactive.function.client.WebClient");
                                maybeRemoveImport("org.springframework.web.client.RestTemplate");

                                JavaTemplate javaTemplate = JavaTemplate.builder("WebClient.create()")
                                        .imports("org.springframework.web.reactive.function.client.WebClient")
                                        .javaParser(JavaParser.fromJavaVersion()
                                                .classpath("spring-web", "spring-webflux", "reactor-core"))
                                        .build();

                                return javaTemplate.apply(getCursor(), newClass.getCoordinates().replace());
                            }
                        }
                        return visitedNewClass;
                    }

                    @Override
                    public J visitVariableDeclarations(J.VariableDeclarations variableDeclarations, ExecutionContext executionContext) {
                        J.VariableDeclarations vd = (J.VariableDeclarations) super.visitVariableDeclarations(variableDeclarations, executionContext);

                        if (TypeUtils.isOfClassType(vd.getType(), "org.springframework.web.client.RestTemplate")) {
                            maybeRemoveImport("org.springframework.web.client.RestTemplate");
                            maybeAddImport("org.springframework.web.reactive.function.client.WebClient");

                            doAfterVisit(new ChangeFieldType(
                                    vd.getTypeAsFullyQualified().withFullyQualifiedName("org.springframework.web.client.RestTemplate"),
                                    vd.getTypeAsFullyQualified().withFullyQualifiedName("org.springframework.web.reactive.function.client.WebClient"))
                            );
                            for (J.VariableDeclarations.NamedVariable namedVariable : variableDeclarations.getVariables()) {
                                if (TypeUtils.isOfClassType(namedVariable.getType(), "org.springframework.web.client.RestTemplate")) {
                                    doAfterVisit(new RenameVariable<>(namedVariable, "webClient"));
                                }
                            }
                        }
                        return vd;
                    }

//                    @Override
//                    public J visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
//                        J.MethodDeclaration methodDecl = (J.MethodDeclaration) super.visitMethodDeclaration(method, ctx);
//                        if (methodDecl != null && TypeUtils.isOfClassType(methodDecl.getMethodType().getReturnType(), "org.springframework.web.client.RestTemplate")) {
//                            doAfterVisit(new ChangeMethodName(methodPattern(methodDecl), "webClient", false, false).getVisitor());
//                        }
//                        return methodDecl;
//                    }

//                    @Override
//                    public J visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
//                        J.MethodDeclaration methodDecl = (J.MethodDeclaration) super.visitMethodDeclaration(method, ctx);
//                        if (methodDecl != null && TypeUtils.isOfClassType(methodDecl.getMethodType(), "org.springframework.web.client.RestTemplate")) {
//                            List<J.TypeParameter> typeParameters = method.getTypeParameters();
//                        }
//                        return methodDecl;
//                    }
                    @Override
                    public J visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
                        // Check if this is a RestTemplate.getForObject call
                        J.MethodInvocation methodInv = (J.MethodInvocation) super.visitMethodInvocation(method, ctx);
                        if (method.getMethodType() != null && TypeUtils.isOfClassType(method.getMethodType().getDeclaringType(), "org.springframework.web.client.RestTemplate")) {
                            if (method.getSimpleName().equals("getForObject") && REST_TEMPLATE_WITH_GET_FOR_OBJECT.matches(method)) {

                                // Extract the arguments
                                Expression url = method.getArguments().get(0);
                                Expression responseType = method.getArguments().get(1);
                                Expression uriVariables = method.getArguments().size() > 2 ? method.getArguments().get(2) : null;
                                JavaTemplate template = JavaTemplate.
                                        builder("webClient.get().uri(#{any(String)}, #{any()}).retrieve().bodyToMono(#{any()}).block();")
                                        .contextSensitive()
                                        .imports("org.springframework.web.reactive.function.client.WebClient")
                                        .javaParser(JavaParser.fromJavaVersion().classpath("spring-web", "spring-webflux", "reactor-core"))
                                        .build();

                                return template.apply(getCursor(), method.getCoordinates().replace(), new Object[]{url, uriVariables, responseType});
                            }
                            return methodInv;
                        }
                        return methodInv;
                    }



                    //                    @Override
//                    public J.VariableDeclarations.NamedVariable visitVariable(J.VariableDeclarations.NamedVariable variable, ExecutionContext ctx) {
//                        J.VariableDeclarations.NamedVariable visitedVariable = (J.VariableDeclarations.NamedVariable) super.visitVariable(variable, ctx);
//                        if (visitedVariable.getInitializer()!=null){
//                            visitedVariable= (J.VariableDeclarations.NamedVariable) new ChangeType("org.springframework.web.client.RestTemplate",
//                                    "org.springframework.web.reactive.function.client.WebClient",false)
//                                    .getVisitor().visitNonNull(visitedVariable,ctx,getCursor().getParentOrThrow());
//
//                            getCursor().putMessageOnFirstEnclosing(J.VariableDeclarations.class,"replace",true);
//                        }
//                        return visitedVariable;
//                    }

                });

    }
}
