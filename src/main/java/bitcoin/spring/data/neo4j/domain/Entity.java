package bitcoin.spring.data.neo4j.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity(label = "ENTITY")
public class Entity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public String getName() {
        return name;
    }

    @JsonIgnoreProperties("entity")
    @Relationship(type = "HAS_ENTITY", direction = Relationship.INCOMING)
    private List<Address> usesAddresses;

    public List<Address> getUsesAddresses() {
        return usesAddresses;
    }
}
