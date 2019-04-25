package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Coinbase;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface CoinbaseRepository extends Neo4jRepository<Coinbase, Long> {

    Coinbase getCoinbaseByCoinbaseId(@Param("coinbaseId") String coinbaseId);
}
