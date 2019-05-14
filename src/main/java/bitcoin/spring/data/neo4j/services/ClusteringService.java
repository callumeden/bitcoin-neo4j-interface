package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.Address;
import bitcoin.spring.data.neo4j.domain.Output;
import bitcoin.spring.data.neo4j.domain.Transaction;
import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.repositories.AddressRepository;
import bitcoin.spring.data.neo4j.repositories.OutputRepository;
import bitcoin.spring.data.neo4j.repositories.TransactionRepository;
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
        Iterable<Transaction> allTransactions = transactionRepository.findAll();

        allTransactions.forEach(transaction -> {

            if (transaction.getInputs() == null || transaction.getInputs().size() < 2) {
                //coinbase input or only one input
                return;//dw just skips this iteration only
            }

            List<InputRelation> transactionInputs = transaction.getInputs();
            Set<Address> addressesSpendingTransactionInputs = new HashSet<>();

            transactionInputs.forEach(inputRelation -> {

                Output transactionInput = inputRelation.getInput();
                Output refetchedTransactionInput = this.outputRepository.getOutputByOutputId(transactionInput.getOutputId());
                Address addressSpendingTransactionInput = refetchedTransactionInput.getLockedToAddress();

                if (addressSpendingTransactionInput != null && !addressesSpendingTransactionInputs.contains(addressSpendingTransactionInput)) {
                    addressesSpendingTransactionInputs.forEach(sameUserAddress -> sameUserAddress.addInputHeuristicLinkedAddresses(addressSpendingTransactionInput));
                    addressesSpendingTransactionInputs.add(addressSpendingTransactionInput);
                }
            });

            //Saves the updated addresses back to Neo4J repo
            addressesSpendingTransactionInputs.forEach(this.addressRepository::save);

        });
        return ResponseEntity.status(200).body("Clustering Complete");
    }
}
