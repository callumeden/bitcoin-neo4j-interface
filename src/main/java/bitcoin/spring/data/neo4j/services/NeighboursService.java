package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.response.AddressNeighboursResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NeighboursService {

    public ResponseEntity<AddressNeighboursResponse> getAddressNeighbours(String address) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
