package edu.nccu.cs.dispatchcloud.manager;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class SelfVerifierJob implements Runnable{



    @Override
    public void run() {

    }
}
