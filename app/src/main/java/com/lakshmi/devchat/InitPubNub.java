package com.lakshmi.devchat;

import android.app.Application;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class InitPubNub extends Application {
    final static String pubkey = "pub-c-910212e4-6810-4b55-ac8b-cda9c65fb6a8";
    final static String subkey= "sub-c-87929408-98b3-11e7-bec3-c65ebd354f7d";


    static String mUsername = "testnick";

    static PNConfiguration PNconfig = new PNConfiguration()
            .setPublishKey(pubkey)
            .setSubscribeKey(subkey)
            .setUuid(mUsername);

    static PubNub pubnub = new PubNub(PNconfig);


}
