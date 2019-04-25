package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Entity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface EntityRepository extends Neo4jRepository<Entity, Long> {

    Entity getEntityByName(@Param("name") String name);

}
