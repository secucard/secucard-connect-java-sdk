/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.merchant;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.merchant.PublicMerchant;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.service.AbstractService;

public class PublicMerchantService extends AbstractService {

    public PublicMerchant getPublicMerchant(String id, Callback<PublicMerchant> callback) {
        try {
            return getRestChannel().getObject(PublicMerchant.class, id, callback);
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

    public ObjectList<PublicMerchant> getPublicMerchants(QueryParams queryParams, final Callback<ObjectList<PublicMerchant>> callback) {
        try {
            ObjectList<PublicMerchant> objects = getRestChannel().findObjects(PublicMerchant.class, queryParams,
                    callback);
            return objects;
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

}
