package com.indu.eurekaconsumer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.indu.catalog.beans.Product;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
 
@RestController
public class ServiceConsumerController {
 
    @Autowired
    private DiscoveryClient discoveryClient;
    
    
    @Autowired
    private EurekaClient eurekaClient;
    
    
    @Autowired

    private RestTemplate restTemplate;
    
    
 
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
 
        return "<a href='showAllServiceIds'>Show All Service Ids</a>";
    }
 
    @ResponseBody
    @RequestMapping(value = "/showAllServiceIds", method = RequestMethod.GET)
    public String showAllServiceIds() {
 
        List<String> serviceIds = this.discoveryClient.getServices();
 
        if (serviceIds == null || serviceIds.isEmpty()) {
            return "No services found!";
        }
        String html = "<h3>Service Ids:</h3>";
        for (String serviceId : serviceIds) {
            html += "<br><a href='showService?serviceId=" + serviceId + "'>" + serviceId + "</a>";
        }
        return html;
    }
 
    @ResponseBody
    @RequestMapping(value = "/showService", method = RequestMethod.GET)
    public String showFirstService(@RequestParam(defaultValue = "") String serviceId) {
 
        // (Need!!) eureka.client.fetchRegistry=true
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
 
        if (instances == null || instances.isEmpty()) {
            return "No instances for service: " + serviceId;
        }
        String html = "<h2>Instances for Service Id: " + serviceId + "</h2>";
 
        for (ServiceInstance serviceInstance : instances) {
            html += "<h3>Instance: " + serviceInstance.getUri() + "</h3>";
            html += "Host: " + serviceInstance.getHost() + "<br>";
            html += "Port: " + serviceInstance.getPort() + "<br>";
        }
 
        return html;
    }
 
    // A REST method, To call from another service.
    // See in Lesson "Load Balancing with Ribbon".
    @ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
 
        return "<html>Hello from Rest Service Consumer</html>";
    }
    
    
	@ResponseBody
	@RequestMapping(value = "/callOtherServerService", method = RequestMethod.GET)
	public String getProductUsingEurekaDiscovery() {

		String productId = "a2623aa3-7071-4cc6-8267-ed33d03d19e3";

		Application application = eurekaClient.getApplication("rest-Service");
		Product product = null;
		if (null != application) {
			InstanceInfo instanceInfo = application.getInstances().get(0);

			if (null != instanceInfo) {
				String url = "http://" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "/" + "get/product/"
						+ productId;

				System.out.println("URL" + url);

				product = restTemplate.getForObject(url, Product.class);

				System.out.println("RESPONSE " + product);
			}
		}
		return product.toString();
	}
    
    
 
}