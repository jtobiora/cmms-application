package ng.upperlink.nibss.cmms.interceptors;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.config.cache.JWTRedisToken;
import ng.upperlink.nibss.cmms.config.cache.SessionManager;
import ng.upperlink.nibss.cmms.config.cache.UserTokenCacheService;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.SecurityConstants;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class InterceptorConfig extends HandlerInterceptorAdapter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    private JWTRedisToken jwtRedisToken;
    private UserTokenCacheService userTokenCacheService;
    private SessionManager sessionManager;

    @Autowired
    public void setUserTokenCacheService(UserTokenCacheService userTokenCacheService) {
        this.userTokenCacheService = userTokenCacheService;
    }

    @Autowired
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Autowired
    public void setJwtRedisToken(JWTRedisToken jwtRedisToken) {
        this.jwtRedisToken = jwtRedisToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserDetail userDetail = jwtRedisToken.decodeToken(request.getHeader(SecurityConstants.HEADER_STRING.getValue()));
        if (userDetail == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), Errors.UNKNOWN_USER.getValue())));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
            return false;
        }

        if (!jwtRedisToken.isValidUserSession(userDetail.getSessionId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_FORBIDDEN),Errors.EXPIRED_SESSION.getValue())));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
            return false;
        }

        if (!jwtRedisToken.isValidUserToken(request.getHeader(SecurityConstants.HEADER_STRING.getValue()), userDetail.getSessionId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(), String.valueOf(HttpServletResponse.SC_FORBIDDEN), Errors.EXPIRED_TOKEN.getValue())));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            return false;
        }

        List<String> task = userTokenCacheService.getTask(userDetail.getSessionId(), request.getHeader(SecurityConstants.HEADER_STRING.getValue()));

        //confirm that the this user have the right visit this url
//        if (!isUserAuthorized(task,(HandlerMethod) handler)){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_UNAUTHORIZED),Errors.NOT_PERMITTED.getValue())));
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
//            return false;
//        }


        //update the sessions expiry time
        sessionManager.updateTimeout(userDetail.getSessionId());
        addToSecurityContextAndServletRequest(userDetail,request);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    private boolean isUserAuthorized(List<String> tasks, HandlerMethod handlerMethod){

        String methodName = handlerMethod.getMethod().getName();

        System.out.println("Method Name ===== " + methodName);
        if (!methodName.toLowerCase().contains("create") && !methodName.toLowerCase().contains("update") && !methodName.toLowerCase().contains("toggle")){
            return true;
        }

        if (tasks.stream().anyMatch(s -> s.equals(methodName))) {
            return true;
        }
        return false;
    }

    private void addToSecurityContextAndServletRequest(UserDetail userDetail, HttpServletRequest request){
        request.setAttribute(Constants.USER_DETAIL, userDetail);

        String username = userDetail.getEmailAddress();
        log.trace("user detail from token: {}", userDetail);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, username, AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //  HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", "API-KEY, API_KEY, LANG, LANGUAGE, User-Agent, Accept-Encoding, Origin, Accept, Accept-Language, Content-Language, Content-Type, Authorization, x-forwarded-by, Last-Event-ID, DPR, Save-Data, Viewport-Width, Width");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods","'GET,HEAD,PUT,PATCH,POST,OPTIONS,DELETE");

        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
//        if (request.getMethod().equals("OPTIONS")) {
//            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
//            return;
//        }

        // pass the request along the filter chain
        filterChain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
