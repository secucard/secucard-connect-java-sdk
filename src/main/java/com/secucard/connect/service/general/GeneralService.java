package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.CallbackAdapter;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class GeneralService extends AbstractService {

  public Skeleton getSkeleton(String id, Callback<Skeleton> callback) {
    try {
      return getChannnel().getObject(Skeleton.class, id, callback);
    } catch (Exception e) {
      handleException(e);
    }
    return null;
  }

  public List<Skeleton> getSkeletons(QueryParams queryParams, final Callback<List<Skeleton>> callback) {
    CallbackAdapter<ObjectList<Skeleton>, List<Skeleton>> adapter =
        null;
    if (callback != null) {
      adapter = new CallbackAdapter<ObjectList<Skeleton>, List<Skeleton>>(callback) {
        @Override
        protected List<Skeleton> convert(ObjectList<Skeleton> object) {
          return object.getList();
        }
      };
    }
    try {

      ObjectList<Skeleton> objects = getStompChannel().findObjects(Skeleton.class, queryParams, adapter);
      if (objects != null) {
        return objects.getList();
      }
    } catch (Exception e) {
      handleException(e);
    }
    return null;
  }

}
