package edu.nccu.cs.datasender.component;

import com.github.zkclient.IZkClient;
import edu.nccu.cs.exception.ApplicationException;
import edu.nccu.cs.exception.SystemException;
import edu.nccu.cs.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static edu.nccu.cs.datasender.config.Constant.PATH_ID;
import static edu.nccu.cs.datasender.config.Constant.PATH_TOKEN;

@Service
@Slf4j
public class ZkService {

    private IZkClient zkClient;
    private Semaphore mutex;

    @Autowired
    public ZkService(ZkClients zkClients) {
        this.zkClient = zkClients.getOne(ZkService.class.getName());
        this.mutex = new Semaphore(1);
    }

    public synchronized void createNode(String token, String id) {
        try {
            log.info("creating node at {}", Thread.currentThread().getName());

            this.createTokenNode(token);
            log.info("ephemeral node {} created with {} at {}", PATH_TOKEN, token, Thread.currentThread().getName());

            this.createIdNode(id);
            log.info("ephemeral node {} created with {} at {}", PATH_ID, id, Thread.currentThread().getName());
        } catch (SystemException nodeExist) {
            log.info("node had been created.");
            log.info(ExceptionUtils.getStackTrace(nodeExist));

            throw new ApplicationException("node exists.");
        }
    }

    private String getParentPath(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    public void createTokenNode(String token) throws SystemException {
        checkNode(PATH_TOKEN);
        zkClient.createEphemeral(PATH_TOKEN, token.getBytes());
    }

    private void checkNode(String path) throws SystemException {
        String parentDir = getParentPath(PATH_TOKEN);

        if (!zkClient.exists(parentDir)) {
            this.createParentNode(parentDir);
        } else if (zkClient.exists(path)) {
            throw new SystemException();
        }
    }

    public void createIdNode(String id) throws SystemException {
        checkNode(PATH_ID);
        zkClient.createEphemeral(PATH_ID, id.getBytes());
    }

    public void createParentNode(String parentDir) {
        try {
            mutex.acquire();

            zkClient.createPersistent(parentDir, true);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            mutex.release();
        }
    }

    public void destroy() {
        this.zkClient.delete(PATH_ID);
        this.zkClient.delete(PATH_TOKEN);
        this.zkClient.close();
    }

    public void cleanToken() {
        try {
            mutex.acquire();

            zkClient.delete(PATH_ID);
            zkClient.delete(PATH_TOKEN);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            mutex.release();
        }
    }

    public boolean isTokenExists() {
        return zkClient.exists(PATH_TOKEN);
    }

    public boolean isIdExists() {
        return zkClient.exists(PATH_ID);
    }

    public String readId() {
        return zkClient.readData(PATH_ID).toString();
    }

    public String readToken() {
        return zkClient.readData(PATH_TOKEN).toString();
    }

    public static final long CONNECTION_WAIT_TIME = 10L;

    public boolean isConnected() {
        return zkClient.waitUntilConnected(CONNECTION_WAIT_TIME, TimeUnit.SECONDS);
    }

    public void close() {
        zkClient.close();
    }
}
