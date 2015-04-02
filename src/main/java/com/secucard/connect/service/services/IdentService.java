package com.secucard.connect.service.services;

import com.secucard.connect.Callback;
import com.secucard.connect.event.AbstractEventHandler;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idresult.Attachment;
import com.secucard.connect.model.services.idresult.Person;
import com.secucard.connect.service.AbstractService;

import java.util.*;

/**
 * Provides access to the secucard services resources and operations.
 * Support also caching of attachments when requested.
 * Note: Caching will speed up clients access, but has also impact on memory usage. Depending on the type of cache storage
 * it may be not a good idea to cache too much data. The {@link com.secucard.connect.storage.DataStorage) implementation
 * provided to the client is used also for caching.
 * See {@link com.secucard.connect.Client#create(String, com.secucard.connect.ClientConfiguration, Object, com.secucard.connect.storage.DataStorage)}
 * to get details which storage implementation is actually used.
 */
public class IdentService extends AbstractService {
  private boolean cacheAttachmentsEnabled = true;

  public IdentService() {
    serviceEventListener = new ServiceEventListener();
  }

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
   * @return If no callback provided the found ident requests, null if nothing was found.<br/>
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
   * Returns a ident result for a given  ident request ids.
   *
   * @param identRequestIds     The request ids to get the results for.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return If no callback was provided the requested ident result or null if no ident result can be found for the given id.<br/>
   * Always null if a callback was provided, the callbacks methods are called analogous then.
   */
  public List<IdentResult> getIdentResultsByRequestIds(final List<String> identRequestIds,
                                                       Callback<List<IdentResult>> callback,
                                                       final boolean downloadAttachments) {
    // todo: better avoid query and access by id?
    return new ConvertingInvoker<ObjectList<IdentResult>, List<IdentResult>>() {
      @Override
      protected ObjectList<IdentResult> handle(Callback<ObjectList<IdentResult>> callback) {
        return getIdentResultsByRequestsRaw(identRequestIds, callback, downloadAttachments);
      }

      @Override
      protected List<IdentResult> convert(ObjectList<IdentResult> objectList) {
        if (objectList == null || objectList.getList() == null || objectList.getList().size() == 0) {
          return null;
        }
        return objectList.getList();
      }
    }.invokeAndConvert(callback);
  }

  private ObjectList<IdentResult> getIdentResultsByRequestsRaw(List<String> requestIds,
                                                               final Callback<ObjectList<IdentResult>> callback,
                                                               final boolean downloadAttachments) {
    StringBuilder query = new StringBuilder();
    for (Iterator<String> iterator = requestIds.iterator(); iterator.hasNext(); ) {
      String id = iterator.next();
      query.append("request.id:").append(id);
      if (iterator.hasNext()) {
        query.append(" or ");
      }
    }
    QueryParams params = new QueryParams();
    params.setQuery(query.toString());
    if (callback == null) {
      ObjectList<IdentResult> list = context.getChannel(null).findObjects(IdentResult.class, params, null);
      if (list != null) {
        processAttachments(list.getList(), downloadAttachments);
      }
      return list;
    } else {
      context.getChannel(null).findObjects(IdentResult.class, params, new Callback<ObjectList<IdentResult>>() {
        @Override
        public void completed(ObjectList<IdentResult> result) {
          if (result != null) {
            processAttachments(result.getList(), downloadAttachments);
          }
          callback.completed(result);
        }

        @Override
        public void failed(Throwable cause) {
          callback.failed(cause);
        }
      });
      return null;
    }
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
   * @return If no callback was provided the found ident results, null if nothing was found.<br/>
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

  /**
   * Register an event handler, see {@link IdentEventHandler} for which event.<br/>
   * This handler will be called then when the event is passed to
   * {@link com.secucard.connect.Client#handleEvent(String)}
   * The event is discarded if no handler is registered.<br/>
   * Note: Registering a handler multiple times just replaces the previous instance.
   *
   * @param handler The handler instance or null to remove the handler.
   */
  public void onIdentRequestChanged(IdentEventHandler handler) {
    if (handler == null) {
      removeEventHandler(IdentEventHandler.ID);
    } else {
      handler.setService(this);
      addEventHandler(IdentEventHandler.ID, handler);
    }
  }

  /**
   * Disable/enable idents request event handling.
   * Default is enabled.
   */
  public void disableEventHandling(boolean disable) {
    disableEventHandler(IdentEventHandler.ID, disable);
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

  /**
   * Event handler for event type {@link Events#TYPE_CHANGED} and target {@link IdentRequest#OBJECT}, happening when
   * IdentRequests are approved. The handler retrieves and returns a list of belonging IdentResults and downloads the
   * containing attachments when required if such an event is passed to
   * {@link com.secucard.connect.Client#handleEvent(String)}.
   */
  public static abstract class IdentEventHandler extends AbstractEventHandler<List<IdentResult>, Event> {
    public static final String ID = IdentRequest.OBJECT + Events.TYPE_CHANGED;
    private IdentService service;

    private void setService(final IdentService service) {
      this.service = service;
    }

    @Override
    public final boolean accept(Event event) {
      return IdentRequest.OBJECT.equals(event.getTarget()) && Events.TYPE_CHANGED.equals(event.getType());
    }

    public final synchronized void handle(Event event) {
      List<IdentRequest> requests = (List<IdentRequest>) event.getData();
      List<String> ids;
      if (requests == null) {
        ids = Collections.emptyList();
      } else {
        ids = new ArrayList<>(requests.size());
        for (IdentRequest request : requests) {
          ids.add(request.getId());
        }
      }

      if (isAsync()) {
        service.getIdentResultsByRequestsRaw(ids, new Callback<ObjectList<IdentResult>>() {
          @Override
          public void completed(ObjectList<IdentResult> result) {
            IdentEventHandler.this.completed(result.getList());
          }

          @Override
          public void failed(Throwable cause) {
            IdentEventHandler.this.failed(cause);
          }
        }, downloadAttachments(requests));
      } else {
        ObjectList<IdentResult> list = service.getIdentResultsByRequestsRaw(ids, null, downloadAttachments(requests));
        IdentEventHandler.this.completed(list.getList());
      }

    }

    /**
     * Indicates if the idents result attachments associated with the given idents requests should be downloaded or not.
     */
    public abstract boolean downloadAttachments(List<IdentRequest> requests);
  }
}
