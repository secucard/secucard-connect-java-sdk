package com.secucard.connect.jmeter;

import com.secucard.connect.SecucardConnect;
import com.secucard.connect.auth.AbstractClientAuthDetails;
import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.auth.model.RefreshCredentials;
import com.secucard.connect.client.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.product.general.model.Notification;
import com.secucard.connect.product.smart.Smart;
import com.secucard.connect.product.smart.TransactionService;
import com.secucard.connect.product.smart.model.*;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SmartTransactionSampler extends AbstractJavaSamplerClient implements Serializable {
  private static final long serialVersionUID = 1L;
  SecucardConnect sc;
  private TransactionService trs;
  private String refreshToken;
  private String clientid;
  private String clientsecret;
  private String type;
  private String identValue;
  private int sum;
  private boolean sharedclient;

  @Override
  public void setupTest(JavaSamplerContext context) {
    getLogger().info("setup sampler " + this);
    refreshToken = context.getParameter("REFRESHTOKEN");
    clientid = context.getParameter("CLIENTID");
    clientsecret = context.getParameter("CLIENTSECRET");
    type = context.getParameter("TRANSTYPE");
    identValue = context.getParameter("IDENT");
    sum = context.getIntParameter("SUM");
//    sharedclient = context.getIntParameter("SHAREDCLIENT") == 1;
  }

  @Override
  public void teardownTest(JavaSamplerContext context) {
    getLogger().info("teardown sampler " + this);
    closeClient();
    getLogger().info("secucard client closed.");
  }

  @Override
  public Arguments getDefaultParameters() {
    Arguments defaultParameters = new Arguments();
    defaultParameters.addArgument("REFRESHTOKEN", "");
    defaultParameters.addArgument("TRANSTYPE", "");
    defaultParameters.addArgument("IDENT", "");
    defaultParameters.addArgument("SUM", "");
    defaultParameters.addArgument("CLIENTSECRET", "");
    defaultParameters.addArgument("CLIENTID", "");
//    defaultParameters.addArgument("SHAREDCLIENT", "1");
    return defaultParameters;
  }


  @Override
  public org.apache.jmeter.samplers.SampleResult runTest(JavaSamplerContext context) {

    final SampleResult result = new SampleResult();
    result.setSampleLabel(context.getParameter("TestElement.name"));
    result.sampleStart(); // start stopwatch

    TransactionService transactions;
    try {
      transactions = getService();
    } catch (Exception e) {
      sampleError(result, "Error getting transaction service.", e);
      return result;
    }

    try {

      Ident ident = new Ident();
      ident.setValue(identValue);

      Transaction newTrans = new Transaction();
      newTrans.setIdents(Collections.singletonList(ident));

      Basket basket = new Basket();
      BasketInfo basketInfo = new BasketInfo(sum, "EUR");
      newTrans.setBasket(basket);
      newTrans.setBasketInfo(basketInfo);

      Transaction trans = transactions.create(newTrans);
      if (!trans.getStatus().equals(Transaction.STATUS_CREATED)) {
        sampleError(result, "Invalid status for created transaction " + trans.getStatus(), null);
        return result;
      }

      getLogger().info("Transaction created");

      trans = transactions.start(trans.getId(), type, null);
      if (!trans.getStatus().equals(Transaction.STATUS_OK)) {
        sampleError(result, "Invalid status for started transaction " + trans.getStatus(), null);
        return result;
      }

      getLogger().info("Transaction started");

      List<ReceiptLine> receiptLines = trans.getReceiptLines();
      for (ReceiptLine line : receiptLines) {
        getLogger().info("Receipt Line: " + line.getLineType() + ", " + line.getValue());
      }

    } catch (Exception e) {
      sampleError(result, "Error happened", e);
      return result;
    }

    result.sampleEnd();
    result.setSuccessful(true);
    result.setResponseMessage("Successfully performed action");
    result.setResponseCodeOK();

    return result;
  }

  protected TransactionService getService() {
    if (trs != null) {
      return trs;
    }

    final SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get();

    // todo: revert config!

    cfg.clientAuthDetails = new AbstractClientAuthDetails(SecucardConnect.Configuration.DEFAULT_CACHE_DIR) {
      @Override
      public OAuthCredentials getCredentials() {
        return new RefreshCredentials(clientid, clientsecret, refreshToken);
      }

      @Override
      public ClientCredentials getClientCredentials() {
        return (ClientCredentials) this.getCredentials();
      }
    };

    final SecucardConnect cli = SecucardConnect.create(cfg);

    // Set up event listener, required to handle auth events!
    cli.onAuthEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        // no auth event expected
        throw new RuntimeException("Got unexpected auth event " + event);
      }
    });

    cli.open();
    getLogger().info("secucard client opened");

    TransactionService ts = cli.service(Smart.Transactions);

    ts.onCashierDisplayChanged(new Callback<Notification>() {
      @Override
      public void completed(Notification result) {
        getLogger().info("Cashier notification: " + result.getText());
      }

      @Override
      public void failed(Throwable cause) {
        throw new RuntimeException("Error processing cashier notification!");
      }
    });

    sc = cli;
    trs = ts;

    return ts;
  }

  protected void closeClient() {
    sc.close();
    sc = null;
    trs = null;
  }

  protected void sampleError(SampleResult result, String msg, Exception ex) {
    getLogger().error(msg, ex);
    result.sampleEnd();
    result.setResponseMessage(msg);
    result.setSuccessful(false);
  }
}
