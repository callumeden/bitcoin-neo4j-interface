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

    public double getGbpValue() {
        double inputValue = input.getValue();
        double gbpExchangeRate = transaction.getMinedInBlock().getGbp();
        return inputValue * gbpExchangeRate;
    }

    public double getUsdValue() {
        double inputValue = input.getValue();
        double usdExchangeRate = transaction.getMinedInBlock().getUsd();
        return inputValue * usdExchangeRate;
    }

    public double getEurValue() {
        double inputValue = input.getValue();
        double eurExchangeRate = transaction.getMinedInBlock().getEur();
        return inputValue * eurExchangeRate;
    }
}
