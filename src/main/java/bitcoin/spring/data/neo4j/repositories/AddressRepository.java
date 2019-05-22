package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Address;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

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

    @Query("MATCH ()-[r:INPUT_HEURISTIC_LINKED_ADDRESSES]-()\n" +
    "DELETE r")
    void deleteClustering();
}
