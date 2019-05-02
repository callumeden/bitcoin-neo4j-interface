package bitcoin.spring.data.neo4j.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonIgnoreProperties({"producedByTransaction", "inputsTransaction"})
    @Relationship(type = "INPUTS", direction = Relationship.INCOMING)
    private List<Output> inputs;

    @JsonIgnoreProperties("inputsTransaction")
    @Relationship(type = "INPUTS", direction = Relationship.INCOMING)
    private Coinbase coinbaseInput;

    @JsonIgnoreProperties("producedByTransaction")
    @Relationship(type = "OUTPUTS")
    private List<Output> outputs;

    public List<Output> getInputs() {
        return inputs;
    }

    public Coinbase getCoinbaseInput() {
        return coinbaseInput;
    }

    public List<Output> getOutputs() {
        return outputs;
    }
}
