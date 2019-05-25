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
        if (transaction.getMinedInBlock() != null) {
            double gbpExchangeRate =transaction.getMinedInBlock().getGbp();
            double outputValue = output.getValue();
            return gbpExchangeRate * outputValue;
        }

        return -1;

    }

    public double getUsdValue() {
        if (transaction.getMinedInBlock() != null) {
            double usdExchangeRate = transaction.getMinedInBlock().getUsd();
            double outputValue = output.getValue();
            return usdExchangeRate * outputValue;
        }

        return -1;
    }

    public double getEurValue() {
        if (transaction.getMinedInBlock() != null) {
            double eurExchangeRate = transaction.getMinedInBlock().getEur();
            double outputValue = output.getValue();
            return eurExchangeRate * outputValue;
        }
        return -1;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public long getTimestamp() {
        if (transaction.getMinedInBlock() != null) {
            return transaction.getMinedInBlock().getTimestamp();
        }

        return -1;
    }
}
