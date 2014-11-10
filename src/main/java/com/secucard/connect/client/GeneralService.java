package com.secucard.connect.client;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.skeleton.Skeleton;

import java.util.List;

public class GeneralService extends AbstractService {
  private ClientContext context;

  @Override
  public void setContext(ClientContext context) {
    this.context = context;
  }

  @Override
  public void setEventListener(EventListener eventListener) {
    context.getStompChannel().setEventListener(eventListener);
  }

  public Skeleton getSkeleton(String id) {
    return context.getChannnel().getObject(Skeleton.class, id);
  }

  public List<Skeleton> getSkeletons() {
    return context.getChannnel().findObjects(Skeleton.class, null).getList();
  }

}
