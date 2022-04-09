package edu.nccu.cs.entity;

import java.io.Serializable;

import edu.nccu.cs.exception.SystemException;

public interface KeyValueEntity extends Serializable {
    byte[] getKey();

    byte[] getValue() throws SystemException;
}
