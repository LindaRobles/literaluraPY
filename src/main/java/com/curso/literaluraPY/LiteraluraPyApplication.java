package com.curso.literaluraPY;

import com.curso.literaluraPY.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class LiteraluraPyApplication implements CommandLineRunner {


	public static void main(String[] args) {

		SpringApplication.run(LiteraluraPyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal();
		principal.muestraElMenu();
	}
}
