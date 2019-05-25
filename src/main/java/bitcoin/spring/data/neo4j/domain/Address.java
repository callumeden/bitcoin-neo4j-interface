package bitcoin.spring.data.neo4j.domain;

import bitcoin.spring.data.neo4j.domain.relationships.LockedToRelation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

@NodeEntity(label = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String address;

    public Address() {
    }

    public String getAddress() {
        return address;
    }

    public List<LockedToRelation> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<LockedToRelation> outputs) {
        this.outputs = outputs;
    }

    public Entity getEntity() {
        return entity;
    }


    @JsonIgnoreProperties("lockedToAddress")
    @Relationship(type = "LOCKED_TO", direction = Relationship.INCOMING)
    private List<LockedToRelation> outputs;

    @JsonIgnoreProperties("usesAddresses")
    @Relationship(type = "HAS_ENTITY")
    private Entity entity;

    @JsonIgnoreProperties({"inputHeuristicLinkedAddresses"})
    private Set<Address> inputHeuristicLinkedAddresses;

    public Set<Address> getInputHeuristicLinkedAddresses() {
        return inputHeuristicLinkedAddresses;
    }

    public void setInputHeuristicLinkedAddresses(Set<Address> inputHeuristicLinkedAddresses) {
        if (this.inputHeuristicLinkedAddresses != null) {
            this.inputHeuristicLinkedAddresses.addAll(inputHeuristicLinkedAddresses);
            return;
        }

        this.inputHeuristicLinkedAddresses = inputHeuristicLinkedAddresses;
    }

    private boolean hasLinkedAddresses;

    public boolean isHasLinkedAddresses() {
        return hasLinkedAddresses;
    }

    public void setHasLinkedAddresses(boolean hasLinkedAddresses) {
        this.hasLinkedAddresses = hasLinkedAddresses;
    }
}
