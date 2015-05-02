package com.secucard.connect.service.custom;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.model.general.Account;
import com.secucard.connect.model.general.Location;
import com.secucard.connect.model.general.PublicMerchant;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.Map;

public class AppService extends AbstractService {

  public Object getFoo(String arg) {
    Map foo = invoke("app_a1621caf12f1499c7ffab0c4", "getFoo", arg, Map.class, null);
    return foo;
  }

  public void updateLocation() {
    final Location object = new Location(51, 55, 1);

    Result result = new ServiceTemplate(Channel.REST).update(Account.class, "me", "location", null, object,
        Result.class, null);

    System.out.println(result);
  }

  public Object getList() {
    RestChannel channel = (RestChannel) getRestChannel();
    QueryParams queryParams = new QueryParams();
//    queryParams.setOffset(1);
//    queryParams.setCount(2);
    //queryParams.setFields("a"); seems not to work properly
//    queryParams.addSortOrder("a", QueryParams.SORT_ASC);
//    queryParams.addSortOrder("b", QueryParams.SORT_DESC);
//    queryParams.setQuery("a:abc1? OR (b:*0 AND NOT c:???1??)");
//    queryParams.addSortOrder("_geometry", QueryParams.SORT_ASC);
    queryParams.setGeoQuery(new QueryParams.GeoQuery("geometry", 51.175214, 14.027788, "1000m"));
    try {
      return channel.getList(PublicMerchant.class, queryParams, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public <T> T invoke(String appId, String method, Object arg, Class<T> returnType, Callback<T> callback) {
    try {
      RestChannel channel = (RestChannel) getRestChannel();
//      StompChannel channel = (StompChannel) getStompChannel();
      return channel.execute(appId, method, arg, returnType, callback);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
