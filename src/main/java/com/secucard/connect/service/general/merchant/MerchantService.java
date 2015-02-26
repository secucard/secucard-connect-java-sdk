/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.merchant;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.merchant.Merchant;
import com.secucard.connect.model.general.merchant.PublicMerchant;
import com.secucard.connect.service.AbstractService;

public class MerchantService extends AbstractService {

    /**
     * Get the merchant with the given ID
     *
     * @param id Merchant ID
     * @return The merchant with the given ID or null if not found
     */
    public Merchant getMerchant(String id, Callback<Merchant> callback) {
        try {
            return getRestChannel().getObject(Merchant.class, id, callback);
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

    /**
     * Get a list of merchants
     *
     * @param queryParams Query params to find the wanted merchants
     * @return List of merchants
     */
    public ObjectList<Merchant> getMerchants(QueryParams queryParams, final Callback<ObjectList<Merchant>> callback) {
        try {
            ObjectList<Merchant> objects = getRestChannel().findObjects(Merchant.class, queryParams,
                    callback);
            return objects;
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

}
