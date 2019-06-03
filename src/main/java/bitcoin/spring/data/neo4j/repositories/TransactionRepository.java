package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Transaction;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    Transaction getTransactionByTransactionId(@Param("transactionId") String transactionId);


    @Query("MATCH (n:TRANSACTION) WHERE \n" +
            "n.transactionId = {0}\n" +
            "OPTIONAL MATCH (n)<-[txInRel:INPUTS]-(txIn:OUTPUT)<-[:OUTPUTS]-(:TRANSACTION)-[:MINED_IN]->(b:BLOCK)\n" +
            "WHERE b.timestamp > {1} AND b.timestamp < {2}\n" +
            "WITH n, txInRel, txIn RETURN n,\n" +
            "[ [ (n)-[txOutRel:OUTPUTS]->(txOut:OUTPUT) | [ txOutRel, txOut ] ], \n" +
            "[ (n)-[minedInRel:MINED_IN]->(minedInBlock:BLOCK) | [ minedInRel, minedInBlock ]], \n" +
            "[  [ txInRel, txIn ] ], \n" +
            "[ (n)<-[txInRelCoin:INPUTS]-(txInCoin:COINBASE) | [ txInRelCoin, txInCoin ] ] ], \n" +
            "ID(n)")
    Transaction getTransactionFilterTime(String transaction, long startTime, long endTime);

}
