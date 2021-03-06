package edu.nccu.cs.datasender.common;

import com.github.zkclient.IZkClient;
import com.github.zkclient.ZkClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ZkClients {

    private String connectString;
    private int connectionTimeout;

    private Map<String, IZkClient> registry;

    public ZkClients(String connectString, int connectionTimeout) {
        this.connectString = connectString;
        this.connectionTimeout = connectionTimeout;
        this.registry = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized IZkClient getOne(String className) {
        if (!this.registry.containsKey(className)) {
            // ZkConnection zkConn = new ZkConnection(connectString);
            this.registry.put(className, new ZkClient(connectString, connectionTimeout));
        }

        return this.registry.get(className);
    }

}
