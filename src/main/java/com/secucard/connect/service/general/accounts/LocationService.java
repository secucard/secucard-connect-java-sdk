/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.accounts;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class LocationService extends AbstractService {

    /**
     * Send a location.
     *
     * @param location The location to send.
     * @return True if successfully, false else.
     */
    public boolean sendLocation(Location location) {
//        try {
//            InvocationResult result = getStompChannel().execute("General.Accounts.Location", new String[]{"me"}, location, InvocationResult.class);
//            return Boolean.parseBoolean(result.getResult());
//        } catch (Exception e) {
//            handleException(e);
//        }
        return false;
    }

}
