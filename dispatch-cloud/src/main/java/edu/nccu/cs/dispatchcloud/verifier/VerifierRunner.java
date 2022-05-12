package edu.nccu.cs.dispatchcloud.verifier;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
public class VerifierRunner {


}
