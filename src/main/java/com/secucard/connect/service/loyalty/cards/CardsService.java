/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.loyalty.cards;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.loyalty.Card;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.service.AbstractService;

public class CardsService extends AbstractService {

    public Card getCard(String id, Callback<Card> callback) {
        try {
            return getRestChannel().getObject(Card.class, id, callback);
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

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
}
