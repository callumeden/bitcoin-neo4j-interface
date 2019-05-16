package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.Address;
import bitcoin.spring.data.neo4j.domain.Output;
import bitcoin.spring.data.neo4j.domain.Transaction;
import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.repositories.AddressRepository;
import bitcoin.spring.data.neo4j.repositories.OutputRepository;
import bitcoin.spring.data.neo4j.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    public ResponseEntity clusterByInput() {
        Pageable pageable = PageRequest.of(0, 50);

        while (true) {
            Page<Transaction> allTransactions = transactionRepository.findAll(pageable);
            System.out.println("*********** Processing page " + allTransactions.getNumber());
            allTransactions.forEach(transaction -> {

                if (transaction.getInputs() == null || transaction.getInputs().size() < 2) {
                    //coinbase input or only one input
                    return;//dw just skips this iteration only
                }

                List<InputRelation> transactionInputs = transaction.getInputs();
                Set<Address> addressesSpendingTransactionInputs = new HashSet<>();

                transactionInputs.forEach(inputRelation -> {
                    Output transactionInput = inputRelation.getInput();
                    System.out.println("Output ID -------> " + transactionInput.getOutputId());

                    Output refetchedTransactionInput = this.outputRepository.getOutputByOutputId(transactionInput.getOutputId());
                    Address addressSpendingTransactionInput = refetchedTransactionInput.getLockedToAddress();

                    if (addressSpendingTransactionInput != null && !addressesSpendingTransactionInputs.contains(addressSpendingTransactionInput)) {
                        addressesSpendingTransactionInputs.forEach(sameUserAddress -> {
                            System.out.println("test");
                            sameUserAddress.addInputHeuristicLinkedAddresses(addressSpendingTransactionInput);
                        });

                        addressesSpendingTransactionInputs.add(addressSpendingTransactionInput);
                    }
                });

                // Saves the updated addresses back to Neo4J repo
                addressesSpendingTransactionInputs.forEach(updatedAddress -> {
                    System.out.println("saving address" + updatedAddress.getAddress());
                    this.addressRepository.save(updatedAddress);
                });

            });

            if (!allTransactions.hasNext()) {
                break;
            }

            pageable = allTransactions.nextPageable();
        }

        return ResponseEntity.status(200).body("Clustering Complete");
    }
}
