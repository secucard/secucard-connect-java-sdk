package com.secucard.connect.client.smart;

import com.secucard.connect.channel.Channel;
import com.secucard.connect.client.AbstractService;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;

import java.util.List;

/**
 * The Smart Product operations.
 */
public class SmartService extends AbstractService {
  private ClientContext context;

  /**
   * Setting a listener for receiving event messages.
   *
   * @param eventListener
   */
  @Override
  public void setEventListener(EventListener eventListener) {
    context.getStompChannel().setEventListener(eventListener);
  }

  @Override
  public void setContext(ClientContext context) {
    this.context = context;
  }

  /**
   * Register a device.
   *
   * @param device The device to register.
   * @return True if successfully, false else.
   */
  public boolean registerDevice(Device device) {
    return context.getStompChannel().execute("register", new String[]{device.getId()}, device, null);
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents() {
    ObjectList<Ident> idents = context.getChannnel().findObjects(Ident.class, null);
    if (idents != null) {
      return idents.getList();
    }
    return null;
  }

  /**
   * Creating a transaction.
   *
   * @param transaction The transaction data to save.
   * @return The new transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction createTransaction(Transaction transaction) {
    return context.getChannnel().saveObject(transaction);
  }

  /**
   * Starting/Exceuting a transaction.
   *
   * @param transaction The transaction data.
   * @return The result data.
   */
  public Result startTransaction(Transaction transaction) {
    Channel channnel = context.getChannnel();
    String[] id = {transaction.getId(), "demo"};
    return channnel.execute("start", id, transaction, Result.class);
  }
}
