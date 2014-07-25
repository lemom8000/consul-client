package com.orbitz.consul;

import com.orbitz.consul.model.agent.Agent;
import com.orbitz.consul.model.health.ServiceHealth;
import org.junit.Rule;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AgentTests {

    @Rule
    public ConsulRule consulRule = new ConsulRule();

    @Test
    @ConsulRunning
    public void shouldRetrieveAgentInformation() throws UnknownHostException {
        Consul client = Consul.newClient();
        Agent agent = client.agentClient().getAgent();

        assertNotNull(agent);
        assertEquals("127.0.0.1", agent.getConfig().getClientAddr());
    }

    @Test
    @ConsulRunning
    public void shouldRegister() throws UnknownHostException {
        Consul client = Consul.newClient();
        String serviceName = UUID.randomUUID().toString();
        String serviceId = UUID.randomUUID().toString();

        client.agentClient().register(8080, 10000L, serviceName, serviceId);

        boolean found = false;

        for(ServiceHealth health : client.healthClient().getServiceHealth(serviceName)) {
            if(health.getService().getId().equals(serviceId)) {
                found = true;
            }
        }

        assertTrue(found);
    }

    @Test(expected = ConsulException.class)
    @ConsulRunning
    public void shouldFailOnDuplicateRegistration() throws UnknownHostException {
        Consul client = Consul.newClient();
        String serviceName = UUID.randomUUID().toString();
        String serviceId = UUID.randomUUID().toString();

        client.agentClient().register(8080, 10000L, serviceName, serviceId);
        client.agentClient().register(8080, 10000L, serviceName, serviceId);
    }
}
