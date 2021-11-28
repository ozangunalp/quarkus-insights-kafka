package io.quarkus.insights;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("/clicks")
public class ClicksResource {

    @Inject
    Logger logger;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void postClick(Click click) {
        logger.infof("Posted click %s", click);
    }
}
