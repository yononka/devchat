package com.lakshmi.devchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    static StringBuilder messages = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView channel_name = (TextView) findViewById(R.id.channelName_view);
        channel_name.setText("welcome to "+Constants.CHANNEL_NAME+" channel");
        final PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(Constants.TEST_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(Constants.TEST_PUBLISH_KEY);
        pnConfiguration.setUuid("DevFlower");

        final PubNub pubnub = new PubNub(pnConfiguration);

       pubnub.subscribe()
                .channels(Arrays.asList(Constants.CHANNEL_NAME))
                .withPresence()
                .execute();


        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                String status_msg = "PubNub status: " + status.getStatusCode();
                System.out.println(status_msg);
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                long digit_time = message.getTimetoken()/10000000;
                String time_msg = DateFormat.format("dd-MM-yyyy HH:mm:ss", digit_time).toString();

            if (message.getMessage().isJsonObject()) {

                    try {
                        String msg = pubnub.getMapper().elementToString(message.getMessage(), "text");
                        String uuid_msg =  pubnub.getMapper().elementToString(message.getMessage(), "user");
                        showMessage(msg, time_msg, uuid_msg);
                    } catch (Exception error) {
                        System.out.println("I got an exception in displaying history in this line: " + message.getMessage().toString());
                        String msg = message.getMessage().toString();
                        showMessage(msg, time_msg,"");
                    }

                }else{
                    String msg = message.getMessage().toString();
                    showMessage(msg, time_msg, "");
                }

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });


        pubnub.history()
            .channel(Constants.CHANNEL_NAME)
            .count(3)
            .includeTimetoken(true)
            .async(new PNCallback<PNHistoryResult>() {
                @Override
                public void onResponse(PNHistoryResult result, PNStatus status) {
                    System.out.println(result);
                    if (result != null) {
                        List<String> history_list = new ArrayList<String>();

                        for (PNHistoryItemResult item : result.getMessages()) {
                            long digit_time = item.getTimetoken()/10000000;
                            String time_history = DateFormat.format("dd-MM-yyyy HH:mm:ss", digit_time).toString();
                            System.out.println(item);
                            if (item.getEntry().isJsonObject()) {
                                try {
                                    String msg_history = pubnub.getMapper().elementToString(item.getEntry(), "text");
                                    String uuid_history = pubnub.getMapper().elementToString(item.getEntry(), "user");
                                    history_list.add(msg_history);
                                    history_list.add(time_history);
                                    history_list.add(uuid_history);
                                    showMessage(history_list);
                                   // showMessage(msg_history, time_history, uuid_history);
                                } catch (Exception error) {
                                    System.out.println("I got an exception in displaying history in this line: " + item.getEntry().toString());
                                    String msg_history = item.getEntry().toString();
                                    history_list.add(msg_history);
                                    history_list.add(time_history);
                                    history_list.add("");
                                    //showMessage(msg_history, time_history, "");
                                    showMessage(history_list);
                                }

                            }else{
                                String msg_history = item.getEntry().toString();
                                history_list.add(msg_history);
                                history_list.add(time_history);
                                history_list.add("");
                               // showMessage(msg_history, time_history, "");
                                showMessage(history_list);
                            }

                       //     history_list.add(history_entry);

                        }
                    } else {
                        System.out.print("history is empty");
                    }
                }
            });


        EditText msg_text = (EditText) findViewById(R.id.newMessage);
        Button send_btn = (Button) findViewById(R.id.bSend);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText new_msg = (EditText) findViewById(R.id.newMessage);
                String msg_value = new_msg.getText().toString();
                String msg_uuid = pnConfiguration.getUuid();
                JSONObject data = new JSONObject();

                try {
                    data.put("user", msg_uuid);
                    data.put("text", msg_value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pubnub.publish().message(data)
                        .channel(Constants.CHANNEL_NAME)
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
    }

    private void showMessage(ArrayList<String>list_items) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String message_clean = message.replace("[]","");
                messages.append("["+time_message+"] ");
                messages.append(uuid_message+": ");
                messages.append(message_clean);
                messages.append("\n\n");

                TextView message_list = (TextView) findViewById(R.id.msg_list_view);
                message_list.setText(messages.toString());
                ScrollView scroll_msg = (ScrollView) findViewById(R.id.scroll_msg_view);
                scroll_msg.fullScroll(View.FOCUS_DOWN);
            }
        });

    }


}