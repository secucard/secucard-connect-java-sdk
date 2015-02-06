package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class ContainerService extends AbstractService {

  public List<Container> getContainers(QueryParams queryParams, final Callback<List<Container>> callback) {
    return get(Container.class, queryParams, callback, null);
  }

  public Container createContainer(final Container container, Callback<Container> callback) {
    return create(container, callback, null);
  }

  public Container updateContainer(final Container container, Callback<Container> callback) {
    return update(container, callback, null);
  }

  public Container updateContainerAssignment(final String containerId, final String customerId, Callback<Container> callback) {
    return execute(Container.class, containerId, "assign", customerId, new Customer(), Container.class, callback, null);
  }

  public void deleteContainerAssignment(final String containerId, Callback<Void> callback) {
    delete(Container.class, containerId, "assign", null, callback, null);
  }

  public void deleteContainer(final String id, Callback<Void> callback) {
    delete(Container.class, id, callback, null);
  }
}
