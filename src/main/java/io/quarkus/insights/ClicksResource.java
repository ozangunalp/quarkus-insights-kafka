package io.quarkus.insights;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;

@Path("/clicks")
public class ClicksResource {

    @Inject
    Logger logger;

    @Channel("clicks")
    MutinyEmitter<Click> clicksEmitter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Void> postClick(Click click) {
        logger.infof("Posted click %s", click);
        return clicksEmitter.send(click);
    }
}
