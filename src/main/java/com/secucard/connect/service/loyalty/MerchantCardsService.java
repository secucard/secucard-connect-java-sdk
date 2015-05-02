package com.secucard.connect.service.loyalty;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.loyalty.MerchantCard;
import com.secucard.connect.service.AbstractService;

public class MerchantCardsService extends AbstractService {

  /**
   * Get a list of merchant cards
   *
   * @param queryParams Query params to find the wanted cards
   * @return List of merchant cards
   */
  public ObjectList<MerchantCard> getMerchantCards(QueryParams queryParams, Callback<ObjectList<MerchantCard>> callback) {
    return new ServiceTemplate().getList(MerchantCard.class, queryParams, callback);
  }

  public MerchantCard getMerchantCard(String id, Callback<MerchantCard> callback) {
    return new ServiceTemplate().get(MerchantCard.class, id, callback);
  }

}
