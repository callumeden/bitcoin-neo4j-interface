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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Transactional(readOnly = true)
    public Address findAddress(String address, boolean inputClustering, Date start, Date end, String startPrice, String endPrice, String priceUnit) {
        Address addressNode = addressRepository.getAddressByAddress(address);
        boolean hasDateFilter = start != null && end != null;
        boolean hasPriceFilter = startPrice != null && endPrice != null && priceUnit != null;

        if (addressNode == null) {
            return null;
        }

        if (addressNode.getOutputs() != null) {
            Stream<Output> outputStream = addressNode.getOutputs()
                    .stream();

            outputStream = outputStream.map(output -> this.findOutputNode(output.getOutputId(), start, end));

            if (hasDateFilter) {
                outputStream = outputStream
                        .filter(out -> checkTimestampInDateRange(out.getProducedByTransaction().getTimestamp(), start, end));
            }

            if (hasPriceFilter) {
                outputStream = filterOutputStreamByPrice(outputStream, startPrice, endPrice, priceUnit);
            }

            addressNode.setOutputs(outputStream.collect(Collectors.toList()));
        }

        if (inputClustering && addressNode.getEntity() == null) {
            performInputClustering(addressNode, start, end);
        }

        return addressNode;
    }

    private void performInputClustering(Address addressNode, Date start, Date end) {
        Set<Address> linkedAddresses = transitiveInputClustering(addressNode, new HashSet<>(), start, end);
        addressNode.setInputHeuristicLinkedAddresses(linkedAddresses);
    }

    private Set<Address> transitiveInputClustering(Address addressNode, Set<Transaction> exploredTransactions, Date startFilter, Date endFilter) {
        //a stream of transactions which all have inputs locked to this address
        Stream<Transaction> allTransactionsThisAddressInputs = getTransactionsForAddress(addressNode, startFilter, endFilter);
        HashSet<Transaction> thisAddressesTransactions = allTransactionsThisAddressInputs.collect(Collectors.toCollection(HashSet::new));

        //removes all transactions we've already seen
        thisAddressesTransactions.removeAll(exploredTransactions);

        //now adds the new transactions we're about to explore to the explored set
        exploredTransactions.addAll(thisAddressesTransactions);

        //all addresses linked directly (1 transaction hop away) from this address
        Stream<Address> linkedAddressesStream = getAddressesLinkedByTransactions(thisAddressesTransactions.parallelStream(), startFilter, endFilter);
        Set<Address> directlyLinkedAddresses = linkedAddressesStream.collect(Collectors.toSet());

        //all addresses linked transitively (2 transaction hops away) from this address
        Stream<Set<Address>> transitiveAddressStream = directlyLinkedAddresses
                .stream()
                .map(linkedAddress -> transitiveInputClustering(linkedAddress, exploredTransactions, startFilter, endFilter));
        directlyLinkedAddresses.addAll(transitiveAddressStream.flatMap(Set::stream).collect(Collectors.toSet()));

        return directlyLinkedAddresses;
    }

    private Stream<Transaction> getTransactionsForAddress(Address address, Date startFilter, Date endFilter) {
        return address.getOutputs()
                .parallelStream()
                .map(outputShell -> findOutputNode(outputShell.getOutputId(), startFilter, endFilter))
                .filter(outputNode -> outputNode.getInputsTransaction() != null)
                .map(outputNode -> outputNode.getInputsTransaction().getTransaction())
                .map(transactionShell -> findTransaction(transactionShell.getTransactionId()))
                .filter(transactionNode -> transactionNode.getInputs() != null && transactionNode.getInputs().size() > 1);
    }

    private Stream<Address> getAddressesLinkedByTransactions(Stream<Transaction> transactionStream, Date startFilter, Date endFilter) {
        return transactionStream.flatMap(tx -> getAddressesLinkedByTransaction(tx, startFilter, endFilter));
    }
    private Stream<Address> getAddressesLinkedByTransaction(Transaction transaction, Date start, Date end) {
        return transaction.getInputs()
                .parallelStream()
                .map(InputRelation::getInput)
                .map(inputShell -> findOutputNode(inputShell.getOutputId(), start, end))
                .map(Output::getLockedToAddress);
    }

    private Stream<Output> filterOutputStreamByPrice(Stream<Output> outputStream, String startPrice, String endPrice, String priceUnit) {
        switch (priceUnit) {
            case "btc":
                return outputStream.filter(output -> isValueInRange(output.getValue(), startPrice, endPrice));
            case "gbp":
                return outputStream.filter(output -> isValueInRange(output.getProducedByTransaction().getGbpValue(), startPrice, endPrice));
            case "usd":
                return outputStream.filter(output -> isValueInRange(output.getProducedByTransaction().getUsdValue(), startPrice, endPrice));
            case "eur":
                return outputStream.filter(output -> isValueInRange(output.getProducedByTransaction().getEurValue(), startPrice, endPrice));
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
    public Entity findEntity(String name) {
        Entity entityNode = entityRepository.getEntityByName(name);

        entityNode.setUserAddresses(entityNode.getUsesAddresses()
                .stream()
                .map(address -> addressRepository.getAddressByAddress(address.getAddress()))
                .collect(Collectors.toList()));

        return entityNode;
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
                //todo - this no longer works
                Address populatedAddress = addressRepository.getAddressByAddress(outputNode.getLockedToAddress().getAddress());
                if (populatedAddress.getInputHeuristicLinkedAddresses() != null) {
                    populatedAddress.setHasLinkedAddresses(populatedAddress.getInputHeuristicLinkedAddresses().size() > 0);
                }
                outputNode.setLockedToAddress(populatedAddress);
            }
        }
        return outputNode;
    }

    public Transaction findTransaction(String txid, Date startDate, Date endDate, String startPrice, String endPrice, String priceUnit) {
        Transaction transactionNode = findTransaction(txid);
        boolean hasDateFilter = startDate != null && endDate != null;
        boolean hasPriceFilter = startPrice != null && endPrice != null && priceUnit != null;

        if (transactionNode != null) {

            if (transactionNode.getOutputs() != null) {

                Stream<OutputRelation> outputStream = transactionNode.getOutputs().stream();

                if (hasDateFilter) {
                    outputStream = outputStream.filter(output -> checkTimestampInDateRange(output.getTimestamp(), startDate, endDate));
                }

                if (hasPriceFilter) {
                    outputStream = filterOutRelationStreamByPrice(outputStream, startPrice, endPrice, priceUnit);
                }

                if (outputStream != null) {
                    transactionNode.setOutputs(outputStream.collect(Collectors.toList()));
                }
            }

            if (transactionNode.getInputs() != null) {

                Stream<InputRelation> inputStream = transactionNode.getInputs().stream();

                if (hasDateFilter) {
                    inputStream = inputStream.filter(input -> checkTimestampInDateRange(input.getTimestamp(), startDate, endDate));
                }

                if (hasPriceFilter) {
                    inputStream = filterInRelationStreamByPrice(inputStream, startPrice, endPrice, priceUnit);
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
