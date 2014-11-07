package com.secucard.connect.example.service;

import com.secucard.connect.BaseClient;
import com.secucard.connect.ClientConfig;
import com.secucard.connect.jackson.general.merchant.Merchant;
import com.secucard.connect.model.general.skeleton.Skeleton;

import java.util.List;

public class GeneralService extends BaseClient {

  public Skeleton getSkeleton(String id){
    return selectChannnel().getObject(Skeleton.class, id);
  }
  public Merchant getMerchant(String id){
    return selectChannnel().getObject(Merchant.class, id);
  }

  public static GeneralService create(ClientConfig config) {
    return BaseClient.create(config, GeneralService.class);
  }

  public List<Skeleton> getSkeletons(){
    return selectChannnel().findObjects(Skeleton.class, null).getList();
  }

}
