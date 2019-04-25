package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }
}
