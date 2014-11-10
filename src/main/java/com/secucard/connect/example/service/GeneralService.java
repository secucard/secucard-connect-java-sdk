package com.secucard.connect.example.service;

import com.secucard.connect.client.BaseClient;
import com.secucard.connect.model.general.skeleton.Skeleton;

import java.util.List;

public class GeneralService extends BaseClient {

  public Skeleton getSkeleton(String id){
    return context.getChannnel().getObject(Skeleton.class, id);
  }

  public List<Skeleton> getSkeletons(){
    return context.getChannnel().findObjects(Skeleton.class, null).getList();
  }

}
