package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PathFinderService {

    private AddressRepository addressRepository;

    @Autowired
    public PathFinderService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Iterable<Map<String, Object>> getShortestPath(String sourceAddress, String destinationAddress) {
        Iterable<Map<String, Object>> paths = addressRepository.shortestPath(sourceAddress, destinationAddress);

        paths.forEach(System.out::println);

        return paths;
    }
}
