package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.PublicMerchant;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class PublicMerchantService extends AbstractService {

  /**
   * Get the public merchant with the given ID
   *
   * @param id PublicMerchant ID
   * @return The public merchant with the given ID or null if not found
   */
  public PublicMerchant getPublicMerchant(String id, Callback<PublicMerchant> callback) {
    return get(PublicMerchant.class, id, callback, null);
  }

  /*
  public PublicMerchant getPublicMerchant(String id, Callback<PublicMerchant> callback) {
    try {
      return getRestChannel().getObject(PublicMerchant.class, id, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  } */

  /**
   * Get a list of public merchants
   *
   * @param queryParams Query params to find the wanted public merchants
   * @return List of public merchants
   */
  public List<PublicMerchant> getPublicMerchants(QueryParams queryParams, final Callback<List<PublicMerchant>> callback) {
    return getList(PublicMerchant.class, queryParams, callback, null);
  }

  /*
  public ObjectList<PublicMerchant> getPublicMerchants(QueryParams queryParams, final Callback<ObjectList<PublicMerchant>> callback) {
    try {
      ObjectList<PublicMerchant> objects = getRestChannel().findObjects(PublicMerchant.class, queryParams,
          callback);
      return objects;
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  } */
}
