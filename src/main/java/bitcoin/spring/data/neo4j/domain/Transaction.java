package bitcoin.spring.data.neo4j.domain;

import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.domain.relationships.OutputRelation;
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

    @JsonIgnoreProperties("transaction")
    @Relationship(type = "INPUTS", direction = Relationship.INCOMING)
    private List<InputRelation> inputs;

    @JsonIgnoreProperties("inputsTransaction")
    @Relationship(type = "INPUTS", direction = Relationship.INCOMING)
    private Coinbase coinbaseInput;

    @JsonIgnoreProperties("transaction")
    @Relationship(type = "OUTPUTS")
    private List<OutputRelation> outputs;

    public List<InputRelation> getInputs() {
        return inputs;
    }

    public Coinbase getCoinbaseInput() {
        return coinbaseInput;
    }

    public List<OutputRelation> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputRelation> outputs) {
        this.outputs = outputs;
    }

    public void setInputs(List<InputRelation> inputs) {
        this.inputs = inputs;
    }
}
