package ng.upperlink.nibss.cmms.config.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Repository
public class UserLoginCacheService {

    private static final String KEY = "userCmmsLogin";
    private static final Logger logger = LoggerFactory.getLogger(ng.upperlink.nibss.cmms.config.cache.UserLoginCacheService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    private SessionManager sessionManager;

    @Value("${token-timeout}")
    private int tokenTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public String findSession(String username) {
        return (String)hashOperations.get(KEY+username, username);
    }

    public boolean isUserLogged(String username){

        //get the session id by username
        String session = findSession(username);
        if (session == null){
            //user is not logged in
            return false;
        }

        if (sessionManager.isValidateSession(session)) {
            //user is still logged in
            return true;
        }

        return false;
    }

    public String getLoggedUserSession(String username){
        //get the session id by username
        String session = findSession(username);
        if (session == null){
            //user is not logged in
            return null;
        }

        if (sessionManager.isValidateSession(session)) {
            //user is still logged in
            return session;
        }

        return null;
    }

    public boolean setUserAsLogged(String username, String sessionId) {
        hashOperations.put(KEY+username, username, sessionId);
        redisTemplate.expire(KEY+username, tokenTimeout, TimeUnit.DAYS);
        return true;
    }

    public void setUserAsNotLogged(String username) {
        hashOperations.delete(KEY+username, username);
    }

}
