package com.secucard.connect.service.services;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventHandlerCallback;
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

  /**
   * Set to true/false to globally enable/disable attachment caching when requested by methods of this service.
   * Caching is enabled by default but is only performed when requested in methods.
   */
  public void cacheAttachments(boolean cacheAttachments) {
    this.cacheAttachmentsEnabled = cacheAttachments;
  }

  /**
   * Returns a list of ident request objects according to the given query parameters.
   * No exception is thrown when a callback was provided.
   *
   * @param queryParams A set of parameters a ident request must match.
   * @param callback    Callback for asynchronous handling.
   * @return The ident requests or null if a callback was provided.
   * @throws com.secucard.connect.ServiceException     If an error happens executing the service.
   * @throws com.secucard.connect.ServerErrorException If the service could not be executed by the secucard server.
   */
  public List<IdentRequest> getIdentRequests(QueryParams queryParams, Callback<List<IdentRequest>> callback) {
    return new ServiceTemplate().getAsList(IdentRequest.class, queryParams, callback);
  }

  /**
   * Returns a single ident request object.
   * No exception is thrown when a callback was provided.
   *
   * @param id       The id of the ident request.
   * @param callback Callback for asynchronous handling.
   * @return The ident request or null if a callback was provided.
   * @throws com.secucard.connect.ServiceException     If an error happens executing the service.
   * @throws com.secucard.connect.ServerErrorException If the service could not be executed by the secucard server.
   */
  public IdentRequest getIdentRequest(final String id, Callback<IdentRequest> callback) {
    return new ServiceTemplate().get(IdentRequest.class, id, callback);
  }

  /**
   * Returns a ident result for a given  ident request ids.
   * No exception is thrown when a callback was provided.
   *
   * @param identRequestIds     The request ids to get the results for.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return The ident results or null if a callback was provided.
   * @throws com.secucard.connect.ServiceException     If an error happens executing the service.
   * @throws com.secucard.connect.ServerErrorException If the service could not be executed by the secucard server.
   */
  public List<IdentResult> getIdentResultsByRequestIds(final List<String> identRequestIds,
                                                       Callback<List<IdentResult>> callback,
                                                       final boolean downloadAttachments) {
    // todo: better avoid query and access by id?

    StringBuilder query = new StringBuilder();
    for (Iterator<String> iterator = identRequestIds.iterator(); iterator.hasNext(); ) {
      String id = iterator.next();
      query.append("request.id:").append(id);
      if (iterator.hasNext()) {
        query.append(" or ");
      }
    }
    QueryParams params = new QueryParams();
    params.setQuery(query.toString());

    return new ServiceTemplate() {
      @Override
      protected void onResult(Object arg) {
        ObjectList<IdentResult> list = (ObjectList<IdentResult>) arg;
        if (list != null) {
          processAttachments(list.getList(), downloadAttachments);
        }
      }
    }.getAsList(IdentResult.class, params, callback);
  }

  /**
   * Creates a new ident request.
   * No exception is thrown when a callback was provided.
   *
   * @param newIdentRequest The data for the ident request to create.
   * @param callback        Callback for asynchronous handling.
   * @return The created ident request or null if a callback was provided.
   * @throws com.secucard.connect.ServiceException     If an error happens executing the service.
   * @throws com.secucard.connect.ServerErrorException If the service could not be executed by the secucard server.
   */
  public IdentRequest createIdentRequest(final IdentRequest newIdentRequest, Callback<IdentRequest> callback) {
    return new ServiceTemplate().create(newIdentRequest, callback);
  }

  /**
   * Returns a list of ident result objects according to the given query parameters.
   * Supports also the optional download and caching of all attachments before returning.
   * This will speed up clients access to them but involves increasing memory usage.
   * No exception is thrown when a callback was provided.
   *
   * @param queryParams         A set of parameters a ident result must match.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned results + persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return The ident results or null if a callback was provided.
   * @throws com.secucard.connect.ServiceException     If an error happens executing the service.
   * @throws com.secucard.connect.ServerErrorException If the service could not be executed by the secucard server.
   */
  public List<IdentResult> getIdentResults(final QueryParams queryParams, Callback<List<IdentResult>> callback,
                                           final boolean downloadAttachments) {
    return new ServiceTemplate() {
      @Override
      protected void onResult(Object arg) {
        ObjectList<IdentResult> list = (ObjectList<IdentResult>) arg;
        if (list != null) {
          processAttachments(list.getList(), downloadAttachments);
        }
      }
    }.getAsList(IdentResult.class, queryParams, callback);
  }

  /**
   * Returns a single ident result object.
   * No exception is thrown when a callback was provided.
   *
   * @param id                  The
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return The ident result or null if a callback was provided.
   * @throws com.secucard.connect.ServiceException     If an error happens executing the service.
   * @throws com.secucard.connect.ServerErrorException If the service could not be executed by the secucard server.
   */
  public IdentResult getIdentResult(final String id, Callback<IdentResult> callback, final boolean downloadAttachments) {
    return new ServiceTemplate() {
      @Override
      protected void onResult(Object arg) {
        processAttachments(Arrays.asList((IdentResult) arg), downloadAttachments);
      }
    }.get(IdentResult.class, id, callback);
  }

  /**
   * Register an event handler, see {@link IdentEventHandler} for which event.<br/>
   * This handler will be called then when the event is passed to
   * {@link com.secucard.connect.Client#handleEvent(String, boolean)}
   * The event is discarded if no handler is registered.<br/>
   * Note: Registering a handler multiple times just replaces the previous instance.
   *
   * @param handler The handler instance or null to remove the handler.
   */
  public void onIdentRequestChanged(IdentEventHandler handler) {
    if (handler != null) {
      handler.setService(this);
    }
    getEventDispatcher().registerListener(IdentEventHandler.ID, handler);
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
   * {@link com.secucard.connect.Client#handleEvent(String, boolean)}.
   */
  public static abstract class IdentEventHandler
      extends EventHandlerCallback<Event<List<IdentRequest>>, List<IdentResult>> {
    public static final String ID = IdentRequest.OBJECT + Events.TYPE_CHANGED;
    private IdentService service;

    private void setService(final IdentService service) {
      this.service = service;
    }

    @Override
    public boolean accept(Event<List<IdentRequest>> event) {
      return IdentRequest.OBJECT.equals(event.getTarget()) && Events.TYPE_CHANGED.equals(event.getType());
    }

    @Override
    protected List<IdentResult> process(Event<List<IdentRequest>> event) {
      List<IdentRequest> requests = event.getData();
      List<String> ids;
      if (requests == null) {
        ids = Collections.emptyList();
      } else {
        ids = new ArrayList<>(requests.size());
        for (IdentRequest request : requests) {
          ids.add(request.getId());
        }
      }

      return service.getIdentResultsByRequestIds(ids, null, downloadAttachments(requests));
    }

    /**
     * Indicates if the idents result attachments associated with the given idents requests should be downloaded or not.
     */
    public abstract boolean downloadAttachments(List<IdentRequest> requests);
  }
}
