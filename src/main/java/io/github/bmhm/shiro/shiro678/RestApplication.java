package io.github.bmhm.shiro.shiro678;

import org.apache.shiro.web.jaxrs.ShiroFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ShiroFeature.class);
        classes.add(JaxRsEndpoint.class);
        return classes;
    }
}
