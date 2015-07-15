package com.secucard.connect.product.services;

public class Services {
  public Services(IdentRequestsService identrequests, IdentResultsService identresults) {
    this.identrequests = identrequests;
    this.identresults = identresults;
  }

  public static Class<IdentRequestsService> Identrequests = IdentRequestsService.class;
  public IdentRequestsService identrequests;

  public static Class<IdentResultsService> Identresults = IdentResultsService.class;
  public IdentResultsService identresults;
}
