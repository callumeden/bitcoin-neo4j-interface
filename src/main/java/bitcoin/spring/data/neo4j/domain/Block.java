package bitcoin.spring.data.neo4j.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "BLOCK")
public class Block {

    @Id
    @GeneratedValue
    private Long id;

    private String hash;
    private String prevBlockHash;
    private int timestamp;
    private int size;
    private double gbp;
    private double usd;
    private double eur;

    public String getHash() {
        return hash;
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getSize() {
        return size;
    }

    public double getGbp() {
        return gbp;
    }

    public double getUsd() {
        return usd;
    }

    public double getEur() {
        return eur;
    }
}
