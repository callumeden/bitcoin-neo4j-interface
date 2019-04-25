package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "COINBASE")
public class Coinbase {

    @Id
    @GeneratedValue
    private Long id;

    private String coinbaseId;

    public String getCoinbaseId() {
        return coinbaseId;
    }
}
