package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.service.AbstractService;

import java.util.List;

/**
 * This service is for testing purposes only.
 * Skeleton is not a valid secucard resource it exist just for testing and playing around.
 */
public class SkeletonService extends AbstractService {

  public Skeleton getSkeleton(final String id, final Callback<Skeleton> callback) {
    return new ServiceTemplate().get(Skeleton.class, id, callback);
  }

  public ObjectList<Skeleton> getSkeletons(final QueryParams queryParams, Callback<ObjectList<Skeleton>> callback) {
    return new ServiceTemplate(Channel.STOMP).getList(Skeleton.class, queryParams, callback);
  }

  public List<Skeleton> getSkeletonsAsList(final QueryParams queryParams, final Callback<List<Skeleton>> callback) {
    return new ServiceTemplate(){
      @Override
      protected void onResult(Object arg) {
        super.onResult(arg);
      }
    }.getAsList(Skeleton.class, queryParams, callback);
  }

  public ObjectList<Skeleton> getSkeletons2(final QueryParams queryParams, Callback<ObjectList<Skeleton>> callback) {
    return new ServiceTemplate(null, true) {
      @Override
      protected void onResult(Object arg) {
        throw new RuntimeException("ha!!!");
      }
    }.getList(Skeleton.class, queryParams, callback);
  }
}
