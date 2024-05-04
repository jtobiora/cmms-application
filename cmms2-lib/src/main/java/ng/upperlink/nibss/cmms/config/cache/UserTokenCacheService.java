package ng.upperlink.nibss.cmms.config.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class UserTokenCacheService{

    private static final String KEY = "userCmmsToken";
    private static final Logger logger = LoggerFactory.getLogger(ng.upperlink.nibss.cmms.config.cache.UserTokenCacheService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    @Value("${token-timeout}")
    private int tokenTimeout;


    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public Object findUserToken(String sessionId, String userToken) {
        return hashOperations.get(KEY+sessionId, userToken);
    }

    public boolean saveUserToken(String sessionId, String userToken)
    {

        if (userToken == null){
            logger.error("userToken is NULL...unable to save... returning false");
            return false;
        }
        List<String> tasks = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(tasks, type);
        hashOperations.put(KEY+sessionId, userToken , json);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.DAYS);
        return true;
    }

    public boolean saveBillerMandateRequest(String sessionId, String accessToken, String mandate) {

        if (accessToken == null){
            logger.error("Biller Token is NULL...unable to save... returning false");
            return false;
        }
        hashOperations.put(KEY+sessionId, accessToken , mandate);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.DAYS);
        return true;
    }

    public boolean saveUserTokenAndTask(String sessionId, String userToken, List<String> tasks) {

        if (userToken == null){
            logger.error("userToken is NULL...unable to save... returning false");
            return false;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(tasks, type);

        hashOperations.put(KEY+sessionId, userToken , json);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.DAYS);
        return true;

    }

    public List<String> getTask(String sessionId, String userToken) {

        Object task = hashOperations.get(KEY + sessionId, userToken);

        if (task != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> fromJson = gson.fromJson(String.valueOf(task), type);
            return fromJson;
        }

        return new ArrayList<>();
    }

    public  Object getMandateRequestToken(String sessionId, String accessToken) {

        Object mandateObj = hashOperations.get(KEY + sessionId, accessToken);
        if (mandateObj == null){
            return null;
        }
        return mandateObj;
    }

    public void deleteUserToken(String userToken, String sessionId) {
        hashOperations.delete(KEY+sessionId, userToken);
    }
    public void deleteMandatRequestToken(String accessToken, String sessionId) {
        hashOperations.delete(KEY+sessionId, accessToken);
    }

    public void deleteUserToken(String sessionId) {
        Set keys = hashOperations.keys(KEY + sessionId);
        keys.forEach(o -> {
            hashOperations.delete(KEY + sessionId, o);
        });
    }

}
