package com.secucard.connect.product.general;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.general.model.Notification;

public class NotificationsService extends ProductService<Notification> {

  @Override
  public ServiceMetaData<Notification> createMetaData() {
    return new ServiceMetaData<>("general", "notifications", Notification.class);
  }
}
