package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.domain.*;
import bitcoin.spring.data.neo4j.services.BitcoinService;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bitcoin")
public class BitcoinController {

    private final BitcoinService bitcoinService;

    public BitcoinController(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    @GetMapping("/getAddress")
    public HttpEntity<Address> getAddress(@RequestParam(name = "address") String address) {
        return this.bitcoinService.findAddress(address);
    }

    @GetMapping("/getBlock")
    public HttpEntity<Block> getBlock(@RequestParam(name = "hash") String hash) {
        return this.bitcoinService.findBlockByHash(hash);
    }

    @GetMapping("/getCoinbase")
    public HttpEntity<Coinbase> getCoinbase(@RequestParam(name = "blockHash") String blockHash) {
        return this.bitcoinService.findCoinbase(blockHash);
    }

    @GetMapping("/getEntity")
    public HttpEntity<Entity> getEntity(@RequestParam(name = "name") String name) {
        return this.bitcoinService.findEntity(name);
    }

    @GetMapping("/getOutput")
    public HttpEntity<Output> getOutput(@RequestParam(name = "id") String id) {
        return this.bitcoinService.findOutput(id);
    }

    @GetMapping("/getTransaction")
    public HttpEntity<Transaction> getTransaction(@RequestParam(name = "txid") String txid) {
        return this.bitcoinService.findTransactionById(txid);
    }
}

