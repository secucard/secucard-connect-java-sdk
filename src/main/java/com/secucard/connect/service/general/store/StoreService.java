package com.secucard.connect.service.general.store;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.stores.Store;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

/**
 * Created by Steffen Schröder on 25.02.15.
 *
 * Service to handle the Stores
 */
public class StoreService extends AbstractService {

    /**
     * Set cjeckIn state for the store
     *
     * @param storeId StoreID
     * @return True if successfully updated, false else.
     */
    public boolean checkIn(final String storeId, Callback<Boolean> callback){
       return new Result2BooleanInvoker() {
            @Override
            protected Result handle(Callback<Result> callback) throws Exception {
                return getRestChannel().execute(Store.class, storeId, "checkin", null, null, Result.class, callback);
            }
        }.invokeAndConvert(callback);
    }

    /**
     * Return a list of stores
     *
     * @param queryParams Query params to find the wanted stores
     * @return A list of found stores
     */
    public ObjectList<Store> getStores(QueryParams queryParams, final Callback<ObjectList<Store>> callback) {
        try {
            ObjectList<Store> objects = getRestChannel().findObjects(Store.class, queryParams,
                    callback);
            return objects;
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }
}
