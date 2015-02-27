package com.secucard.connect.service.loyalty;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.loyalty.Card;
import com.secucard.connect.service.AbstractService;

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
    try {
      ObjectList<Card> objects = getRestChannel().findObjects(Card.class, queryParams,
              callback);
      return objects;
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  /**
   * Assign a card
   *
   * @param cardNumber Card number
   * @return Assigned card
   */
  public Card assignCard(String cardNumber, Callback<Card> callback) {
    return getRestChannel().execute(Card.class, cardNumber, "assignUser", "me", null, Card.class, callback);
  }
}
