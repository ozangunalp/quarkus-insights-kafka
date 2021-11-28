package io.quarkus.insights;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Blocking;

@ApplicationScoped
public class ClicksProcessor {

    @Inject
    Logger logger;

    @Blocking
    @Transactional
    @Incoming("clicks-in")
    public void processClick(Click click) {
        new ClickDTO(click).persist();
        logger.infof("Persisted click %s", click);
    }
}
