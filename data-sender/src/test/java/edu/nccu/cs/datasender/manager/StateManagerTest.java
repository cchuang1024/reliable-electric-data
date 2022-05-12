package edu.nccu.cs.datasender.manager;

import edu.nccu.cs.datasender.common.ZkService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class StateManagerTest {

    @Autowired
    private StateManager stateManager;
    @Autowired
    private ZkService zkService;
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;
    @Value("${application.id}")
    private String id;

    @Test
    public void testLoadContext() {
        assertThat(stateManager).isNotNull();
    }

    @Test
    public void testConnect() {
        cleanNodes();

        stateManager.connect()
                    .handleState(state -> assertThat(state).isEqualTo(SenderState.CONNECTED))
                    .handleToken(token -> assertThat(token).isNotEmpty())
                    .handleId(id -> assertThat(id).isEqualTo(this.id))
                    .execute(appState -> log.info("application state: {}", appState));
    }

    @Test
    public void testDisconnect() throws InterruptedException {
        cleanNodes();

        // use one thread to keep connection
        taskExecutor.execute(() ->
        {
            System.out.println("connect to zk and create node.");
            stateManager.connect()
                        .handleState(state -> {
                            assertThat(state).isEqualTo(SenderState.CONNECTED);
                            System.out.println("close zk now!");
                        });
        });

        TimeUnit.SECONDS.sleep(30L);

        // use another thread to monitor connection
        taskExecutor.execute(() -> {
            System.out.println("check zk connection.");
            stateManager.check()
                        .handleState(state -> {
                            assertThat(state).isEqualTo(SenderState.DISCONNECTED);
                            System.out.println("start zk now!");
                        });
        });
        TimeUnit.SECONDS.sleep(30L);
    }

    @Test
    public void testDestroy() throws InterruptedException {
        cleanNodes();

        taskExecutor.execute(() -> {
            System.out.println("connect to zk and create node.");
            stateManager.connect()
                        .handleState(state -> assertThat(state).isEqualTo(SenderState.CONNECTED));
        });

        TimeUnit.SECONDS.sleep(10L);

        taskExecutor.execute(() -> {
            System.out.println("destroy connection.");
            stateManager.destroy()
                        .handleState(state -> {
                            assertThat(state).isEqualTo(SenderState.CONNECTED);
                            System.out.println("service shutdown now!");
                        });
        });
    }

    private void cleanNodes() {
        zkService.cleanToken();
    }
}
