package com.secucard.connect.channel.rest;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.smart.Ident;

import javax.ws.rs.core.GenericType;

public class StaticGenericTypeResolver implements RestChannel.GenericTypeResolver {

  @Override
  public GenericType getGenericType(Class type) {

    if (Skeleton.class.equals(type)){
      return new GenericType<ObjectList<Skeleton>>(){};
    }

    if (Ident.class.equals(type)){
      return new GenericType<ObjectList<Ident>>(){};
    }

    throw new IllegalArgumentException("Invalid type");
  }

  // No better (dynamic) solution found (if there is any better at all).
  // Keep in sync with the model...

}
