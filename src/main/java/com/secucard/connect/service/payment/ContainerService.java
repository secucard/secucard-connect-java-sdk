package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class ContainerService extends AbstractService {

  public List<Container> getContainers(QueryParams queryParams, final Callback<List<Container>> callback) {
    return new ServiceTemplate().getAsList(Container.class, queryParams, callback);
  }

  public Container createContainer(final Container container, Callback<Container> callback) {
    return new ServiceTemplate().create(container, callback);
  }

  public Container updateContainer(final Container container, Callback<Container> callback) {
    return new ServiceTemplate().update(container, callback);
  }

  public Container updateContainerAssignment(final String containerId, final String customerId, Callback<Container> callback) {
    return new ServiceTemplate().execute(Container.class, containerId, "assign", customerId, new Customer(),
        Container.class, callback);
  }

  public void deleteContainerAssignment(final String containerId, Callback<Void> callback) {
    new ServiceTemplate().delete(Container.class, containerId, "assign", null, callback);
  }

  public void deleteContainer(final String id, Callback<Void> callback) {
    new ServiceTemplate().delete(Container.class, id, callback);
  }
}
