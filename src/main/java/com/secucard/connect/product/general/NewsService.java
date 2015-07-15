package com.secucard.connect.product.general;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.general.model.News;

public class NewsService extends ProductService<News> {

  @Override
  public ServiceMetaData<News> createMetaData() {
    return new ServiceMetaData<>("general", "news", News.class);
  }

  /**
   * Mark news as read
   *
   * @param pid News ID
   * @return True if successfully updated, false else.
   */
  public Boolean markRead(final String pid, Callback<Boolean> callback) {
    return executeToBool(pid, "markRead", null, null, callback);
  }

}
