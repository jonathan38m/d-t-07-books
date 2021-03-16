package com.distribuida;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;


@ApplicationScoped
public class InitializationCdi {
    
    protected static String ID = UUID.randomUUID().toString();
    
    
    @Inject
    @ConfigProperty(name = "configsource.consul.host", defaultValue = "127.0.0.1")
    protected String consulHost;
    
    @Inject
    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    protected int appPort;
    
    @Inject
    @ConfigProperty(name = "app.books.name", defaultValue = "Books Default")
    protected String appName;
    
    public void init(@Observes @Initialized(ApplicationScoped.class)Object obt) throws UnknownHostException {
        System.out.println("**********************init");
        
        ConsulClient client = new ConsulClient("localhost");
        
        NewService newService = new NewService();
        newService.setId(ID);
        newService.setName(appName);
        newService.setPort(appPort);
        newService.setAddress(InetAddress.getLocalHost().getHostAddress());
        
        NewService.Check check = new NewService.Check();
        check.setMethod("GET");
        check.setHttp("http://127.0.0.1:" + appPort + "/q/health/live");
        check.setInterval("10s");
        check.setDeregisterCriticalServiceAfter("20s");
        
        newService.setCheck(check);
        client.agentServiceRegister(newService);
        
    }
    public void destroy(@Observes @Destroyed(ApplicationScoped.class)Object obt) {
        System.out.println("***destroy");
        
        ConsulClient client = new ConsulClient("localhost");
        
        client.agentServiceDeregister(ID);
        
    }
}