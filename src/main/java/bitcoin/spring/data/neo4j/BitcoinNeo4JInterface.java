package bitcoin.spring.data.neo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * @author Michael Hunger
 * @author Mark Angrish
 */
@SpringBootApplication
@EnableNeo4jRepositories("bitcoin.spring.data.neo4j.repositories")
public class BitcoinNeo4JInterface {

    public static void main(String[] args) {
        SpringApplication.run(BitcoinNeo4JInterface.class, args);
    }
}