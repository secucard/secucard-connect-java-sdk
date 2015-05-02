package com.secucard.connect;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.service.general.SkeletonService;

public class ClientDemo {

  public static void main(String[] args) throws Exception {

    Client client = Client.create("clientdemo", "config-clientdemo.properties");

    client.connect();

    ObjectList<Skeleton> skeletons = client.getService(SkeletonService.class).getSkeletons(null, null);

    client.disconnect();
  }


}
