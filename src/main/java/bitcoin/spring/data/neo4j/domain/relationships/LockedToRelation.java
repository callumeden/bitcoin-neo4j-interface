package bitcoin.spring.data.neo4j.domain.relationships;

import bitcoin.spring.data.neo4j.domain.Address;
import bitcoin.spring.data.neo4j.domain.Output;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "LOCKED_TO")
public class LockedToRelation {

    @Id
    @GeneratedValue
    private Long relationshipId;

    @JsonIgnoreProperties({"lockedToAddress", "producedByTransaction", "inputsTransaction"})
    @StartNode
    private Output output;

    @JsonIgnoreProperties({"outputs", "inputHeuristicLinkedAddresses"})
    @EndNode
    private Address address;

    public Output getOutput() {
        return output;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setOutput(Output output) {
        this.output = output;
    }
}
