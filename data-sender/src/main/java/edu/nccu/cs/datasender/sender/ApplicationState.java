package edu.nccu.cs.datasender.sender;

public class ApplicationState {

    private static final Object LOCK = new Object();

    private SenderState state;
    private String token;

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
}
