package com.lakshmi.devchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

public class TextChatActivity extends MainActivity {

    StringBuilder messages = new StringBuilder();

    private String mUsername;
    public PubNub mPubNub;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
     //   menu.findItem(R.id.logout).setVisible(true);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent chat = getIntent();
        final String pubkey = chat.getStringExtra("pub_key");
        final String subkey= chat.getStringExtra("sub_key");
        final String chatname= chat.getStringExtra("chat_name");

        this.mUsername = chat.getStringExtra("user_name");

        final PubNub pubnub = InitPubNub.pubnubDeclaration(pubkey, subkey, mUsername);
        mPubNub = pubnub;

        TextView channel_name = (TextView) findViewById(R.id.channelName_view);
        channel_name.setText("welcome to " + chatname + " channel");

        mPubNub.subscribe()
                .channels(Arrays.asList(chatname))
                .withPresence()
                .execute();

  //      long sub_time = pubnub.getTimestamp();

        mPubNub.history()
                .channel(chatname)
                .count(100)
                .includeTimetoken(true)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        System.out.println(result);
                        if (result != null) {
                           for (PNHistoryItemResult item : result.getMessages()) {
                                if (item.getEntry().isJsonObject()) {
                                    try {
                                        String msg_history = mPubNub.getMapper().elementToString(item.getEntry(), "text");
                                        String uuid_history = mPubNub.getMapper().elementToString(item.getEntry(), "username");
                                        showMessage(msg_history, uuid_history);
                                    } catch (Exception error) {
                                        System.out.println("I got an exception in displaying history in this line: " + item.getEntry().toString());
                                        String msg_history = item.getEntry().toString();
                                        showMessage(msg_history, "<anonymous>");

                                    }

                                }else{
                                    String msg_history = item.getEntry().toString();
                                    showMessage(msg_history, "<anonymous>");
                                }

                            }


                        } else {
                            System.out.print("history is empty");
                        }
                    }
                });


        Button send_btn = (Button) findViewById(R.id.bSend);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText new_msg = (EditText) findViewById(R.id.newMessage);
                String msg_value = new_msg.getText().toString();
                String msg_uuid = mUsername;

                JsonObject data = new JsonObject();

                try {
                    data.addProperty("username", msg_uuid);
                    data.addProperty("text", msg_value);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mPubNub.publish().message(data)
                        .channel(chatname)
                        .async(new PNCallback<PNPublishResult>() {
                            @Override
                            public void onResponse(PNPublishResult result, PNStatus status) {
                                if (status.isError()) {
                                    System.out.println("error happened while publishing: " + status.toString());
                                }else{
                                    new_msg.setText("");
                                }
                            }
                        });
            }

        }
        );


        mPubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                String status_msg = "PubNub status: " + status.getStatusCode();
                System.out.println(status_msg);
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
            //      long msg_time = message.getTimetoken()/10000000;
               //   String msg_time = DateFormat.format("dd-MM-yyyy HH:mm:ss", digit_time).toString();

            if (message.getMessage().isJsonObject()) {

                    try {
                        String msg = pubnub.getMapper().elementToString(message.getMessage(), "text");
                        String uuid_msg =  pubnub.getMapper().elementToString(message.getMessage(), "username");
                        showMessage(msg, uuid_msg);
                    } catch (Exception error) {
                        System.out.println("I got an exception in displaying history in this line: " + message.getMessage().toString());
                        String msg = message.getMessage().toString();
                        showMessage(msg, "<anonymous>");
                    }

                }else{
                    String msg = message.getMessage().toString();
                    showMessage(msg, "<anonymous>");
                }

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });


        Button backtolist_btn = (Button) findViewById(R.id.bBackToList);

        backtolist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatslist = new Intent(TextChatActivity.this, MainActivity.class);
                finish();
                startActivity(chatslist);
            }
        }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                mPubNub.unsubscribeAll();
                finish();
                System.exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void showMessage(final String message,  final String uuid_message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


             //   String message_clean = message.replace("[]","");
            //    messages.append("["+time_message+"] ");
                messages.append(uuid_message+": ");
                messages.append(message);
                messages.append("\n\n");

                TextView message_list = (TextView) findViewById(R.id.msg_list_view);
                message_list.setText(messages.toString());
                ScrollView scroll_msg = (ScrollView) findViewById(R.id.scroll_msg_view);
                scroll_msg.fullScroll(View.FOCUS_DOWN);

            }
        });

    }




}
