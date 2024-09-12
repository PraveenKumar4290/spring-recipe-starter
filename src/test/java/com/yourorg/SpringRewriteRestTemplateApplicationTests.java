package com.yourorg;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;


class SpringRewriteRestTemplateApplicationTests implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RestTemplateToWebClientRecipe())
          .parser(JavaParser.fromJavaVersion()
            .classpath("spring-web", "spring-webflux", "reactor-core")
            .logCompilationWarningsAndErrors(true));
    }

    @Test
    public void refactoringRestTemplateToWebClient() {

        rewriteRun(
          // language=java
          java(
            """
              package com.imag;
              import org.springframework.web.client.RestTemplate;
              
              public class DemoService{
              
                  private final RestTemplate restTemplate = new RestTemplate();
              
                  public void getPostById(Long id) {
              
                  }
              }
              """,
            """ 
              package com.imag;
              import org.springframework.web.reactive.function.client.WebClient;
              
              public class DemoService{
              
                  private final WebClient webClient = WebClient.create();
              
                  public void getPostById(Long id) {
              
                  }
              }
              """
          )
        );
    }

    @Test
    public void refactoringRestMethodToWebMethod() {

        rewriteRun(
          // language=java
          java(
            """
              package com.imag;
              import org.springframework.web.client.RestTemplate;
              
              public class DemoService{
              
                  private final RestTemplate restTemplate = new RestTemplate();
              
                  public void getPostById(Long id) {
                       restTemplate.getForObject("http://localhost:8080/url",String.class,id);
                  }
              }
              """,
            """ 
              package com.imag;
              import org.springframework.web.reactive.function.client.WebClient;
              
              public class DemoService{
              
                  private final WebClient webClient = WebClient.create();
              
                  public void getPostById(Long id) {
                       webClient.get().uri("http://localhost:8080/url", id).retrieve().bodyToMono(String.class).block();
                  }
              }
              """
          )
        );
    }

}
