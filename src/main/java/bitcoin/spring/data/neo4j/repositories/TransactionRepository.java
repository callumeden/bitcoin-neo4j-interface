package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Transaction;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    Transaction getTransactionByTransactionId(@Param("transactionId") String transactionId);

}
