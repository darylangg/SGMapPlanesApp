package org.sgmap.planes;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableEncryptableProperties
@PropertySource(name="EncryptedProperties", value = "classpath:encrypted.properties")
public class SGMapPlanesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SGMapPlanesAppApplication.class, args);
	}

}
