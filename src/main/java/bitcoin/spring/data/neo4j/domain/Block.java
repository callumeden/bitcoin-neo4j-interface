package bitcoin.spring.data.neo4j.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

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

    @JsonIgnoreProperties("minedInBlock")
    @Relationship(type = "MINED_IN", direction = Relationship.INCOMING)
    private List<Transaction> minedTransactions;

    @JsonIgnoreProperties("parent")
    @Relationship(type = "CHAINED_FROM", direction = Relationship.INCOMING)
    private Block child;

    @JsonIgnoreProperties("child")
    @Relationship(type = "CHAINED_FROM")
    private Block parent;

    @JsonIgnoreProperties("block")
    @Relationship(type = "COINBASE")
    private Coinbase coinbase;

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

    public List<Transaction> getMinedTransactions() {
        return minedTransactions;
    }

    public Block getChild() {
        return child;
    }

    public Coinbase getCoinbase() {
        return coinbase;
    }

    public Block getParent() {
        return parent;
    }
}
