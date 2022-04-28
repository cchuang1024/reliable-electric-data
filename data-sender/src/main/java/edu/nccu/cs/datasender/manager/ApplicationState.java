package edu.nccu.cs.datasender.manager;

import org.springframework.beans.factory.annotation.Value;

import java.util.function.Consumer;

public class ApplicationState {

    private static final Object LOCK = new Object();

    private SenderState state;
    private String token;

    @Value("${application.id}")
    private String id;

    public String getId() {
        return id;
    }

    public SenderState getState() {
        synchronized (LOCK) {
            return state;
        }
    }

    public void setState(SenderState state) {
        synchronized (LOCK) {
            this.state = state;
        }
    }

    public String getToken() {
        synchronized (LOCK) {
            return token;
        }
    }

    public void setToken(String token) {
        synchronized (LOCK) {
            this.token = token;
        }
    }

    public ApplicationState handleState(Consumer<SenderState> handler) {
        handler.accept(this.state);
        return this;
    }

    public ApplicationState handleToken(Consumer<String> handler) {
        handler.accept(this.token);
        return this;
    }

    public ApplicationState handleId(Consumer<String> handler) {
        handler.accept(this.id);
        return this;
    }

    public void execute(Consumer<ApplicationState> exe) {
        exe.accept(this);
    }

    @Override
    public String toString() {
        return "ApplicationState{" +
                "state=" + state +
                ", token='" + token + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
