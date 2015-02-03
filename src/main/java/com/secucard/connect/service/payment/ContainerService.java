package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

public class ContainerService extends AbstractService {

  public ObjectList<Container> getContainers(QueryParams queryParams, final Callback<ObjectList<Container>> callback) {
    try {
      ObjectList<Container> objects = getRestChannel().findObjects(Container.class, queryParams,
          callback);
      return objects;
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  public Container createContainer(final Container container, Callback<Container> callback) {
    return new Invoker<Container>() {
      @Override
      protected Container handle(Callback<Container> callback) throws Exception {
        return getChannel().createObject(container, callback);
      }
    }.invoke(callback);
  }

  public Container updateContainer(final Container container, Callback<Container> callback) {
    return new Invoker<Container>() {
      @Override
      protected Container handle(Callback<Container> callback) throws Exception {
        return getChannel().updateObject(container, callback);
      }
    }.invoke(callback);
  }

  public Container updateContainerAssignment(final String containerId, final String customerId, Callback<Container> callback) {
    return new Invoker<Container>() {
      @Override
      protected Container handle(Callback<Container> callback) throws Exception {
        return getChannel().updateObject(Container.class, containerId, "assign", customerId, new Customer(),
            Container.class, callback);
      }
    }.invoke(callback);
  }

  public void deleteContainerAssignment(final String containerId, Callback callback) {
    new Invoker<Void>() {
      @Override
      protected Void handle(Callback<Void> callback) throws Exception {
        getChannel().deleteObject(Container.class, containerId, "assign", null, callback);
        return null;
      }
    }.invoke(callback);
  }

  public void deleteContainer(final String id, Callback callback) {
    new Invoker<Void>() {
      @Override
      protected Void handle(Callback<Void> callback) throws Exception {
        getChannel().deleteObject(Container.class, id, callback);
        return null;
      }
    }.invoke(callback);
  }


}
