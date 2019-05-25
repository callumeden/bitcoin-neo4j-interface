package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.*;
import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.domain.relationships.LockedToRelation;
import bitcoin.spring.data.neo4j.domain.relationships.OutputRelation;
import bitcoin.spring.data.neo4j.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
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

    @Transactional(readOnly = true)
    public Transaction findTransaction(String txid) {
        return transactionRepository.getTransactionByTransactionId(txid);
    }

    private Address findAddress(String address, Date start, Date end, String startPrice, String endPrice, String priceUnit, Integer nodeLimit) {
        Address addressNode = addressRepository.getAddressByAddress(address);
        boolean hasDateFilter = start != null && end != null;
        boolean hasPriceFilter = startPrice != null && endPrice != null && priceUnit != null;

        if (addressNode == null) {
            return null;
        }

        if (addressNode.getOutputs() != null) {
            Stream<LockedToRelation> outputStream = addressNode.getOutputs().parallelStream();

            outputStream = outputStream.peek(outputRelation -> {
                Output outNode = this.findOutputNode(outputRelation.getOutput().getOutputId(), start, end);
                outputRelation.setOutput(outNode);
            });

            if (hasDateFilter) {
                outputStream = outputStream
                        .filter(out -> checkTimestampInDateRange(out.getOutput().getProducedByTransaction().getTimestamp(), start, end));
            }

            if (hasPriceFilter) {
                outputStream = filterOutputStreamByPrice(outputStream, startPrice, endPrice, priceUnit);
            }

            if (nodeLimit != null && outputStream != null) {
                System.out.println("ADDRESS TRUNCATING ...........................................");
                outputStream = outputStream.limit(nodeLimit);
            }

            if (outputStream != null) {
                addressNode.setOutputs(outputStream.collect(Collectors.toList()));
            }

        }
        return addressNode;
    }

    @Transactional(readOnly = true)
    public Address findAddress(String address, boolean inputClustering, Date start, Date end, String startPrice, String endPrice, String priceUnit, Integer nodeLimit) {
        Address addressNode = findAddress(address, start, end, startPrice, endPrice, priceUnit, nodeLimit);

        if (inputClustering && addressNode.getEntity() == null) {
            performInputClustering(addressNode, start, end, nodeLimit);
        }

        return addressNode;
    }

    private Set<Address> performInputClustering(Address addressNode, Date start, Date end, Integer nodeLimit) {
        Set<Address> linkedAddresses = transitiveInputClustering(addressNode, new HashSet<>(), start, end, nodeLimit);
        addressNode.setInputHeuristicLinkedAddresses(linkedAddresses);
        return linkedAddresses;
    }

    private Set<Address> transitiveInputClustering(Address addressNode, Set<Transaction> exploredTransactions, Date startFilter, Date endFilter, Integer nodeLimit) {
        //a stream of transactions which all have inputs locked to this address

        if (nodeLimit != null && exploredTransactions.size() > nodeLimit) {
            return new HashSet<>();
        }

        Stream<Transaction> allTransactionsThisAddressInputs = getTransactionsForAddress(addressNode, startFilter, endFilter, nodeLimit);
        HashSet<Transaction> thisAddressesTransactions = allTransactionsThisAddressInputs.collect(Collectors.toCollection(HashSet::new));

        //removes all transactions we've already seen
        thisAddressesTransactions.removeAll(exploredTransactions);

        //now adds the new transactions we're about to explore to the explored set
        exploredTransactions.addAll(thisAddressesTransactions);

        //all addresses linked directly (1 transaction hop away) from this address
        Stream<Address> linkedAddressesStream = getAddressesLinkedByTransactions(thisAddressesTransactions.parallelStream(), startFilter, endFilter, nodeLimit);
        Set<Address> directlyLinkedAddresses = linkedAddressesStream.collect(Collectors.toSet());

        //all addresses linked transitively (2 transaction hops away) from this address
        Stream<Set<Address>> transitiveAddressStream = directlyLinkedAddresses
                .stream()
                .map(linkedAddress -> transitiveInputClustering(linkedAddress, exploredTransactions, startFilter, endFilter, nodeLimit));
        directlyLinkedAddresses.addAll(transitiveAddressStream.flatMap(Set::stream).collect(Collectors.toSet()));

        return directlyLinkedAddresses;
    }

    private Stream<Transaction> getTransactionsForAddress(Address address, Date startFilter, Date endFilter, Integer nodeLimit) {
        Stream<LockedToRelation> outputStream = address.getOutputs()
                .parallelStream();

        if (nodeLimit != null) {
            outputStream = outputStream.limit(nodeLimit);
        }

        return outputStream.map(outputShell -> findOutputNode(outputShell.getOutput().getOutputId(), startFilter, endFilter))
                .filter(outputNode -> outputNode.getInputsTransaction() != null)
                .map(outputNode -> outputNode.getInputsTransaction().getTransaction())
                .map(transactionShell -> findTransaction(transactionShell.getTransactionId()))
                .filter(transactionNode -> transactionNode.getInputs() != null && transactionNode.getInputs().size() > 1);
    }

    private Stream<Address> getAddressesLinkedByTransactions(Stream<Transaction> transactionStream, Date startFilter, Date endFilter, Integer nodeLimit) {
        return transactionStream.flatMap(tx -> getAddressesLinkedByTransaction(tx, startFilter, endFilter, nodeLimit));
    }

    private Stream<Address> getAddressesLinkedByTransaction(Transaction transaction, Date start, Date end, Integer nodeLimit) {
        Stream<InputRelation> inputStream = transaction.getInputs()
                .parallelStream();

        if (nodeLimit != null) {
            inputStream = inputStream.limit(nodeLimit);
        }

        return inputStream.map(InputRelation::getInput)
                .map(inputShell -> findOutputNode(inputShell.getOutputId(), start, end))
                .map(output -> output.getLockedToAddress().getAddress());
    }

    private Stream<LockedToRelation> filterOutputStreamByPrice(Stream<LockedToRelation> outputStream, String startPrice, String endPrice, String priceUnit) {
        switch (priceUnit) {
            case "btc":
                return outputStream.filter(output -> isValueInRange(output.getOutput().getValue(), startPrice, endPrice));
            case "gbp":
                return outputStream.filter(output -> isValueInRange(output.getOutput().getProducedByTransaction().getGbpValue(), startPrice, endPrice));
            case "usd":
                return outputStream.filter(output -> isValueInRange(output.getOutput().getProducedByTransaction().getUsdValue(), startPrice, endPrice));
            case "eur":
                return outputStream.filter(output -> isValueInRange(output.getOutput().getProducedByTransaction().getEurValue(), startPrice, endPrice));
        }

        return null;
    }

    private Stream<OutputRelation> filterOutRelationStreamByPrice(Stream<OutputRelation> stream, String startPrice, String endPrice, String priceUnit) {
        switch (priceUnit) {
            case "btc":
                return stream.filter(output -> isValueInRange(output.getOutput().getValue(), startPrice, endPrice));
            case "gbp":
                return stream.filter(output -> isValueInRange(output.getGbpValue(), startPrice, endPrice));
            case "usd":
                return stream.filter(output -> isValueInRange(output.getUsdValue(), startPrice, endPrice));
            case "eur":
                return stream.filter(output -> isValueInRange(output.getEurValue(), startPrice, endPrice));
        }

        return null;
    }

    private Stream<InputRelation> filterInRelationStreamByPrice(Stream<InputRelation> stream, String startPrice, String endPrice, String priceUnit) {
        switch (priceUnit) {
            case "btc":
                return stream.filter(inputRelation -> isValueInRange(inputRelation.getInput().getValue(), startPrice, endPrice));
            case "gbp":
                return stream.filter(output -> isValueInRange(output.getGbpValue(), startPrice, endPrice));
            case "usd":
                return stream.filter(output -> isValueInRange(output.getUsdValue(), startPrice, endPrice));
            case "eur":
                return stream.filter(output -> isValueInRange(output.getEurValue(), startPrice, endPrice));
        }

        return null;
    }

    private boolean isValueInRange(double value, String start, String end) {
        return value >= Double.valueOf(start) && value <= Double.valueOf(end);
    }

    @Transactional(readOnly = true)
    public Coinbase findCoinbase(String coinbaseId) {
        return coinbaseRepository.getCoinbaseByCoinbaseId(coinbaseId);
    }

    @Transactional(readOnly = true)
    public Entity findEntity(String name, Date start, Date end, String startPrice, String endPrice, String priceUnit, Integer nodeLimit) {
        Entity entityNode = entityRepository.getEntityByName(name);

        Stream<Address> linkedAddressStream = entityNode.getUsesAddresses()
                .parallelStream();

        if (nodeLimit != null) {
            linkedAddressStream = linkedAddressStream.limit(nodeLimit);
        }

        linkedAddressStream = linkedAddressStream.map(address -> findAddress(address.getAddress(), start, end, startPrice, endPrice, priceUnit, nodeLimit));

        entityNode.setUserAddresses(linkedAddressStream.collect(Collectors.toList()));

        return entityNode;
    }

    public Output findOutputNodeCheckIfCanCluster(String id, Date startDate, Date endDate) {
        Output outputNode = findOutputNode(id, startDate, endDate);

        if (outputNode.getLockedToAddress() != null) {
            Address addressNode = outputNode.getLockedToAddress().getAddress();
            Set<Address> clusteredAddresses = performInputClustering(addressNode, startDate, endDate, 1);
            addressNode.setHasLinkedAddresses(clusteredAddresses.size() > 0);
        }

        return outputNode;
    }
    @Transactional(readOnly = true)
    public Output findOutputNode(String id, Date startDate, Date endDate) {
        Output outputNode = outputRepository.getOutputByOutputId(id);
        boolean hasDateFilter = startDate != null && endDate != null;

        if (outputNode != null) {
            //This makes the output relation have a full transaction in its relation, so it contains its block
            //to fetch the exchange rates from
            OutputRelation outRelation = outputNode.getProducedByTransaction();

            if (outRelation != null) {
                Transaction producedByTx = outRelation.getTransaction();
                Transaction fullProducedByTx = findTransaction(producedByTx.getTransactionId());
                outRelation.setTransaction(fullProducedByTx);
            }

            InputRelation inRelation = outputNode.getInputsTransaction();
            if (inRelation != null) {

                Transaction inputsTx = inRelation.getTransaction();
                Transaction fullInputsTx = findTransaction(inputsTx.getTransactionId());
                inRelation.setTransaction(fullInputsTx);

                long txTimestamp = inRelation.getTimestamp();

                if (hasDateFilter && !checkTimestampInDateRange(txTimestamp, startDate, endDate)) {
                    outputNode.setInputsTransaction(null);
                }
            }

            if (outputNode.getLockedToAddress() != null) {
                Address populatedAddress = addressRepository.getAddressByAddress(outputNode.getLockedToAddress().getAddress().getAddress());
                outputNode.getLockedToAddress().setAddress(populatedAddress);
            }
        }
        return outputNode;
    }

    public Transaction findTransaction(String txid, Date startDate, Date endDate, String startPrice, String endPrice, String priceUnit, Integer nodeLimit) {
        Transaction transactionNode = findTransaction(txid);
        boolean hasDateFilter = startDate != null && endDate != null;
        boolean hasPriceFilter = startPrice != null && endPrice != null && priceUnit != null;

        if (transactionNode != null) {

            if (transactionNode.getOutputs() != null) {

                Stream<OutputRelation> outputStream = transactionNode.getOutputs().parallelStream();

                if (hasDateFilter) {
                    outputStream = outputStream.filter(output -> checkTimestampInDateRange(output.getTimestamp(), startDate, endDate));
                }

                if (hasPriceFilter) {
                    outputStream = filterOutRelationStreamByPrice(outputStream, startPrice, endPrice, priceUnit);
                }

                if (nodeLimit != null && outputStream != null) {
                    outputStream = outputStream.limit(nodeLimit);
                }

                if (outputStream != null) {
                    transactionNode.setOutputs(outputStream.collect(Collectors.toList()));
                }
            }

            if (transactionNode.getInputs() != null) {

                Stream<InputRelation> inputStream = transactionNode.getInputs().parallelStream();


                if (hasDateFilter) {
                    inputStream = inputStream.filter(input -> checkTimestampInDateRange(input.getTimestamp(), startDate, endDate));
                }

                if (hasPriceFilter) {
                    inputStream = filterInRelationStreamByPrice(inputStream, startPrice, endPrice, priceUnit);
                }

                if (nodeLimit != null && inputStream != null) {
                    inputStream = inputStream.limit(nodeLimit);
                }

                if (inputStream != null) {
                    transactionNode.setInputs(inputStream.collect(Collectors.toList()));
                }
            }

        }


        return transactionNode;
    }


    private boolean checkTimestampInDateRange(long timestamp, Date start, Date end) {
        Date producedAt = Date.from(Instant.ofEpochSecond(timestamp));
        return producedAt.compareTo(start) >= 0 && end.compareTo(producedAt) >= 0;
    }

}
