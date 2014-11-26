package com.secucard.connect.service.general;

import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class GeneralService extends AbstractService {

  public Skeleton getSkeleton(String id) {
    try {
      return getChannnel().getObject(Skeleton.class, id);
    } catch (Exception e) {
      handleException(e);
      return null;
    }
  }

  public List<Skeleton> getSkeletons() {
    try {
      return getChannnel().findObjects(Skeleton.class, null).getList();
    } catch (Exception e) {
      handleException(e);
      return null;
    }
  }

}
