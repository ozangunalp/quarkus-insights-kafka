package io.quarkus.insights;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;

@ApplicationScoped
public class ClicksProcessor {

    @Inject
    Logger logger;

    @Inject
    PgPool pgClient;

    @Incoming("clicks-in")
    @Outgoing("clicks-out")
    public Uni<List<ClickDTO>> processClick(List<Click> clicks) {
        logger.infof("Persisting click %s", clicks);
        return persist(clicks);
    }

    public static String sqlInsert = "INSERT INTO clicks (userId, xpath) VALUES ($1, $2)";

    private Uni<List<ClickDTO>> persist(List<Click> clicks) {
        return pgClient.withTransaction(conn ->
                conn.preparedQuery(sqlInsert)
                        .executeBatch(clicks.stream().map(c -> Tuple.of(c.getUserId(), c.getXpath()))
                                .collect(Collectors.toList())))
                .replaceWith(() -> clicks.stream().map(ClickDTO::new).collect(Collectors.toList()));
    }
}
