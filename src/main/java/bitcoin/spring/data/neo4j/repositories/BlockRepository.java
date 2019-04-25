package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Block;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface BlockRepository extends Neo4jRepository<Block, Long> {

    Block findByHash(@Param("hash") String hash);

}
