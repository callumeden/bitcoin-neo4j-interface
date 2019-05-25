package bitcoin.spring.data.neo4j.domain;

import bitcoin.spring.data.neo4j.domain.relationships.InputRelation;
import bitcoin.spring.data.neo4j.domain.relationships.LockedToRelation;
import bitcoin.spring.data.neo4j.domain.relationships.OutputRelation;
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

    public OutputRelation getProducedByTransaction() {
        return producedByTransaction;
    }

    @JsonIgnoreProperties("output")
    @Relationship(type = "OUTPUTS", direction = Relationship.INCOMING)
    private OutputRelation producedByTransaction;

    @JsonIgnoreProperties("input")
    @Relationship(type = "INPUTS")
    private InputRelation inputsTransaction;

    @JsonIgnoreProperties({"outputs", "inputHeuristicLinkedAddresses"})
    @Relationship(type = "LOCKED_TO")
    private LockedToRelation lockedToAddress;

    public LockedToRelation getLockedToAddress() {
        return lockedToAddress;
    }

    public InputRelation getInputsTransaction() {
        return inputsTransaction;
    }

    public void setInputsTransaction(InputRelation inputsTransaction) {
        this.inputsTransaction = inputsTransaction;
    }

}
