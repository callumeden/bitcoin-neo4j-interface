package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity(label = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    private String transactionId;

    public Block getMinedInBlock() {
        return minedInBlock;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Relationship(type = "MINED_IN", direction = Relationship.OUTGOING)
    private Block minedInBlock;

    @Relationship(type = "INPUTS", direction = Relationship.INCOMING)
    private List<Output> inputs;

    @Relationship(type = "INPUTS", direction = Relationship.INCOMING)
    private Coinbase coinbaseInput;

    public List<Output> getInputs() {
        return inputs;
    }

    public Coinbase getCoinbaseInput() {
        return coinbaseInput;
    }
}
