package com.lakshmi.devchat;

import android.app.Application;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class InitPubNub extends Application {

    public static PubNub pubnubDeclaration(String pubkey, String subkey, String mUsername) {
        PNConfiguration PNconfig = new PNConfiguration()
                .setPublishKey(pubkey)
                .setSubscribeKey(subkey)
                .setUuid(mUsername);

        PubNub pubnub = new PubNub(PNconfig);

        return pubnub;
    }
}
