// package org.curastone.Crafty;
//
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
//
// @SpringBootApplication
// public class Main {
//
//    public static void main(String[] args) {
//        SpringApplication.run(Main.class, args);
//    }
//
// }

package org.curastone.Crafty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Main {

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
