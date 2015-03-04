package com.secucard.connect.service.loyalty;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Store;
import com.secucard.connect.model.loyalty.Card;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import org.json.JSONException;
import org.json.JSONObject;

public class CardsService extends AbstractService {

  /**
   * Get the card with the given ID
   *
   * @param id Card ID
   * @return The card with the given ID or null if not found
   */
  public Card getCard(String id, Callback<Card> callback) {
    try {
      return getRestChannel().getObject(Card.class, id, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  /**
   * Get a list of cards
   *
   * @param queryParams Query params to find the wanted cards
   * @return List of cards
   */
  public ObjectList<Card> getCards(QueryParams queryParams, final Callback<ObjectList<Card>> callback) {
    return getRestChannel().findObjects(Card.class, queryParams, callback);
  }

  /**
   * Assign a card
   *
   * @param cardNumber Card number
   * @return Assigned card
   */
  public Boolean assignUser(final String cardNumber, final Object pin, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return getRestChannel().execute(Card.class, cardNumber, "assignUser", "me", pin, Result.class, callback);
      }
    }.invokeAndConvert(callback);

  }
}
