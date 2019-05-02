package bitcoin.spring.data.neo4j.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "COINBASE")
public class Coinbase {

    @Id
    @GeneratedValue
    private Long id;

    private String coinbaseId;

    public String getCoinbaseId() {
        return coinbaseId;
    }

    @JsonIgnoreProperties("coinbase")
    @Relationship(type = "COINBASE", direction = Relationship.INCOMING)
    private Block block;

    @JsonIgnoreProperties("coinbaseInput")
    @Relationship(type = "INPUTS")
    private Transaction inputsTransaction;

    public Block getBlock() {
        return block;
    }

    public Transaction getInputsTransaction() {
        return inputsTransaction;
    }
}
