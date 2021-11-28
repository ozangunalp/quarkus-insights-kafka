package io.quarkus.insights;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ClicksProcessor {

    @Inject
    Logger logger;

    @Incoming("clicks-in")
    public void processClick(Click click) {
        logger.infof("Received click %s", click);
    }
}
