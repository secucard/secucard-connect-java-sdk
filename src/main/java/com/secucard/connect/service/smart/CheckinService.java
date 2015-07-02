package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.smart.Checkin;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class CheckinService extends AbstractService {

  public void onCheckinsChanged(final Callback<List<Checkin>> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, List<Checkin>>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && Checkin.OBJECT.equals(event.getTarget());
        }

        @Override
        protected List<Checkin> process(Event event) {
          return getCheckins(null);
        }
      };
    }

    getEventDispatcher().registerListener(Checkin.OBJECT + Events.TYPE_CHANGED, listener);
  }

  /**
   * Returning check in data.<br/>
   * Data may contain a downloadable image. When the method returns either by callback or directly it is
   * guarantied that all images are completely downloaded to an local cache. To get the image content simply call
   * {@link com.secucard.connect.model.MediaResource#getInputStream()}  or
   * {@link com.secucard.connect.model.MediaResource#getContents()}. The streaming approach should be favored,
   * since the other obviously loads the content completely in memory before returning, which may not be optimal in any
   * case.<br/>
   * Note: Depending on the network it may of course take a while to download all, so without using an callback the
   * method may block until ready. Id that's not acceptable use the callback.
   *
   * @param callback Callback for async result processing.
   * @return If no callback is provided a list of check in data objects or null if no data could be found.
   * Returns always null if a callback was provided, the callbacks methods return the result analogous.
   */
  public List<Checkin> getCheckins(Callback<List<Checkin>> callback) {
    return new ServiceTemplate(Channel.STOMP) {
      @Override
      protected void onResult(Object arg) {
        processCheckins((ObjectList<Checkin>) arg);
      }
    }.getAsList(Checkin.class, null, callback);
  }

  public ObjectList<Checkin> getCheckinsList(Callback<ObjectList<Checkin>> callback) {
    return new ServiceTemplate(Channel.STOMP) {
      @Override
      protected void onResult(Object arg) {
        processCheckins((ObjectList<Checkin>) arg);
      }
    }.getList(Checkin.class, null, callback);
  }

  /**
   * Downloading check in pictures sequentially and return just when done.
   * todo: consider doing it in parallel to speed up, but not sure if rest channel can handle this properly
   */
  private void processCheckins(ObjectList<Checkin> checkins) {
    if (checkins != null && checkins.getList() != null) {
      for (Checkin object : checkins.getList()) {
        MediaResource picture = object.getPictureObject();
        if (picture != null) {
          if (!picture.isCached()) {
            picture.download();
          }
        }
      }
    }
  }

}
