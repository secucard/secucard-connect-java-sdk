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
    return super.getObjectList(MerchantCard.class, queryParams, callback, null);
  }

  public MerchantCard getMerchantCard(String id, Callback<MerchantCard> callback) {
    return super.get(MerchantCard.class, id, callback, null);
  }

}
