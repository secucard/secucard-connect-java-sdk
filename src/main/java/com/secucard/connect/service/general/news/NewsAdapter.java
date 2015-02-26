package com.secucard.connect.service.general.news;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.news.News;
import com.secucard.connect.service.AbstractService;

/**
 * Created by Steffen Schröder on 26.02.15.
 */
public class NewsAdapter extends AbstractService {

    /**
     * Return a list of news
     *
     * @param queryParams Query params to find the wanted news
     * @return A list of found news
     */
    public ObjectList<News> getNews(QueryParams queryParams, final Callback<ObjectList<News>> callback) {
        try {
            ObjectList<News> objects = getRestChannel().findObjects(News.class, queryParams,
                    callback);
            return objects;
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

}
