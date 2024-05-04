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
        registry.addInterceptor(downloadTokenInterceptor).addPathPatterns(
                "/uploads/bulk","/transactionreport/bulk", "/transactionreport/bystateandlga/bulk");

        registry.addInterceptor(interceptorConfig).addPathPatterns("/user/**", "/report/**","/bank/mandate/**",
                "/nibss/mandate/**","/biller/mandate/**","/pssp/mandate/**","/search/**", "/logout/**","/password/update-password","/transactions/**",
                "/uploads/template","/uploads/image", "/webaudit/**","/dashboard/**"
        );

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

