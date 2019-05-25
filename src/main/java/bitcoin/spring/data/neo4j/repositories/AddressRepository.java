package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Address;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface AddressRepository extends Neo4jRepository<Address, Long> {

    Address getAddressByAddress(@Param("address") String address);

    @Query("MATCH (t:TRANSACTION)\n" +
            "WHERE size((t)<-[:INPUTS]-()) > 1\n" +
            "WITH [(t)<-[:INPUTS]-(:OUTPUT)-[:LOCKED_TO]->(a:ADDRESS) | a] as addresses\n" +
            "UNWIND addresses as first\n" +
            "UNWIND addresses as second\n" +
            "WITH addresses, first, second\n" +
            "WHERE id(first) < id(second)\n" +
            "MERGE (first)-[:INPUTS_SAME_TX]-(second)")
    void performClustering();

    @Query("PROFILE MATCH (a:ADDRESS)\n" +
            "OPTIONAL MATCH (a)-[r:INPUTS_SAME_TX]-(:ADDRESS)\n" +
            "DELETE r\n" +
            "RETURN count(r)")
    void deleteClustering();

    @Query("MATCH " +
            "(a1:ADDRESS{address:{0}})<-[:LOCKED_TO]-(outStart:OUTPUT)-[:INPUTS]->(:TRANSACTION), " +
            "(a2:ADDRESS{address:{1}})<-[:LOCKED_TO]-(outEnd:OUTPUT)<-[:OUTPUTS]-(:TRANSACTION), " +
            "p = shortestPath((outStart)-[:INPUTS |:OUTPUTS |:LOCKED_TO*..1000]-(outEnd)) " +
            "RETURN a1 as startNode, nodes(p) as intermediateNodes, relationships(p) as rels" +
            ", a2 as endNode")
    Iterable<Map<String, Object>> shortestPath(String sourceAddress, String destinationAddress);


}
