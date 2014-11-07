package com.secucard.connect.example.service;

import com.secucard.connect.BaseClient;
import com.secucard.connect.ClientConfig;
import com.secucard.connect.model.general.skeleton.Skeleton;

import java.util.List;

public class GeneralService extends BaseClient {

  public Skeleton getSkeleton(String id){
    return selectChannnel().getObject(Skeleton.class, id);
  }


  public static GeneralService create(ClientConfig config) {
    return BaseClient.create(config, GeneralService.class);
  }

  public List<Skeleton> getSkeletons(){
    return selectChannnel().findObjects(Skeleton.class, null).getList();
  }

}
