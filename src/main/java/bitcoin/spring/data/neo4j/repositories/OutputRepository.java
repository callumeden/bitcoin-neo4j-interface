package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Output;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface OutputRepository extends Neo4jRepository<Output, Long> {

    Output getOutputByOutputId(@Param("outputId") String outputId);

    @Query("MATCH (n:OUTPUT) WHERE\n" +
            "n.outputId = {0}\n" +
            "OPTIONAL MATCH (n)-[lockedToRel:LOCKED_TO]->(a:ADDRESS)\n" +
            "WITH n, lockedToRel, a RETURN n,\n" +
            "[[  [ lockedToRel, a ] ]]")
    Output getOutputAndAddressOnly(String output);
}
