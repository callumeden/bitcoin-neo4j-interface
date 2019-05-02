package bitcoin.spring.data.neo4j.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "OUTPUT")
public class Output {

    @Id
    @GeneratedValue
    private Long id;

    private String outputId;
    private double value;

    public String getOutputId() {
        return outputId;
    }

    public double getValue() {
        return value;
    }

    public Transaction getProducedByTransaction() {
        return producedByTransaction;
    }

    @JsonIgnoreProperties({"outputs", "inputs"})
    @Relationship(type = "OUTPUTS", direction = Relationship.INCOMING)
    private Transaction producedByTransaction;

    @Relationship(type = "INPUTS")
    private Transaction inputsTransaction;

    @JsonIgnoreProperties("outputs")
    @Relationship(type = "LOCKED_TO")
    private Address lockedToAddress;

    public Address getLockedToAddress() {
        return lockedToAddress;
    }

    public Transaction getInputsTransaction() {
        return inputsTransaction;
    }
}
