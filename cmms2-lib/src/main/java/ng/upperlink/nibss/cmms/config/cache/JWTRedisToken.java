package ng.upperlink.nibss.cmms.config.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.enums.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JWTRedisToken {

    private UserTokenCacheService userTokenCacheService;

    private SessionManager sessionManager;

    @Autowired
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Autowired
    public void setUserTokenCacheService(UserTokenCacheService userTokenCacheService) {
        this.userTokenCacheService = userTokenCacheService;
    }

    private final static Logger logger = LoggerFactory.getLogger(UserTokenCacheService.class);

    public String generateAndSaveTokenInRedis(UserDetail userDetail){


        //generateAuthRequest token
        String token = Jwts.builder()
                .setPayload(new Gson().toJson(userDetail))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getValue())
                .compact();

        //save in redis
        if (userTokenCacheService.saveUserToken( userDetail.getSessionId(), SecurityConstants.TOKEN_PREFIX.getValue() +" "+ token)){
//            logger.info("User token saved : {}", token);
        }

        return SecurityConstants.TOKEN_PREFIX.getValue()+" "+ token;
    }

    public String generateToken(UserDetail userDetail){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(userDetail);
        //generateAuthRequest token
        String token = Jwts.builder()
                .setPayload(jsonNode.toString())
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getValue())
                .compact();

        return SecurityConstants.TOKEN_PREFIX.getValue()+" "+ token;
    }

    public boolean isValidUserToken(String userToken, String sessionId) {
        Object token = this.userTokenCacheService.findUserToken(sessionId, userToken);
        return token != null;
    }

    public boolean isValidUserSession(String sessionId) {
        return sessionManager.isValidateSession(sessionId);
    }

    /**
     * To decypt token so that we can use this to know which task the user show be able perform
     * @param token
     * @return
     */
    public UserDetail decodeToken(String token){

        if (token != null) {

            // parse the token.
            Claims userDetailString = null;
            try {
                userDetailString = Jwts.parser()
                        .setSigningKey(SecurityConstants.SECRET.getValue())
                        .parseClaimsJws(token.trim().replace(" ","").replace(SecurityConstants.TOKEN_PREFIX.getValue(), ""))
                        .getBody();
            } catch (ExpiredJwtException e) {
                logger.error("Unable to parse token", e);
                return null;
            } catch (UnsupportedJwtException e) {
                logger.error("Unable to parse token", e);
                return null;
            } catch (MalformedJwtException e) {
                logger.error("Unable to parse token", e);
                return null;
            } catch (SignatureException e) {
                logger.error("Unable to parse token", e);
                return null;
            } catch (IllegalArgumentException e) {
                logger.error("Unable to parse token", e);
                return null;
            } catch (Exception e){
                logger.error("Unable to parse token", e);
                return null;
            }

            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(Long.valueOf(String.valueOf(userDetailString.get("userId"))));
            userDetail.setRoleType(userDetailString.get("roleType",String.class));
            userDetail.setEmailAddress(userDetailString.get("emailAddress", String.class));
            userDetail.setUserType(userDetailString.get("userType", String.class));
            userDetail.setUserAuthorizationType(userDetailString.get("userAuthorizationType", String.class));
            userDetail.setSessionId(userDetailString.get("sessionId", String.class));
            userDetail.setRoles(userDetailString.get("roles", ArrayList.class));
            userDetail.setPrivileges(userDetailString.get("privileges", ArrayList.class));


            if (userDetail.getUserId() != null) {
                return userDetail;
            }

            return null;
        }

        return null;
    }

}
