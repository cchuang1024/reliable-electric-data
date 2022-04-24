package edu.nccu.cs.datasender.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
public class ZkConfig {
    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${zookeeper.maxRetries}")
    private int maxRetries;

    @Value("${zookeeper.baseSleepTimeMs}")
    private int sleepTime;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public CuratorFramework zkClient() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(sleepTime, maxRetries);
        return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }
}
