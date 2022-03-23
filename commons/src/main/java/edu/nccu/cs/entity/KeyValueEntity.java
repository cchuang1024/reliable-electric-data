package edu.nccu.cs.entity;

import edu.nccu.cs.exception.SystemException;

public interface KeyValueEntity {
    byte[] getKey();

    byte[] getValue() throws SystemException;
}
