package com.indu.eurekaconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@SpringBootApplication
@EnableEurekaClient
@Controller
public class EurekaClientRestServiceConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientRestServiceConsumerApplication.class, args);
	}
	
	
	//below code is written for making rest client available to the application, so that http calls can be made.
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
	
	
	@Autowired
	private EurekaClient eurekaClient;
	 
	
	@RequestMapping(value = "/whatisthis", method = RequestMethod.GET)
    public String greeting() {
		doRequest();
        return "This is the EurekaClient !";
    }
	
	@Bean
	public Boolean doRequest() {
		Application application = eurekaClient.getApplication("rest-Service");
		if (null != application) {
			InstanceInfo instanceInfo = (InstanceInfo) application.getInstances().get(0);
			String hostname = instanceInfo.getHostName();
			int port = instanceInfo.getPort();

			System.out.println("##### Used HostName is: " + hostname);
			System.out.println("##### Used Port is: " + port);
			// ...

		}
		return true;
	}
}
