package bitcoin.spring.data.neo4j.services;

import bitcoin.spring.data.neo4j.domain.*;
import bitcoin.spring.data.neo4j.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseEntity<Block> findBlockByHash(String hash) {
        return entityOrNotFound(blockRepository.findByHash(hash));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Transaction> findTransactionById(String txid) {
        return entityOrNotFound(transactionRepository.getTransactionByTransactionId(txid));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Address> findAddress(String address) {
        return entityOrNotFound(addressRepository.getAddressByAddress(address));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Coinbase> findCoinbase(String coinbaseId) {
        return entityOrNotFound(coinbaseRepository.getCoinbaseByCoinbaseId(coinbaseId));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Entity> findEntity(String name) {
        return entityOrNotFound(entityRepository.getEntityByName(name));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Output> findOutput(String id) {
        return entityOrNotFound(outputRepository.getOutputByOutputId(id));
    }

    private <T> ResponseEntity<T> entityOrNotFound(T result) {
        return result == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(result, HttpStatus.OK);
    }


}
