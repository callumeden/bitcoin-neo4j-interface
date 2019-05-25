package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.Address;
import bitcoin.spring.data.neo4j.domain.Output;
import bitcoin.spring.data.neo4j.domain.Transaction;
import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.repositories.AddressRepository;
import bitcoin.spring.data.neo4j.repositories.OutputRepository;
import bitcoin.spring.data.neo4j.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClusteringService {
    private final AddressRepository addressRepository;
    private final TransactionRepository transactionRepository;
    private OutputRepository outputRepository;

    public ClusteringService(AddressRepository addressRepository, TransactionRepository transactionRepository, OutputRepository outputRepository) {
        this.addressRepository = addressRepository;
        this.transactionRepository = transactionRepository;
        this.outputRepository = outputRepository;
    }


    @Transactional(readOnly = true)
    public Output getOutputById(String id) {
        return this.outputRepository.getOutputByOutputId(id);
    }

    public ResponseEntity clusterByInput() {
        this.addressRepository.performClustering();
        System.out.println("Clustering complete");
        return ResponseEntity.status(200).body("Accepted");
    }

    public ResponseEntity deleteClustering() {
        this.addressRepository.deleteClustering();
        System.out.println("Deleted");
        return ResponseEntity.status(200).body("deleted");
    }

    public ResponseEntity clusterByInputOld() {
        Pageable pageable = PageRequest.of(0, 1);

        while (true) {
            Page<Transaction> allTransactions = transactionRepository.findAll(pageable);
            allTransactions.forEach(transaction -> {

                if (transaction.getInputs() == null || transaction.getInputs().size() < 2) {
                    //coinbase input or only one input
                    return;//dw just skips this iteration only
                }

                List<InputRelation> transactionInputs = transaction.getInputs();
                Set<Address> addressesSpendingTransactionInputs = new HashSet<>();

                transactionInputs.forEach(inputRelation -> {
                    String inputId = inputRelation.getInput().getOutputId();

                    Output refetchedTransactionInput = getOutputById(inputId);
                    Address addressSpendingTransactionInput = refetchedTransactionInput.getLockedToAddress().getAddress();

                    if (addressSpendingTransactionInput != null) {
                        addressesSpendingTransactionInputs.add(addressSpendingTransactionInput);
                    }
                });

                addressesSpendingTransactionInputs.forEach(address -> {
                    address.setInputHeuristicLinkedAddresses(addressesSpendingTransactionInputs);
                    // Saves the updated addresses back to Neo4J repo
                    this.addressRepository.save(address, 0);
                });

                System.out.println("completed for tx" + transaction.getTransactionId());

            });

            if (!allTransactions.hasNext()) {
                break;
            }

            pageable = allTransactions.nextPageable();
        }

        return ResponseEntity.status(200).body("Clustering Complete");
    }
}
