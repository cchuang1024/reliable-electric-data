package edu.nccu.cs.dispatchcloud.common;

import com.github.zkclient.IZkClient;
import com.github.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZkClients {

    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${zookeeper.connectionTimeout}")
    private Integer connectionTimeout;

    private Map<String, IZkClient> registry;

    public ZkClients() {
        this.registry = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized IZkClient getOne(String className) {
        if (!this.registry.containsKey(className)) {
            this.registry.put(className, new ZkClient(connectString, connectionTimeout));
        }

        return this.registry.get(className);
    }

    public synchronized IZkClient create() {
        return new ZkClient(connectString, connectionTimeout);
    }
}
