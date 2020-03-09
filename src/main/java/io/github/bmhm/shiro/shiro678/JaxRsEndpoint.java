package io.github.bmhm.shiro.shiro678;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class JaxRsEndpoint {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doLogin(
        @DefaultValue("") @FormParam("l_username") final String username, // login username
        @DefaultValue("") @FormParam("l_password") final String password // login password
    ) {
        Map<String, String> receivedData = new ConcurrentHashMap<>();
        receivedData.put("l_username", username);
        receivedData.put("l_password", password);

        return Response.ok()
            .entity(unmodifiableMap(receivedData))
            .build();
    }

}