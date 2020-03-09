package io.github.bmhm.shiro.shiro678;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.stream.JsonParser;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JaxRsEndpointIT {

    private static final Logger LOG = LoggerFactory.getLogger(JaxRsEndpointIT.class);
    private static final String LOGIN_PASSWORD = "pw_äöüßñç¿? clef: \uD834\uDD1E";

    private static String URL;

    @BeforeAll
    public static void initJaxRsEndpointIT() {
        final String port = System.getProperty("http.port");
        final String war = System.getProperty("war.name");
        URL = "http://localhost:" + port + "/" + war + "/api";
        LOG.info("Target URL is: [{}].", URL);
    }

    /**
     * No cookies, no Unicode.
     *
     * @throws IOException
     */
    @Test
    public void testPostResponse() throws IOException {
        Form input = new Form();
        input.param("l_username", "user");
        input.param("l_password", LOGIN_PASSWORD);

        final Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
        final WebTarget webTarget = client.target(URL).path("login");
        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        final Response response = invocationBuilder.post(Entity.entity(input, MediaType.APPLICATION_FORM_URLENCODED));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final String responseEntity = response.readEntity(String.class);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseEntity.getBytes(StandardCharsets.UTF_8))) {
            final JsonParser jsonParser = Json.createParser(byteArrayInputStream);
            jsonParser.next();
            final JsonObject object = jsonParser.getObject();
            final JsonString passwordValue = (JsonString) object.get("l_password");

            assertEquals(LOGIN_PASSWORD, passwordValue.getString());
        }
    }

    /**
     * Adds an invalid JSESSIONID cookie. Magic: now it works!
     *
     * @throws IOException
     *     error reading response.
     */
    @Test
    public void testPostResponse_withCookie() throws IOException {
        Form input = new Form();
        input.param("l_username", "user");
        input.param("l_password", LOGIN_PASSWORD);

        final Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
        final WebTarget webTarget = client.target(URL).path("login");
        /* Adding this cookie will print org.apache.shiro.session.UnknownSessionException: There is no session with id [0],
         * but reading UTF-8 suddenly works!
         */
        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
            .cookie("JSESSIONID", "0");
        final Response response = invocationBuilder.post(Entity.entity(input, MediaType.APPLICATION_FORM_URLENCODED));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final String responseEntity = response.readEntity(String.class);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseEntity.getBytes(StandardCharsets.UTF_8))) {
            final JsonParser jsonParser = Json.createParser(byteArrayInputStream);
            jsonParser.next();
            final JsonObject object = jsonParser.getObject();
            final JsonString passwordValue = (JsonString) object.get("l_password");

            assertEquals(LOGIN_PASSWORD, passwordValue.getString());
        }
    }
}
