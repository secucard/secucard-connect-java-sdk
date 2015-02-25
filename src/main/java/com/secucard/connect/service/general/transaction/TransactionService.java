package com.secucard.connect.service.general.transaction;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.transaction.Transaction;
import com.secucard.connect.service.AbstractService;

/**
 * Created by Steffen Schröder on 25.02.15.
 */
public class TransactionService extends AbstractService {

    /**
     * Return a list of transactions
     *
     * @param queryParams Query params to find the wanted transactions
     * @return A list of found transactions
     */
    public ObjectList<Transaction> getStores(QueryParams queryParams, final Callback<ObjectList<Transaction>> callback) {
        try {
            ObjectList<Transaction> objects = getRestChannel().findObjects(Transaction.class, queryParams,
                    callback);
            return objects;
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }
}
