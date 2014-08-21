package com.secucard.connect.java.client;

import java.util.List;

import com.secucard.connect.java.client.general.SkelentonsListModel;
import com.secucard.connect.java.client.general.Skeletons;
import com.secucard.connect.java.client.general.SkeletonsApi;
import com.secucard.connect.java.client.lib.ResourceFactory;
import com.secucard.connect.java.client.oauth.OAuthClientCredentials;
import com.secucard.connect.java.client.oauth.OAuthPasswordCredentials;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SkeletonTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SkeletonTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SkeletonTest.class );
    }

    /**
     * Rigorous Test :-)
     */
    public void testApp()
    {
    	System.out.println("test app method running");
    	OAuthClientCredentials client_cred = new OAuthClientCredentials("webapp", "821fc7042ec0ddf5cc70be9abaa5d6d311db04f4679ab56191038cb6f7f9cb7c");
    	OAuthPasswordCredentials pass_cred = new OAuthPasswordCredentials("sten@beispiel.net", "secrets");
    	System.out.println("credentials created");
    	ResourceFactory res = new ResourceFactory(client_cred, pass_cred);
    	System.out.println("factory created");
    	SkeletonsApi skeletonApi = new SkeletonsApi(res);
    	System.out.println("The testing api created");

    	int count = 5;
    	int offset = 1;
    	SkelentonsListModel skels = skeletonApi.getSkeletons(count, offset);
    	System.out.println(skels.toString());

        assertTrue( skels.getCount() != null);
    }
}
