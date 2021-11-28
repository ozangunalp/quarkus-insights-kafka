package io.quarkus.insights;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ClicksProcessor {

    @Inject
    Logger logger;

    @Incoming("clicks-in")
    @Outgoing("clicks-out")
    public Uni<ClickDTO> processClick(Click click) {
        logger.infof("Persisting click %s", click);
        return Panache.withTransaction(() -> persist(click));
    }

    private Uni<ClickDTO> persist(Click click) {
        return new ClickDTO(click).persist();
    }
}
