package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Address;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends Neo4jRepository<Address, Long> {

    Address getAddressByAddress(@Param("address") String address);

}
