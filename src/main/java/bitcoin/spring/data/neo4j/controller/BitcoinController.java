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
                                 @RequestParam(value = "endTime", required = false) String endTime,
                                 @RequestParam(value = "startPrice", required = false) String startPrice,
                                 @RequestParam(value = "endPrice", required = false) String endPrice,
                                 @RequestParam(value = "priceUnit", required = false) String priceUnit,
                                 @RequestParam(value="inputClustering", required = false) boolean inputClustering,
                                 @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {

        Date startDate = null;
        Date endDate = null;

        if (startTime != null && endTime != null) {
            startDate = parseDateFromTimestamp(startTime);
            endDate = parseDateFromTimestamp(endTime);
        }

        return entityOrNotFound(this.bitcoinService.findAddress(address, inputClustering, startDate, endDate, startPrice, endPrice, priceUnit, nodeLimit));
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
    public HttpEntity getEntity(@PathVariable("name") String name,
                                @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {
        return entityOrNotFound(this.bitcoinService.findEntity(name, nodeLimit));
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

        Date startDate = null;
        Date endDate = null;

        if (startTime != null && endTime != null) {
            startDate = parseDateFromTimestamp(startTime);
            endDate = parseDateFromTimestamp(endTime);
        }


        return entityOrNotFound(this.bitcoinService.findOutputNode(id, startDate, endDate));
    }

    @GetMapping("/getTransaction/{txid}")
    @CrossOrigin
    public HttpEntity getTransaction(@PathVariable("txid") String txid,
                                     @RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime,
                                     @RequestParam(value="startPrice", required = false) String startPrice,
                                     @RequestParam(value="endPrice", required = false) String endPrice,
                                     @RequestParam(value = "priceUnit", required = false) String priceUnit,
                                     @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {

        Date filterStartTime = null;
        Date filterEndTime = null;

        if (startTime != null && endTime != null) {
            filterStartTime = parseDateFromTimestamp(startTime);
            filterEndTime = parseDateFromTimestamp(endTime);
        }


        return entityOrNotFound(this.bitcoinService.findTransaction(txid, filterStartTime, filterEndTime, startPrice, endPrice, priceUnit, nodeLimit));
    }

    private Date parseDateFromTimestamp(String timestamp) {
        return Date.from(Instant.ofEpochMilli(Long.valueOf(timestamp)));
    }

}

