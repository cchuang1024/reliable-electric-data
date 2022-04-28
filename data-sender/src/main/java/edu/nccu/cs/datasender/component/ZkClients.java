package edu.nccu.cs.datasender.component;

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

    private Map<String, IZkClient> registry;

    public ZkClients() {
        this.registry = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized IZkClient getOne(String className) {
        if (!this.registry.containsKey(className)) {
            // ZkConnection zkConn = new ZkConnection(connectString);
            this.registry.put(className, new ZkClient(connectString));
        }

        return this.registry.get(className);
    }

}
