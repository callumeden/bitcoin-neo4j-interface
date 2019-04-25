package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "ENTITY")
public class Entity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public String getName() {
        return name;
    }
}
