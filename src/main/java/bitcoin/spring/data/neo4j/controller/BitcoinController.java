package bitcoin.spring.data.neo4j.controller;

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

    @GetMapping("/getAddress/{address}")
    @CrossOrigin
    public HttpEntity getAddress(@PathVariable("address") String address) {
        System.out.println("get address");
        return this.bitcoinService.findAddress(address);
    }

    @GetMapping("/getBlock/{hash}")
    @CrossOrigin
    public HttpEntity getBlock(@PathVariable("hash") String hash) {
        return this.bitcoinService.findBlockByHash(hash);
    }

    @GetMapping("/getCoinbase/{coinbaseId}")
    @CrossOrigin
    public HttpEntity getCoinbase(@PathVariable("coinbaseId") String coinbaseId) {
        return this.bitcoinService.findCoinbase(coinbaseId);
    }

    @GetMapping("/getEntity/{name}")
    @CrossOrigin
    public HttpEntity getEntity(@PathVariable("name") String name) {
        return this.bitcoinService.findEntity(name);
    }

    @GetMapping("/getOutput/{id}")
    @CrossOrigin
    public HttpEntity getOutput(@PathVariable("id") String id) {
        return this.bitcoinService.findOutput(id);
    }

    @GetMapping("/getTransaction/{txid}")
    @CrossOrigin
    public HttpEntity getTransaction(@PathVariable("txid") String txid) {
        return this.bitcoinService.findTransactionById(txid);
    }

}

