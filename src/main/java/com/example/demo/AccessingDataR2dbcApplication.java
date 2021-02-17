package com.example.demo;

import com.example.demo.domain.Customer;
import com.example.demo.repository.CustomerRepository;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class AccessingDataR2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataR2dbcApplication.class, args);
	}

	@Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

		return initializer;
	}

	@Bean
	public CommandLineRunner dataInitializer(CustomerRepository repository) {
		return args -> {
			var customers = Flux.just("Javier,Beneito", "Eva,Ruiz", "Iñaki,Reta", "Pablo,García")
				.map(n -> n.split(","))
				.map(t -> new Customer(null, t[0], t[1]))
				.flatMap(repository::save);

			repository
				.deleteAll()
				.thenMany(customers)
				.thenMany(repository.findAll())
				.subscribe(System.out::println);
		};
	}

}
