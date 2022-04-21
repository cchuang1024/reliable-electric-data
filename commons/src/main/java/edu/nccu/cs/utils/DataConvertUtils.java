package edu.nccu.cs.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import edu.nccu.cs.exception.SystemException;

public class DataConvertUtils {

    public static <T> T objectFromJson(Class<T> claz, String json) throws SystemException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, claz);
        } catch (JacksonException ex) {
            throw new SystemException(ex);
        }
    }


    public static String jsonFromObject(Object obj) throws SystemException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }
    }


    public static byte[] cborFromObject(Object obj) throws SystemException {
        ObjectMapper mapper = new CBORMapper();

        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }
    }

    public static <T> T objectFromCbor(Class<T> claz, byte[] cbor) throws SystemException {
        ObjectMapper mapper = new CBORMapper();
        try {
            return mapper.readValue(cbor, claz);
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }
}
