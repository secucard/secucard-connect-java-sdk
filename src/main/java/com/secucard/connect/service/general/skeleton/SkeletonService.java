/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.skeleton;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;

public class SkeletonService extends AbstractService {

    public Skeleton getSkeleton(String id, Callback<Skeleton> callback) {
        try {
            return getRestChannel().getObject(Skeleton.class, id, callback);
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

    public ObjectList<Skeleton> getSkeletons(QueryParams queryParams, final Callback<ObjectList<Skeleton>> callback) {
        try {
            ObjectList<Skeleton> objects = getRestChannel().findObjects(Skeleton.class, queryParams,
                    callback);
            return objects;
        } catch (Exception e) {
            handleException(e, callback);
        }
        return null;
    }

}
