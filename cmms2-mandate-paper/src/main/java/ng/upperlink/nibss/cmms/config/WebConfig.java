package ng.upperlink.nibss.cmms.config;

import ng.upperlink.nibss.cmms.interceptors.DownloadTokenInterceptor;
import ng.upperlink.nibss.cmms.interceptors.InterceptorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by toyin.oladele on 18/10/2017.
 */
@Configuration
@EnableScheduling
@EnableJpaAuditing
public class WebConfig extends WebMvcConfigurerAdapter {

    private InterceptorConfig interceptorConfig;

    private DownloadTokenInterceptor downloadTokenInterceptor;

    @Autowired
    public void setDownloadTokenInterceptor(DownloadTokenInterceptor downloadTokenInterceptor) {
        this.downloadTokenInterceptor = downloadTokenInterceptor;
    }

    @Autowired
    public void setInterceptorConfig(InterceptorConfig interceptorConfig) {
        this.interceptorConfig = interceptorConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(downloadTokenInterceptor).addPathPatterns("/billingreportcsv",
                "/dashboard/branches/download", "/dashboard/enrollers/download",
                "/dashboard/enrollments/download", "/dashboard/agents/download",
                "/dashboard/agentmanagers/download", "/template/**", "/bulk/**"
                , "/transactionreport/bulk", "/transactionreport/bystateandlga/bulk");

//        , "/bulk/**"

        registry.addInterceptor(interceptorConfig).addPathPatterns("/mandate/**","/operations/**");

       /* registry.addInterceptor(interceptorConfig).addPathPatterns("/report/**","/agent/**",
                "/dashboard","/dashboard/statistics/state", "/dashboard/agentmanagers", "/dashboard/agents",
                "/dashboard/enrollers", "/dashboard/branches", "/dashboard/enrollments",
                "/sync-report/**","/desktopaudit/**", "/webaudit/**", "/bvnreport/**", "/logout/**", "/setting/**"
                ,"/password/update-password", "/institution/**", "/transactionreport"
                ,"/transactionreport/dashboard", "/transactionreport/bystateandlga");*/

    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/img/**",
                "/css/**",
                "/js/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/static/img/",
                        "classpath:/static/css/",
                        "classpath:/static/js/");
    }

}
