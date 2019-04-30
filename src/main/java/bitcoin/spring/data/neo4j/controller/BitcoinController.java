package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.domain.*;
import bitcoin.spring.data.neo4j.services.BitcoinService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RepositoryRestController
@RequestMapping("/bitcoin")
public class BitcoinController {

    private final BitcoinService bitcoinService;

    public BitcoinController(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    @GetMapping("/getAddress")
    @CrossOrigin
    public HttpEntity<Address> getAddress(@RequestParam(name = "address") String address) {
        return this.bitcoinService.findAddress(address);
    }

    @GetMapping("/getBlock")
    @CrossOrigin
    public HttpEntity<Block> getBlock(@RequestParam(name = "hash") String hash) {
        return this.bitcoinService.findBlockByHash(hash);
    }

    @GetMapping("/getCoinbase")
    public HttpEntity<Coinbase> getCoinbase(@RequestParam(name = "blockHash") String blockHash) {
        return this.bitcoinService.findCoinbase(blockHash);
    }

    @GetMapping("/getEntity")
    @CrossOrigin
    public HttpEntity<Entity> getEntity(@RequestParam(name = "name") String name) {
        return this.bitcoinService.findEntity(name);
    }

    @GetMapping("/getOutput")
    @CrossOrigin
    public HttpEntity<Output> getOutput(@RequestParam(name = "id") String id) {
        return this.bitcoinService.findOutput(id);
    }

    @GetMapping("/getTransaction")
    @CrossOrigin
    public HttpEntity<Transaction> getTransaction(@RequestParam(name = "txid") String txid) {
        return this.bitcoinService.findTransactionById(txid);
    }

}

