package edu.washu.bms.fedex.fedexintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class KitIntegrationApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(KitIntegrationApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(KitIntegrationApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer properties =
				new PropertySourcesPlaceholderConfigurer();
		//TODO Replace with linux path
		//properties.setLocation(new FileSystemResource("C:/Users/chinnam/Desktop/SURYA WORKSPACE/kitintegration.properties"));
		properties.setLocation(new FileSystemResource("/home/bioms/.bioms/fedexintegration.properties"));
		properties.setIgnoreResourceNotFound(false);
		return properties;
	}
	@Bean
	public  JavaMailSender emailSender(){
		JavaMailSenderImpl emailSender = new JavaMailSenderImpl();
		emailSender.setHost("mailrelay.wustl.edu");
		emailSender.setPort(25);

		return emailSender;
	}
}
