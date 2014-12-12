package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.merchant.PublicMerchant;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.util.Converter;

import java.util.List;

public class GeneralService extends AbstractService {

  public Skeleton getSkeleton(String id, Callback<Skeleton> callback) {
    try {
      return getChannel().getObject(Skeleton.class, id, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  public List<Skeleton> getSkeletons(QueryParams queryParams, final Callback<List<Skeleton>> callback) {
    try {
      Converter<ObjectList<Skeleton>, List<Skeleton>> converter = new Converter<ObjectList<Skeleton>, List<Skeleton>>() {
        @Override
        public List<Skeleton> convert(ObjectList<Skeleton> value) {
          return value == null ? null : value.getList();
        }
      };
      ObjectList<Skeleton> objects = getRestChannel().findObjects(Skeleton.class, queryParams,
          getCallbackAdapter(callback, converter));
      return converter.convert(objects);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }
}
