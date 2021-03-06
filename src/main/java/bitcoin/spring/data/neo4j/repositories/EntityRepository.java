package bitcoin.spring.data.neo4j.repositories;

import bitcoin.spring.data.neo4j.domain.Entity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface EntityRepository extends Neo4jRepository<Entity, Long> {

    Entity getEntityByName(@Param("name") String name);

    @Query("MATCH (n:ENTITY)\n" +
            "WHERE n.name = {0}\n" +
            "OPTIONAL MATCH (n)<-[entityRel:HAS_ENTITY]-(a:ADDRESS)<-[:LOCKED_TO]-(o:OUTPUT)-[:INPUTS]->(:TRANSACTION)-[:MINED_IN]->(b:BLOCK) \n" +
            "WHERE b.timestamp > {1} AND b.timestamp < {2}\n" +
            "WITH n, entityRel, a, b RETURN n, b,\n" +
            "[ [ [ entityRel, a ] ] ], \n" +
            "ID(n)")
    Entity getEntityAddressFiltered(String name, long start, long end);

    @Query("MATCH (n:ENTITY)\n" +
            "WHERE n.name = {0}\n" +
            "OPTIONAL MATCH (n)<-[entityRel:HAS_ENTITY]-(a:ADDRESS)<-[:LOCKED_TO]-(o:OUTPUT)-[:INPUTS]->(:TRANSACTION)-[:MINED_IN]->(b:BLOCK) \n" +
            "WHERE b.timestamp > {1} AND b.timestamp < {2}\n" +
            "WITH n, entityRel, a, b RETURN n, b,\n" +
            "[ [ [ entityRel, a ] ] ], \n" +
            "ID(n) LIMIT {3}")
    Entity getEntityAddressFiltered(String name, long start, long end, int limit);


    @Query("MATCH (n:ENTITY)\n" +
            "WHERE n.name = {0}\n" +
            "OPTIONAL MATCH (n)<-[entityRel:HAS_ENTITY]-(a:ADDRESS)<-[:LOCKED_TO]-(o:OUTPUT)\n" +
            "WHERE o.value > {1} AND o.value < {2}\n" +
            "WITH n, entityRel, a RETURN n, \n" +
            "[ [ [ entityRel, a ] ] ],\n" +
            "ID(n) LIMIT 100")
    Entity getEntityaddressPriceFiltered(String name, double startPrice, double endPrice);

    @Query("MATCH (n:ENTITY)\n" +
            "WHERE n.name = {0}\n" +
            "OPTIONAL MATCH (n)<-[entityRel:HAS_ENTITY]-(a:ADDRESS)<-[:LOCKED_TO]-(o:OUTPUT)-[:INPUTS]->(:TRANSACTION)-[:MINED_IN]->(b:BLOCK)\n" +
            "WHERE o.value > {3} AND o.value < {4} AND b.timestamp > {1} AND b.timestamp < {2}\n" +
            "WITH n, entityRel, a, o RETURN n,o,\n" +
            "[ [ [ entityRel, a ] ] ],\n" +
            "ID(n) LIMIT 100")
    Entity getEntityAddressPriceAndTimeFiltered(String name, long startTime, long endTime, double startPrice, double endPrice);

    @Query("MATCH (n:ENTITY) WHERE n.name = {0} WITH n RETURN n,\n" +
            "[ [ (n)<-[r_h1:HAS_ENTITY]-(a1:ADDRESS) | [ r_h1, a1 ] ] ], \n" +
            "ID(n)\n" +
            "LIMIT {1}")
    Entity getEntityByNameAndLimit(String name, int nodeLimit);
}
