/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.accounts.location;

import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class LocationService extends AbstractService {

    /**
     * Send a location.
     *
     * @param location The location to send.
     * @return True if successfully, false else.
     */
    public boolean sendLocation(Location location) {
        location.setId("me");
        Result result = getStompChannel().saveObject(location, null, Result.class);
        return result.getResult().equals("true");
    }

}
