package ng.upperlink.nibss.cmms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * Created by toyin.oladele on 14/10/2017.
 * To enable session management with Redis server.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 5000, redisNamespace = "mysession")
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

}