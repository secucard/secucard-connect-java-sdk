package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class ContainerService extends AbstractService {

  public List<Container> getContainers(QueryParams queryParams, final Callback<List<Container>> callback) {
    return getList(Container.class, queryParams, callback, null);
  }

  /**
   * Creating a payment container.
   * The container data must at least provide a valid IBAN in private data!
   * See {@link com.secucard.connect.model.payment.Data}, all other data is optional.
   * Returns validated and completed container data on success.
   * Throws exception when validation failed, see "throws" section.
   *
   * @param container The payment container data.
   * @param callback  Callback to get notified when completed or failed.
   * @return The validated and completed data like Container.publicData.
   * @throws com.secucard.connect.SecuException if a error happens.
   *                                            If the validation failed the cause is of type
   *                                            {@link com.secucard.connect.ProductException} and Status.errorDetails
   *                                            has the reason, like "invalid iban and bic combination".
   */
  public Container createContainer(final Container container, Callback<Container> callback) {
    return create(container, callback, null);
  }

  public Container updateContainer(final Container container, Callback<Container> callback) {
    return update(container, callback, null);
  }

  public void deleteContainer(final String id, Callback<Void> callback) {
    delete(Container.class, id, callback, null);
  }
}
