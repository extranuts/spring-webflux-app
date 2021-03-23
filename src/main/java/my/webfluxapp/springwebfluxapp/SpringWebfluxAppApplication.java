package my.webfluxapp.springwebfluxapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxAppApplication {

	static{
		BlockHound.install();
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxAppApplication.class, args);
	}

}
