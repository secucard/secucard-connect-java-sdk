package com.secucard.connect.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.smart.Transaction;
import org.junit.Test;

import java.io.File;

public class ModelTest {

  @Test
  public void test() throws  Exception{
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    File file = new File("/home/public/projects/secu/secuconnect/SecuConnect/src/test/java/com/secucard/connect/text.json");
    Transaction transaction = mapper.readValue(file, Transaction.class);
    System.out.println();
  }

}
