package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.*;
import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.domain.relationships.OutputRelation;
import bitcoin.spring.data.neo4j.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BitcoinService {

    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;
    private final AddressRepository addressRepository;
    private final CoinbaseRepository coinbaseRepository;
    private final EntityRepository entityRepository;
    private final OutputRepository outputRepository;

    @Autowired
    public BitcoinService(BlockRepository blockRepository, TransactionRepository transactionRepository, AddressRepository addressRepository, CoinbaseRepository coinbaseRepository, EntityRepository entityRepository, OutputRepository outputRepository) {
        this.blockRepository = blockRepository;
        this.transactionRepository = transactionRepository;
        this.addressRepository = addressRepository;
        this.coinbaseRepository = coinbaseRepository;
        this.entityRepository = entityRepository;
        this.outputRepository = outputRepository;
    }

    @Transactional(readOnly = true)
    public Block findBlockByHash(String hash) {
        return blockRepository.findByHash(hash);
    }


    public Transaction findTransaction(String txid) {
        return transactionRepository.getTransactionByTransactionId(txid);
    }

    @Transactional(readOnly = true)
    public Address findAddress(String address, Date start, Date end) {
        Address addressNode = addressRepository.getAddressByAddress(address);

        if (addressNode != null && addressNode.getOutputs() != null) {
            Stream<Output> outputStream = addressNode.getOutputs()
                    .stream();

            if (start != null && end != null) {
                outputStream = outputStream
                        .map(output -> findOutputFilterByDate(output.getOutputId(), start, end))
                        .filter(out -> checkTimestampInDateRange(out.getProducedByTransaction().getTimestamp(), start, end));
            } else {
                outputStream = outputStream.map(output -> this.findOutputNode(output.getOutputId()));
            }

            addressNode.setOutputs(outputStream.collect(Collectors.toList()));
        }

        return addressNode;
    }

    @Transactional(readOnly = true)
    public Coinbase findCoinbase(String coinbaseId) {
        return coinbaseRepository.getCoinbaseByCoinbaseId(coinbaseId);
    }

    @Transactional(readOnly = true)
    public Entity findEntity(String name) {
        return entityRepository.getEntityByName(name);
    }

    @Transactional(readOnly = true)
    public Output findOutputNode(String id) {
        Output output = outputRepository.getOutputByOutputId(id);

        if (output != null) {
            //This makes the output relation have a full transaction in its relation, so it contains its block
            //to fetch the exchange rates from
            OutputRelation outRelation = output.getProducedByTransaction();

            if (outRelation != null) {
                Transaction producedByTx = outRelation.getTransaction();
                Transaction fullProducedByTx = findTransaction(producedByTx.getTransactionId());
                outRelation.setTransaction(fullProducedByTx);
            }

            InputRelation inRelation = output.getInputsTransaction();
            if (inRelation != null) {
                Transaction inputsTx = inRelation.getTransaction();
                Transaction fullInputsTx = findTransaction(inputsTx.getTransactionId());
                inRelation.setTransaction(fullInputsTx);
            }
        }
        return output;
    }

    public Transaction findTransactionByIdFilterByDate(String txid, Date start, Date end) {
        Transaction transactionNode = findTransaction(txid);

        if (transactionNode != null && transactionNode.getOutputs() != null) {
            transactionNode.setOutputs(transactionNode.getOutputs()
                    .stream()
                    .filter(output -> checkTimestampInDateRange(output.getTimestamp(), start, end))
                    .collect(Collectors.toList()));
        }

        if (transactionNode != null && transactionNode.getInputs() != null) {
            transactionNode.setInputs(transactionNode.getInputs()
                    .stream()
                    .filter(input -> checkTimestampInDateRange(input.getTimestamp(), start, end))
                    .collect(Collectors.toList()));
        }

        return transactionNode;
    }

    public Output findOutputFilterByDate(String id, Date start, Date end) {
        Output outputNode = findOutputNode(id);

        if (outputNode != null && outputNode.getInputsTransaction() != null) {

            long txTimestamp = outputNode.getInputsTransaction().getTimestamp();
            if (!checkTimestampInDateRange(txTimestamp, start, end)) {
                outputNode.setInputsTransaction(null);
            }
        }

        return outputNode;
    }

    private boolean checkTimestampInDateRange(long timestamp, Date start, Date end) {
        Date producedAt = Date.from(Instant.ofEpochSecond(timestamp));

        return producedAt.compareTo(start) >= 0 && end.compareTo(producedAt) >= 0;
    }

}
