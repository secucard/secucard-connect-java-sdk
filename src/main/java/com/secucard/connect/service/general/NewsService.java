package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.News;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class NewsService extends AbstractService {

  /**
   * Return a list of news
   *
   * @param queryParams Query params to find the wanted news
   * @return A list of found news
   */
  public ObjectList<News> getNews(QueryParams queryParams, final Callback<ObjectList<News>> callback) {
    return new ServiceTemplate().getList(News.class, queryParams, callback);
  }

  /**
   * Mark news as read
   *
   * @param pid News ID
   * @return True if successfully updated, false else.
   */
  public Boolean markRead(final String pid, Callback<Boolean> callback) {
    return new ServiceTemplate().executeToBoolean(News.class, pid, "markRead", null, null, Result.class, callback);
  }
}
