package edu.nccu.cs.domain;

import java.util.List;

import edu.nccu.cs.exception.SystemException;
import edu.nccu.cs.utils.TypedPair;

public interface KeyValueEntity {
    byte[] getKey();

    byte[] getValue() throws SystemException;

    List<TypedPair<byte[]>> getKeyValuePairs() throws SystemException;
}
