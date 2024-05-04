package ng.upperlink.nibss.cmms.exceptions;

import org.glassfish.jersey.server.ResourceConfig;

//@Component
public class JerseyConfig extends ResourceConfig
{
    private static final String BASE_PACKAGE ="ng.upperlink.nibss.cmms.";
private static final String BASE_PACKAGE_EXCEPTION =BASE_PACKAGE+"exceptions.";
    public JerseyConfig()
    {
        packages(
                BASE_PACKAGE+"controller",
                BASE_PACKAGE_EXCEPTION+"emandate",
                BASE_PACKAGE_EXCEPTION+"mcash",
                BASE_PACKAGE_EXCEPTION+"validator",
                BASE_PACKAGE+"filter"
        );
//        register(UserResource.class);
//        register(MessageResources.class);
    }
}