package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.services.ClusteringService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping("/admin")
public class AdminController {

    private final ClusteringService clusteringService;

    public AdminController(ClusteringService clusteringService) {
        this.clusteringService = clusteringService;
    }

    @GetMapping("/clusterByInput")
    public HttpEntity clusterByInput() {
        return clusteringService.clusterByInput();
    }
}
