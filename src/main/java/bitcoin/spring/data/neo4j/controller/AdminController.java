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

    @GetMapping(path = "/clusterByInput")
    public HttpEntity clusterByInput() {
        String[] individualFiles = new String[]{"../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-unique.csv",
                        "../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-unique.csv",
                        "../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-unique.csv",
                        "../health-monitoring/import/data/bitcoin-csv-block-200001-250000/sample-transaction-data-unique.csv",
                        "../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-unique.csv",
                        "../health-monitoring/import/data/bitcoin-csv-block-250001-300000/sample-transaction-data-unique.csv",
                        "../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-unique.csv"};

        return clusteringService.clusterByInput(individualFiles);
    }

    @GetMapping("/deleteClustering")
    public HttpEntity deleteClustering() {
        return clusteringService.deleteClustering();
    }
}
