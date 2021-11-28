package io.quarkus.insights;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Blocking;

@ApplicationScoped
public class ClicksProcessor {

    @Inject
    Logger logger;

    @Blocking
    @Transactional
    @Incoming("clicks-in")
    @Outgoing("clicks-out")
    public ClickDTO processClick(Click click) {
        ClickDTO clickDTO = new ClickDTO(click);
        clickDTO.persist();
        logger.infof("Persisted click %s", click);
        return clickDTO;
    }
}
