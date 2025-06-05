package com.sunseed.simtool.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BeansFactoryPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {
	
    private List<String> serverNames;
    private List<String> serverTypes;
    private List<String> serverCpu;
    private List<String> serverUsername;
    private List<String> serverPassword;
    private List<String> serverHost;
    private List<String> serverPem;
    
    private static Environment environment;
    
    @Override
    public void setEnvironment(Environment environment) {
    	BeansFactoryPostProcessor.environment = environment;
    }
    
    public void setEnvironmentValues(Environment environment) {
    	serverNames = Arrays.asList(environment.getProperty("simulation.server.name").split(","));
    	serverTypes = Arrays.asList(environment.getProperty("simulation.server.type").split(","));
    	serverCpu = Arrays.asList(environment.getProperty("simulation.server.cpu").split(","));
    	serverUsername = Arrays.asList(environment.getProperty("simulation.server.username").split(","));
    	serverPassword = Arrays.asList(environment.getProperty("simulation.server.password").split(","));
    	serverHost = Arrays.asList(environment.getProperty("simulation.server.host").split(","));
    	serverPem = Arrays.asList(environment.getProperty("simulation.server.pem").split(","));
    	
    }
    
    @Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    	setEnvironmentValues(BeansFactoryPostProcessor.environment);
    	
		int idx = 0;
		for(;idx < serverNames.size();idx++)
		{
			createAndRegister((BeanDefinitionRegistry) beanFactory, serverNames.get(idx), serverTypes.get(idx),
					Integer.parseInt(serverCpu.get(idx)), serverUsername.get(idx),
					serverPassword.get(idx), serverHost.get(idx), serverPem.size()>idx ? serverPem.get(idx) : null);
		}
	}
	
	private void createAndRegister(BeanDefinitionRegistry registry, String serverName, String type, Integer cpu, String username, String password, String host, String pem) {
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SimulationServer.class)
                .addConstructorArgValue(serverName)
                .addConstructorArgValue(type)
                .addConstructorArgValue(cpu)
                .addConstructorArgValue(username)
                .addConstructorArgValue(password)
                .addConstructorArgValue(host)
                .addConstructorArgValue(pem)
                .getBeanDefinition();
        registry.registerBeanDefinition(serverName, beanDefinition);
    }

}
