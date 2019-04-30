package bitcoin.spring.data.neo4j.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping("/bitcoin/neighbours")
public class NeighboursController {

    @GetMapping("/address/{address}")
    public String getAddressNeighbours(@PathVariable("address") String address) {
        return "";
    }
}
