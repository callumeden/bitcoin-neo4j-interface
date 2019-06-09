package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.services.ClusteringService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        String transactionFiles = "../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-63-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-64-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-65-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-66-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-67-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-68-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-69-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-70-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-71-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-72-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-73-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-74-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-75-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-76-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-77-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-thread-78-0.csv,../health-monitoring/import/data/bitcoin-csv-block-0-200000/sample-transaction-data-unique.csv,../health-monitoring/import/data/bitcoin-csv-block-200001-250000/sample-transaction-data-0.csv,../health-monitoring/import/data/bitcoin-csv-block-200001-250000/sample-transaction-data-10000001.csv,../health-monitoring/import/data/bitcoin-csv-block-200001-250000/sample-transaction-data-unique.csv,../health-monitoring/import/data/bitcoin-csv-block-250001-300000/sample-transaction-data-0.csv,../health-monitoring/import/data/bitcoin-csv-block-250001-300000/sample-transaction-data-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-250001-300000/sample-transaction-data-unique.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-70-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-71-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-72-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-73-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-74-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-75-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-76-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-77-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-78-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-79-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-80-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-81-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-82-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-83-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-84-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-thread-85-0.csv,../health-monitoring/import/data/bitcoin-csv-block-300001-350000/sample-transaction-data-unique.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-68-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-69-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-70-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-71-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-72-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-73-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-74-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-75-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-76-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-77-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-78-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-79-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-80-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-81-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-82-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-thread-83-0.csv,../health-monitoring/import/data/bitcoin-csv-block-350001-375000/sample-transaction-data-unique.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-69-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-70-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-71-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-73-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-77-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-79-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-80-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-82-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-85-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-85-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-86-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-90-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-91-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-92-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-92-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-93-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-94-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-thread-95-0.csv,../health-monitoring/import/data/bitcoin-csv-block-375001-450000/sample-transaction-data-unique.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-67-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-67-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-70-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-71-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-71-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-72-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-72-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-73-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-73-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-74-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-74-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-75-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-75-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-76-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-76-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-79-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-79-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-80-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-81-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-81-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-82-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-82-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-83-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-83-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-84-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-84-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-85-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-85-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-86-0.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-thread-86-10000000.csv,../health-monitoring/import/data/bitcoin-csv-block-450001-570000/sample-transaction-data-unique.csv";
        String[] individualFiles = transactionFiles.split(",");
        return clusteringService.clusterByInput(individualFiles);
    }

    @GetMapping("/deleteClustering")
    public HttpEntity deleteClustering() {
        return clusteringService.deleteClustering();
    }
}
