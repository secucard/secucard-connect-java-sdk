package com.secucard.connect.product.smart;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.smart.model.Checkin;

import java.util.List;

/**
 * Implements the smart/checkins operations.
 */
public class CheckinService extends ProductService<Checkin> {

  public static final ServiceMetaData<Checkin> META_DATA = new ServiceMetaData<>("smart", "checkins", Checkin.class);

  @Override
  public ServiceMetaData<Checkin> getMetaData() {
    return META_DATA;
  }

  @Override
  public Options getDefaultOptions() {
    return new Options(Options.CHANNEL_STOMP);
  }
  
  public String getSecuofficeUrl() {
    // TODO return
  }
  
  public String getCheckinOverviewUrl() {
    // TODO return
  }

  /**
   * Set a callback to get notified when a check in happened.
   */
  public void onCheckinSelected(final Callback<Checkin> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, Checkin>(callback) {
        @Override
        protected Checkin process(Event event) {
          return get(); // TODO: parameter prüfen
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }

  /**
   * Set a callback to get notified when a check in happened.
   */
  public void onCheckinsChanged(final Callback<List<Checkin>> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, List<Checkin>>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && getMetaData().getObject().equals(event.getTarget());
        }

        @Override
        protected List<Checkin> process(Event event) {
          return getAll(null);
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }

  /**
   * Returning check in data.<br/>
   * Data may contain a downloadable image. When the method returns either by callback or directly it is
   * guarantied that all images are completely downloaded to an local cache. To get the image content simply call
   * {@link MediaResource#getInputStream()}  or
   * {@link MediaResource#getContents()}. The streaming approach should be favored,
   * since the other obviously loads the content completely in memory before returning, which may not be optimal in any
   * case.<br/>
   * Note: Depending on the network it may of course take a while to download all, so without using an callback the
   * method may block until ready. Id that's not acceptable use the callback.
   *
   * @param callback Callback for async result processing.
   * @return If no callback is provided a list of check in data objects or null if no data could be found.
   * Returns always null if a callback was provided, the callbacks methods return the result analogous.
   */
  public List<Checkin> getAll(Callback<List<Checkin>> callback) {
    Options options = getDefaultOptions();
    options.resultProcessing = new Callback.Notify<ObjectList<Checkin>>() {
      @Override
      public void notify(ObjectList<Checkin> result) {
        processCheckins(result);
      }
    };
    return super.getSimpleList(null, options, callback);
  }

  /**
   * Downloading check in pictures sequentially and return just when done.
   * todo: consider doing it in parallel to speed up, but not sure if rest channel can handle this properly
   */
  private void processCheckins(ObjectList<Checkin> checkins) {
    if (checkins != null && checkins.getList() != null) {
      for (Checkin checkin : checkins.getList()) {
        try {
          downloadMedia(checkin.getPictureObject());
        } catch (Exception e) {
          // download went wrong, so skip picture and continue
          checkin.setPicture(null);
          checkin.setError(e);
        }
      }
    }
  }

}
