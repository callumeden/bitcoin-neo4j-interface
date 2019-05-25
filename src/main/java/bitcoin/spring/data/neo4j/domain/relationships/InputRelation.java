package bitcoin.spring.data.neo4j.domain.relationships;

import bitcoin.spring.data.neo4j.domain.Output;
import bitcoin.spring.data.neo4j.domain.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type="INPUTS")
public class InputRelation {

    @Id
    @GeneratedValue
    private Long relationshipId;

    @StartNode
    @JsonIgnoreProperties({"producedByTransaction", "inputsTransaction"})
    private Output input;

    @EndNode
    @JsonIgnoreProperties({"inputs", "outputs"})
    private Transaction transaction;

    public Output getInput() {
        return input;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public double getGbpValue() {
        if (transaction.getMinedInBlock() != null) {
            double inputValue = input.getValue();
            double gbpExchangeRate = transaction.getMinedInBlock().getGbp();
            return inputValue * gbpExchangeRate;
        }

        return -1;
    }

    public double getUsdValue() {
        if (transaction.getMinedInBlock() != null) {
            double inputValue = input.getValue();
            double usdExchangeRate = transaction.getMinedInBlock().getUsd();
            return inputValue * usdExchangeRate;
        }

        return -1;
    }

    public double getEurValue() {
        if (transaction.getMinedInBlock() != null) {
            double inputValue = input.getValue();
            double eurExchangeRate = transaction.getMinedInBlock().getEur();
            return inputValue * eurExchangeRate;
        }

        return -1;
    }

    public long getTimestamp() {
        if (transaction.getMinedInBlock() != null) {
            return transaction.getMinedInBlock().getTimestamp();
        }
        return -1;
    }
}
