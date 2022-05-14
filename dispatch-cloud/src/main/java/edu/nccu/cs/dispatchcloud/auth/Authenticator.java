package edu.nccu.cs.dispatchcloud.auth;

import com.github.zkclient.IZkClient;
import edu.nccu.cs.dispatchcloud.common.ZkClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static edu.nccu.cs.dispatchcloud.config.Constant.PATH_ID;
import static edu.nccu.cs.dispatchcloud.config.Constant.PATH_TOKEN;

@Component
@Slf4j
public class Authenticator {

    private final IZkClient zkClient;

    @Autowired
    public Authenticator(ZkClients zkClients) {
        zkClient = zkClients.create();
    }

    public boolean checkIdAndToken(String id, String token) {
        byte[] rawId = zkClient.readData(PATH_ID, true);
        byte[] rawToken = zkClient.readData(PATH_TOKEN, true);

        if (rawId == null || rawToken == null) {
            return false;
        } else {
            String realId = new String(rawId);
            String realToken = new String(rawToken);

            return Objects.equals(id, realId) && Objects.equals(token, realToken);
        }
    }
}
