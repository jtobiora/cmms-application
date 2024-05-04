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
public class SessionManager {

    private static final String KEY = "spring:session:mysession:sessions:expires:";
    private static final Logger logger = LoggerFactory.getLogger(ng.upperlink.nibss.cmms.config.cache.SessionManager.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    @Value("${session-timeout}")
    private Long sessionTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void updateTimeout(String sessionId) {
        hashOperations.getOperations().expire(KEY+sessionId,sessionTimeout, TimeUnit.SECONDS);
    }

    public boolean isValidateSession(String sessionId) {
        return hashOperations.getOperations().hasKey(KEY+sessionId);
    }

    public void deleteSession(String sessionId) {
        hashOperations.getOperations().delete(KEY+sessionId);
    }
}
