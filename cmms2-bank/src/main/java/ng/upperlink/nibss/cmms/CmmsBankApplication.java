package ng.upperlink.nibss.cmms;

import ng.upperlink.nibss.cmms.encryptanddecrypt.ContentFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@SpringBootApplication
@EnableAsync
public class CmmsBankApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CmmsBankApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder);
	}

	@Bean
	public ContentFilter replaceHtmlFilter() {
		return new ContentFilter();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());

		return executor;
	}

}
