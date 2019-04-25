package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String address;

    public String getAddress() {
        return address;
    }
}
