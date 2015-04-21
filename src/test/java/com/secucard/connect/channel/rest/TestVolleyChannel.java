package com.secucard.connect.channel.rest;

import android.content.Context;
import com.android.volley.ExecutorDelivery;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;
import java.util.concurrent.Executors;

public class TestVolleyChannel extends VolleyChannel {

  public TestVolleyChannel(String id, Context context, Configuration configuration) {
    super(id, context, configuration);
  }

  @Override
  public void open() {
    HttpStack stack;
//      stack = new HttpClientStack(AndroidHttpClient.newInstance("volley/0, robolectric mockup test"));
    stack = new HurlStack();

    // must use custom single threaded delivery
    // otherwise see: http://stackoverflow.com/questions/16816600/getting-robolectric-to-work-with-volley
    requestQueue = new RequestQueue(
        new DiskBasedCache(new File(context.getApplicationContext().getCacheDir(), "volley")),
        new BasicNetwork(stack),
        1,
        new ExecutorDelivery(Executors.newSingleThreadExecutor()));

    requestQueue.start();
  }
}
