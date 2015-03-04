package com.secucard.connect.service.loyalty;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.loyalty.Card;
import com.secucard.connect.model.loyalty.MerchantCard;
import com.secucard.connect.service.AbstractService;

import org.json.JSONObject;

public class MerchantCardsService extends AbstractService {

    /**
   * Get a list of merchant cards
   *
   * @param queryParams Query params to find the wanted cards
   * @return List of merchant cards
   */
  public ObjectList<MerchantCard> getMerchantCards(QueryParams queryParams, final Callback<ObjectList<MerchantCard>> callback) {
    return getRestChannel().findObjects(MerchantCard.class, queryParams, callback);
  }

}
