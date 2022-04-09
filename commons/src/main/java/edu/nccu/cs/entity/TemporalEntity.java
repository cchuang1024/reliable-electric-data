package edu.nccu.cs.entity;

import java.io.Serializable;

public interface TemporalEntity extends Serializable {
    long getTimestamp();
    void setTimestamp(long timestamp);
}
