package com.secucard.connect.service.services;

import com.secucard.connect.Callback;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class ServicesService extends AbstractService {

  public List<IdentRequest> getIdentRequests(QueryParams queryParams, Callback<List<IdentRequest>> callback) {
    return null;
  }

  public IdentRequest getIdentRequest(String id, Callback<IdentRequest> callback) {
    return null;
  }

  public IdentRequest createIdentRequest(IdentRequest newIdentRequest, Callback<IdentRequest> callback) {
    return null;
  }

  public List<IdentResult> getIdentResults(QueryParams queryParams, Callback<List<IdentResult>> callback) {
    return null;
  }

  public IdentResult getIdentResult(String id, Callback<IdentResult> callback) {
    return null;
  }
}
