package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.services.BitcoinService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;

@RepositoryRestController
@RequestMapping("/bitcoin")
public class BitcoinController {

    private final BitcoinService bitcoinService;

    public BitcoinController(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    @GetMapping("/getAddress/{address}")
    @CrossOrigin
    public HttpEntity getAddress(@PathVariable("address") String address,
                                 @RequestParam(value = "startTime", required = false) String startTime,
                                 @RequestParam(value = "endTime", required = false) String endTime) {
        if (startTime != null && endTime != null) {
            Date startDate = parseDateFromTimestamp(startTime);
            Date endDate = parseDateFromTimestamp(endTime);
            return entityOrNotFound(this.bitcoinService.findAddress(address, startDate, endDate));
        }

        return entityOrNotFound(this.bitcoinService.findAddress(address, null, null));
    }

    @GetMapping("/getBlock/{hash}")
    @CrossOrigin
    public HttpEntity getBlock(@PathVariable("hash") String hash) {
        return entityOrNotFound(this.bitcoinService.findBlockByHash(hash));
    }

    @GetMapping("/getCoinbase/{coinbaseId}")
    @CrossOrigin
    public HttpEntity getCoinbase(@PathVariable("coinbaseId") String coinbaseId) {
        return entityOrNotFound(this.bitcoinService.findCoinbase(coinbaseId));
    }

    @GetMapping("/getEntity/{name}")
    @CrossOrigin
    public HttpEntity getEntity(@PathVariable("name") String name) {
        return entityOrNotFound(this.bitcoinService.findEntity(name));
    }

    private <T> ResponseEntity entityOrNotFound(T result) {
        return result == null ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find entity") :
                new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getOutput/{id}")
    @CrossOrigin
    public HttpEntity getOutput(@PathVariable("id") String id,
                                @RequestParam(value = "startTime", required = false) String startTime,
                                @RequestParam(value = "endTime", required = false) String endTime) {

        if (startTime != null && endTime != null) {
            Date startDate = parseDateFromTimestamp(startTime);
            Date endDate = parseDateFromTimestamp(endTime);
            return entityOrNotFound(this.bitcoinService.findOutputFilterByDate(id, startDate, endDate));
        }

        return entityOrNotFound(this.bitcoinService.findOutputNode(id));
    }

    @GetMapping("/getTransaction/{txid}")
    @CrossOrigin
    public HttpEntity getTransaction(@PathVariable("txid") String txid,
                                     @RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime) {

        if (startTime != null && endTime != null) {
            Date startDate = parseDateFromTimestamp(startTime);
            Date endDate = parseDateFromTimestamp(endTime);
            return entityOrNotFound(this.bitcoinService.findTransactionByIdFilterByDate(txid, startDate, endDate));
        }
        return entityOrNotFound(this.bitcoinService.findTransaction(txid));
    }

    private Date parseDateFromTimestamp(String timestamp) {
        return Date.from(Instant.ofEpochMilli(Long.valueOf(timestamp)));
    }

}

