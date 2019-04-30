package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity(label = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String address;

    public String getAddress() {
        return address;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    @Relationship(type = "LOCKED_TO", direction = Relationship.INCOMING)
    private List<Output> outputs;


    @Relationship(type = "HAS_ENTITY", direction = Relationship.OUTGOING)
    private List<Entity> entities;
}
