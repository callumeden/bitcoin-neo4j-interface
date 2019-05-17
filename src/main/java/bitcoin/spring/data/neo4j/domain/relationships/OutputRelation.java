package bitcoin.spring.data.neo4j.domain.relationships;

import bitcoin.spring.data.neo4j.domain.Output;
import bitcoin.spring.data.neo4j.domain.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "OUTPUTS")
public class OutputRelation {

    @Id
    @GeneratedValue
    private Long relationshipId;

    @StartNode
    @JsonIgnoreProperties({"inputs", "outputs"})
    private Transaction transaction;

    @JsonIgnoreProperties({"producedByTransaction", "inputsTransaction"})
    @EndNode
    private Output output;

    public Transaction getTransaction() {
        return transaction;
    }

    public Output getOutput() {
        return output;
    }

    public double getGbpValue() {
        double gbpExchangeRate = getTransaction().getMinedInBlock().getGbp();
        double outputValue = getOutput().getValue();
        return gbpExchangeRate * outputValue;
    }

    public double getUsdValue() {
        double usdExchangeRate = getTransaction().getMinedInBlock().getUsd();
        double outputValue = getOutput().getValue();
        return usdExchangeRate * outputValue;
    }

    public double getEurValue() {
        double eurExchangeRate = getTransaction().getMinedInBlock().getEur();
        double outputValue = getOutput().getValue();
        return eurExchangeRate * outputValue;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public double getTimestamp() {
        return transaction.getMinedInBlock().getTimestamp();
    }
}
