/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.accounts.beacon;

import com.secucard.connect.model.general.accounts.BeaconEnvironment.BeaconList;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class BeaconService extends AbstractService {

    /**
     * Send a BeaconList.
     *
     * @param beaconList List of found Beacons
     * @return True if successfully, false else.
     */
    public boolean sendBeacons(BeaconList beaconList) {
        beaconList.setId("me");
        Result result = getStompChannel().saveObject(beaconList, null, Result.class);
        return result.getResult().equals("true");
    }

}
