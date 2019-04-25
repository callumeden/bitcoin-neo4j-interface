package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

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
}
