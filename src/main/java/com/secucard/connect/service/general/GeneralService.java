package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class GeneralService extends AbstractService {

  public Skeleton getSkeleton(final String id, final Callback<Skeleton> callback) {
    return new Invoker<Skeleton>() {
      @Override
      protected Skeleton handle(Callback<Skeleton> callback) {
        return getChannel().getObject(Skeleton.class, id, callback);
      }
    }.invoke(callback);
  }


  public List<Skeleton> getSkeletons(final QueryParams queryParams, final Callback<List<Skeleton>> callback) {
    return new ConvertingInvoker<ObjectList<Skeleton>, List<Skeleton>>() {
      @Override
      protected ObjectList<Skeleton> handle(Callback<ObjectList<Skeleton>> callback) {
        return getRestChannel().findObjects(Skeleton.class, queryParams, callback);
      }

      @Override
      protected List<Skeleton> convert(ObjectList<Skeleton> object) {
        return object == null ? null : object.getList();
      }
    }.invokeAndConvert(callback);
  }


}
