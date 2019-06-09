package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.services.ClusteringService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestController
@RequestMapping("/admin")
public class AdminController {

    private final ClusteringService clusteringService;

    public AdminController(ClusteringService clusteringService) {
        this.clusteringService = clusteringService;
    }

    @GetMapping(path = "/clusterByInput")
    public HttpEntity clusterByInput(@RequestParam("data") String transactionFiles) {
        String[] individualFiles = transactionFiles.split(",");
        return clusteringService.clusterByInput(individualFiles);
    }

    @GetMapping("/deleteClustering")
    public HttpEntity deleteClustering() {
        return clusteringService.deleteClustering();
    }
}
