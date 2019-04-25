package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Transaction;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends Neo4jRepository<Transaction, Long> {

    Transaction getTransactionByTransactionId(@Param("transactionId") String transactionId);
}
