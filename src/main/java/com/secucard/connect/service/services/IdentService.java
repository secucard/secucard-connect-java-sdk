package com.secucard.connect.service.services;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;

import java.util.List;

/**
 * Provides acces to the secucard services resources and operations.
 */
public class IdentService extends AbstractService {

  /**
   * Returns a list of ident request objects according to the given query parameters.
   *
   * @param queryParams A set of parameters a ident request must match.
   * @param callback    Callback for asynchronous handling.
   * @return If no callaback provided the found ident requests, null if nothing was found.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public List<IdentRequest> getIdentRequests(final QueryParams queryParams, Callback<List<IdentRequest>> callback) {
    return new ConvertingInvoker<ObjectList<IdentRequest>, List<IdentRequest>>() {
      @Override
      protected ObjectList<IdentRequest> handle(Callback<ObjectList<IdentRequest>> callback) {
        return getChannel().findObjects(IdentRequest.class, queryParams, callback);
      }

      @Override
      protected List<IdentRequest> convert(ObjectList<IdentRequest> object) {
        return object == null ? null : object.getList();
      }
    }.invokeAndConvert(callback);
  }

  /**
   * Returns a single ident request object.
   *
   * @param id       The id of the ident request.
   * @param callback Callback for asynchronous handling.
   * @return If no callback was provided the requested ident request, throws {@link com.secucard.connect.SecuException}
   * if no ident request can be found for the given id.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public IdentRequest getIdentRequest(final String id, Callback<IdentRequest> callback) {
    return new Invoker<IdentRequest>() {
      @Override
      protected IdentRequest handle(Callback<IdentRequest> callback) {
        return getChannel().getObject(IdentRequest.class, id, callback);
      }
    }.invoke(callback);
  }

  public IdentResult getIdentResultByRequestId(final String identRequestId, Callback<IdentResult> callback) {
    return new ConvertingInvoker<ObjectList<IdentResult>, IdentResult>() {
      @Override
      protected ObjectList<IdentResult> handle(Callback<ObjectList<IdentResult>> callback) {
        QueryParams params = new QueryParams();
        params.setQuery("request.id:" + identRequestId);
        return getChannel().findObjects(IdentResult.class, params, callback);
      }

      @Override
      protected IdentResult convert(ObjectList<IdentResult> object) {
        if (object == null || object.getList() == null || object.getList().size() == 0) {
          return null;
        }
        return object.getList().get(0);
      }
    }.invokeAndConvert(callback);
  }


  /**
   * Creates a new ident request.
   *
   * @param newIdentRequest The data for the ident request to create.
   * @param callback        Callback for asynchronous handling.
   * @return If a callback was provided the newly created ident request, throws {@link com.secucard.connect.SecuException}
   * if no ident request could be created for the given data.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public IdentRequest createIdentRequest(final IdentRequest newIdentRequest, Callback<IdentRequest> callback) {
    return new Invoker<IdentRequest>() {
      @Override
      protected IdentRequest handle(Callback<IdentRequest> callback) throws Exception {
        return getChannel().createObject(newIdentRequest, callback);
      }
    }.invoke(callback);
  }

  /**
   * Returns a list of ident result objects according to the given query parameters.
   *
   * @param queryParams A set of parameters a ident result must match.
   * @param callback    Callback for asynchronous handling.
   * @return If no callaback provided the found ident results, null if nothing was found.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public List<IdentResult> getIdentResults(final QueryParams queryParams, Callback<List<IdentResult>> callback) {
    return new ConvertingInvoker<ObjectList<IdentResult>, List<IdentResult>>() {
      @Override
      protected ObjectList<IdentResult> handle(Callback<ObjectList<IdentResult>> callback) {
        return getChannel().findObjects(IdentResult.class, queryParams, callback);
      }

      @Override
      protected List<IdentResult> convert(ObjectList<IdentResult> object) {
        return object == null ? null : object.getList();
      }
    }.invokeAndConvert(callback);
  }

  /**
   * Returns a single ident result object.
   *
   * @param id       The
   * @param callback Callback for asynchronous handling.
   * @return If no callback was provided the requested ident result, throws {@link com.secucard.connect.SecuException}
   * if no ident result can be found for the given id.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public IdentResult getIdentResult(final String id, Callback<IdentResult> callback) {
    return new Invoker<IdentResult>() {
      @Override
      protected IdentResult handle(Callback<IdentResult> callback) {
        return getChannel().getObject(IdentResult.class, id, callback);
      }
    }.invoke(callback);
  }
}
