package com.secucard.connect.service.services;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idresult.Attachment;
import com.secucard.connect.model.services.idresult.Person;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.service.AbstractService;

import java.util.Arrays;
import java.util.List;

/**
 * Provides acces to the secucard services resources and operations.
 * Support also caching of attachments when requested.
 * Note: Caching will speed up clients access, but has also impact on memory usage. Depending on the type of cache storage
 * it may be not a good idea to cache too much data. The {@link com.secucard.connect.storage.DataStorage) implementation
 * provided to the client is used also for caching.
 * See {@link com.secucard.connect.Client#create(String, com.secucard.connect.ClientConfiguration, Object, com.secucard.connect.storage.DataStorage)}
 * to get details which storage implementation is actually used.
 */
public class IdentService extends AbstractService {
  private boolean cacheAttachmentsEnabled = true;

  /**
   * Set to true/false to globally enable/disable attachment caching when requested by methods of this service.
   * Caching is enabled by default but is only performed when requested in methods.
   */
  public void cacheAttachments(boolean cacheAttachments) {
    this.cacheAttachmentsEnabled = cacheAttachments;
  }

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

  /**
   * Returns a ident result for a certain ident request.
   *
   * @param identRequestId      The id of the originating ident request.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return If no callback was provided the requested ident result or null if no ident result can be found for the given id.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public IdentResult getIdentResultByRequestId(final String identRequestId, Callback<IdentResult> callback,
                                               final boolean downloadAttachments) {
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
        IdentResult result = object.getList().get(0);
        processAttachments(Arrays.asList(result), downloadAttachments);
        return result;
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
   * Supports also the optional download and caching of all attachments before returning.
   * This will speed up clients access to them but involves increasing memory usage.
   *
   * @param queryParams         A set of parameters a ident result must match.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned results + persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return If no callaback provided the found ident results, null if nothing was found.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public List<IdentResult> getIdentResults(final QueryParams queryParams, Callback<List<IdentResult>> callback,
                                           final boolean downloadAttachments) {
    return new ConvertingInvoker<ObjectList<IdentResult>, List<IdentResult>>() {
      @Override
      protected ObjectList<IdentResult> handle(Callback<ObjectList<IdentResult>> callback) {
        return getChannel().findObjects(IdentResult.class, queryParams, callback);
      }

      @Override
      protected List<IdentResult> convert(ObjectList<IdentResult> object) {
        if (object == null) {
          return null;
        }
        List<IdentResult> results = object.getList();
        processAttachments(results, downloadAttachments);
        return results;
      }
    }.invokeAndConvert(callback);
  }

  /**
   * Returns a single ident result object.
   *
   * @param id                  The
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return If no callback was provided the requested ident result, throws {@link com.secucard.connect.SecuException}
   * if no ident result can be found for the given id.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public IdentResult getIdentResult(final String id, Callback<IdentResult> callback, final boolean downloadAttachments) {
    return new Invoker<IdentResult>() {
      @Override
      protected IdentResult handle(Callback<IdentResult> callback) {
        IdentResult result = getChannel().getObject(IdentResult.class, id, callback);
        processAttachments(Arrays.asList(result), downloadAttachments);
        return result;
      }
    }.invoke(callback);
  }

  private void processAttachments(List<IdentResult> results, boolean cache) {
    setContext();

    if (cacheAttachmentsEnabled && cache) {
      for (IdentResult result : results) {
        for (Person person : result.getPersons()) {
          for (Attachment attachment : person.getAttachments()) {
            // todo: introduce download policy settings to be able to avoid some downloads
            attachment.download();
          }
        }
      }
    }
  }
}
