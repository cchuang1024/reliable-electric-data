package edu.nccu.cs.datasender.component;

import edu.nccu.cs.exception.ApplicationException;
import edu.nccu.cs.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

import static edu.nccu.cs.datasender.config.Constant.PATH_ID;
import static edu.nccu.cs.datasender.config.Constant.PATH_TOKEN;

@Service
@Slf4j
public class ZkService {

    private ZkClient zkClient;
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
        } catch (ZkNoNodeException noNode) {
            log.info("parent node has not been created.");
            log.info(ExceptionUtils.getStackTrace(noNode));

            String parentDir = PATH_TOKEN.substring(0, PATH_TOKEN.lastIndexOf('/'));
            this.createParentNode(parentDir);

            createNode(token, id);
        } catch (ZkNodeExistsException nodeExist) {
            log.info("node had been created.");
            log.info(ExceptionUtils.getStackTrace(nodeExist));

            throw new ApplicationException("node exists.");
        }
    }

    public void createTokenNode(String token) {
        try {
            mutex.acquire();
            zkClient.createEphemeral(PATH_TOKEN, token);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            mutex.release();
        }
    }

    public void createIdNode(String id) {
        try {
            mutex.acquire();
            zkClient.createEphemeral(PATH_ID, id);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            mutex.release();
        }
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
        try {
            mutex.acquire();

            this.zkClient.delete(PATH_ID);
            this.zkClient.delete(PATH_TOKEN);
            this.zkClient.close();
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            mutex.release();
        }
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
}
