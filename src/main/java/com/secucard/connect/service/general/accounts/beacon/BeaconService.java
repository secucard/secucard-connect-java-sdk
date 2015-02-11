/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.service.general.accounts.beacon;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.accounts.Account;
import com.secucard.connect.model.general.accounts.beaconenvironment.BeaconEnvironment;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class BeaconService extends AbstractService {

  /**
   * Send a BeaconList.
   *
   * @param beaconList List of found Beacons
   * @return True if successfully, false else.
   */
  public boolean sendBeacons(List<BeaconEnvironment> beaconList) {
    ObjectList<BeaconEnvironment> objectList = new ObjectList<>();
    objectList.setList(beaconList);
    Result result = getStompChannel().updateObject(Account.class, "me", "beacon", null, objectList, Result.class,
        null);
    return result.getResult().equals("true");
  }

}
