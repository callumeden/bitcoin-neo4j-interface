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
import org.springframework.transaction.annotation.Transactional;
import util.CsvWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    public ResponseEntity deleteClustering() {
        this.addressRepository.deleteClustering();
        System.out.println("Deleted");
        return ResponseEntity.status(200).body("deleted");
    }

    public ResponseEntity clusterByInput(String[] transactionFiles) {
        ExecutorService executorService = Executors.newFixedThreadPool(16);

        for (String file : transactionFiles) {
            executorService.execute(new ClusterRunnable(file));
        }

        executorService.shutdown();

        return ResponseEntity.status(200).body("Completed");
    }

    class ClusterRunnable implements Runnable {
        private String filePath;
        private CsvWriter writer;

        public ClusterRunnable(String file) {
            this.filePath = file;

        }

        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();
            this.writer = new CsvWriter("SAME_OWNER", "./out", getFilePrefix(threadId), Integer.MAX_VALUE);

            System.out.println(this.filePath);

            try (BufferedReader br = Files.newBufferedReader(Paths.get(this.filePath))) {
                String line;
                while ((line = br.readLine()) != null) {

                    String[] data = line.split(",");
                    String txid = data[0];
                    Transaction tx = transactionRepository.getTransactionInputsAndAddressOnly(txid);

                    if (tx.getInputs() == null) {
                        continue;
                    }

                    List<Address> addresses = tx.getInputs()
                            .stream()
                            .map(InputRelation::getInput)
                            .map(input -> input.getLockedToAddress().getAddress())
                            .collect(Collectors.toList());

                    if (addresses.size() > 1) {
                        for (int i = 0; i < addresses.size() - 1; i++) {
                            for (int j = i + 1; j < addresses.size(); j++) {
                                String addressOne = addresses.get(i).getAddress();
                                String addressTwo = addresses.get(j).getAddress();
                                if (addressOne.equals(addressTwo)) {
                                    continue;
                                }
                                writer.write(addresses.get(i).getAddress(), addresses.get(j).getAddress(), "HAS_SAME_OWNER");
                            }
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                System.out.println("finished file " + this.filePath + "............................................");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getFilePrefix(long threadId) {
            Date date= new Date();
            long time = date.getTime();
            return "thread-" + threadId + "-" + this.filePath + "-" + time;
        }
    }
}


