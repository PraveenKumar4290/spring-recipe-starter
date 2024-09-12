//package com.yourorg;
//
//import org.junit.jupiter.api.Test;
//import org.openrewrite.java.JavaParser;
//import org.openrewrite.test.RecipeSpec;
//import org.openrewrite.test.RewriteTest;
//
//import static org.openrewrite.java.Assertions.java;
//
//public class RenameTheClassImplementationMethodsTest implements RewriteTest {
//
//    @Override
//    public void defaults(RecipeSpec spec) {
//        spec.recipe(new RenameTheClassImplementationMethods())
//          .parser(JavaParser.fromJavaVersion()
//            .logCompilationWarningsAndErrors(true));
//    }
//
//    @Test
//    void methodImplementationToGetObject() {
//        // language=java
//        rewriteRun(java("""
//                package com.imag;
//
//                public class HelloDemoClass{
//
//                    public void getForObject(){
//                         System.out.println("Hello : getForObject");
//                    }
//                }
//          """, """
//                package com.imag;
//
//                public class HelloDemoClass{
//
//                    public void getObject() {
//                        System.out.println("Hello : getObject");
//                    }
//                }
//          """));
//    }
//
//    @Test
//    void methodImplementationToExchange() {
//        // language=java
//        rewriteRun(java("""
//                package com.imag;
//
//                public class HelloDemoClass{
//
//                    public void getExchange(){
//                         System.out.println("Hello : getExchange");
//                    }
//                }
//          """, """
//                package com.imag;
//
//                public class HelloDemoClass{
//
//                    public void exchange() {
//                        System.out.println("Hello : exchange");
//                    }
//                }
//          """));
//    }
//
//    @Test
//    void methodImplementationToGetEntity() {
//        // language=java
//        rewriteRun(java("""
//                package com.imag;
//
//                public class HelloDemoClass{
//
//                    public void getForEntity(){
//                         System.out.println("Hello : getForEntity");
//                    }
//                }
//          """, """
//                package com.imag;
//
//                public class HelloDemoClass{
//
//                    public void getEntity() {
//                        System.out.println("Hello : getEntity");
//                    }
//                }
//          """));
//    }
//}