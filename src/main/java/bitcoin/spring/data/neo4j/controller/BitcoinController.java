package bitcoin.spring.data.neo4j.controller;

import bitcoin.spring.data.neo4j.services.BitcoinService;
import bitcoin.spring.data.neo4j.services.PathFinderService;
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
    private final PathFinderService pathFinderService;

    public BitcoinController(BitcoinService bitcoinService, PathFinderService pathFinderService) {
        this.bitcoinService = bitcoinService;
        this.pathFinderService = pathFinderService;
    }

    @GetMapping("/address/{address}")
    @CrossOrigin
    public HttpEntity getAddress(@PathVariable("address") String address,
                                 @RequestParam(value = "startTime", required = false) String startDateString,
                                 @RequestParam(value = "endTime", required = false) String endDateString,
                                 @RequestParam(value = "startPrice", required = false) String startPrice,
                                 @RequestParam(value = "endPrice", required = false) String endPrice,
                                 @RequestParam(value = "priceUnit", required = false) String priceUnit,
                                 @RequestParam(value="inputClustering", required = false) boolean inputClustering,
                                 @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {

        Date startDate = parseDate(startDateString);
        Date endDate = parseDate(endDateString);

        return entityOrNotFound(this.bitcoinService.findAddress(address, inputClustering, startDate, endDate, startPrice, endPrice, priceUnit, nodeLimit));
    }

    private Date parseDate(String dateString) {
        if (dateString != null) {
            return parseDateFromTimestamp(dateString);
        }
        return null;
    }

    @GetMapping("/block/{hash}")
    @CrossOrigin
    public HttpEntity getBlock(@PathVariable("hash") String hash) {
        return entityOrNotFound(this.bitcoinService.findBlockByHash(hash));
    }

    @GetMapping("/coinbase/{coinbaseId}")
    @CrossOrigin
    public HttpEntity getCoinbase(@PathVariable("coinbaseId") String coinbaseId) {
        return entityOrNotFound(this.bitcoinService.findCoinbase(coinbaseId));
    }

    @GetMapping("/entity/{name}")
    @CrossOrigin
    public HttpEntity getEntity(@PathVariable("name") String name,
                                @RequestParam(value = "startTime", required = false) String startDateString,
                                @RequestParam(value = "endTime", required = false) String endDateString,
                                @RequestParam(value = "startPrice", required = false) String startPrice,
                                @RequestParam(value = "endPrice", required = false) String endPrice,
                                @RequestParam(value = "priceUnit", required = false) String priceUnit,
                                @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {
        Date startDate = parseDate(startDateString);
        Date endDate = parseDate(endDateString);
        return entityOrNotFound(this.bitcoinService.findEntity(name, startDate, endDate, startPrice, endPrice, priceUnit, nodeLimit));
    }

    private <T> ResponseEntity entityOrNotFound(T result) {
        return result == null ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find entity") :
                new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/output/{id}")
    @CrossOrigin
    public HttpEntity getOutput(@PathVariable("id") String id,
                                @RequestParam(value = "startTime", required = false) String startTime,
                                @RequestParam(value = "endTime", required = false) String endTime,
                                @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {

        Date startDate = parseDate(startTime);
        Date endDate = parseDate(endTime);

        return entityOrNotFound(this.bitcoinService.findOutputNodeCheckIfCanCluster(id, startDate, endDate, nodeLimit));
    }

    @GetMapping("/transaction/{txid}")
    @CrossOrigin
    public HttpEntity getTransaction(@PathVariable("txid") String txid,
                                     @RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime,
                                     @RequestParam(value="startPrice", required = false) String startPrice,
                                     @RequestParam(value="endPrice", required = false) String endPrice,
                                     @RequestParam(value = "priceUnit", required = false) String priceUnit,
                                     @RequestParam(value = "nodeLimit", required = false) Integer nodeLimit) {

        Date filterStartTime = parseDate(startTime);
        Date filterEndTime = parseDate(endTime);

        return entityOrNotFound(this.bitcoinService.findTransaction(txid, filterStartTime, filterEndTime, startPrice, endPrice, priceUnit, nodeLimit));
    }

    private Date parseDateFromTimestamp(String timestamp) {
        return Date.from(Instant.ofEpochMilli(Long.valueOf(timestamp)));
    }

    @GetMapping("/shortestPath/{sourceAddress}/{destinationAddress}")
    @CrossOrigin
    public HttpEntity getShortestPath(@PathVariable("sourceAddress") String sourceAddress,
                                      @PathVariable("destinationAddress") String destinationAddress) {
        return entityOrNotFound(this.pathFinderService.getShortestPath(sourceAddress, destinationAddress));
    }

}

