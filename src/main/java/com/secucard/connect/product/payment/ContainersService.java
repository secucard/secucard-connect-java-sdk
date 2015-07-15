package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Container;
import com.secucard.connect.product.payment.model.Customer;

public class ContainersService extends ProductService<Container> {

  @Override
  public ServiceMetaData<Container> createMetaData() {
    return new ServiceMetaData<>("payment", "containers", Container.class);
  }

  /**
   * Creating a payment container.
   * The container data must at least provide a valid IBAN in private data!
   * See {@link com.secucard.connect.product.payment.model.Data}, all other data is optional.
   * Returns validated and completed container data on success.
   * Throws exception when validation failed, see "throws" section.
   *
   * @param container The payment container data.
   * @param callback  Callback to get notified when completed or failed.
   * @return The validated and completed data like Container.publicData.
   * @throws com.secucard.connect.client.SecucardConnectException if a error happens. userMessage field has the reason,
   *                                                   like "invalid iban and bic combination".
   */
  public Container create(final Container container, Callback<Container> callback) {
    return super.create(container, callback);
  }

  public Container updateContainerAssignment(final String containerId, final String customerId, Callback<Container> callback) {
    return super.execute(containerId, "assign", customerId, new Customer(), Container.class, null, callback);
  }

  public void deleteContainerAssignment(final String containerId, Callback<Void> callback) {
    super.delete(containerId, "assign", null, null, callback);
  }
}
