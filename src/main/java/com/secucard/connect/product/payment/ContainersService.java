package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Container;

/**
 * Implements the payment/containers operations.
 */
public class ContainersService extends ProductService<Container> {

  public static final ServiceMetaData<Container> META_DATA = new ServiceMetaData<>("payment",
      "containers", Container.class);

  @Override
  public ServiceMetaData<Container> getMetaData() {
    return META_DATA;
  }

  /**
   * Creating a payment container.
   * The container data must at least provide a valid IBAN in private data!
   * See {@link com.secucard.connect.product.payment.model.Data}, all other data is optional.
   * Returns validated and completed container data on success.
   * Throws exception when validation failed, see "throws" section.
   *
   * @param container The payment container data.
   * @param callback Callback to get notified when completed or failed.
   * @return The validated and completed data like Container.publicData.
   * @throws com.secucard.connect.client.APIError if a error happens. userMessage field has the
   * reason, like "invalid IBAN and BIC combination".
   */
  public Container create(final Container container, Callback<Container> callback) {
    return super.create(container, callback);
  }
}
