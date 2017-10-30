package com.lakshmi.devchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        setContentView(R.layout.login_page);

    }

    public void onNickButtonClicked (View view) {
         final EditText nick = (EditText) findViewById(R.id.username_view);
         String nick_value = nick.getText().toString();
         Intent chat = new Intent(MainActivity.this, TextChatActivity.class);
         chat.putExtra("nickname", nick_value);
         finish();
         startActivity(chat);
     }


 /*   public void onRadioButtonClicked(View view) {
        String pub_demo = "pub-c-910212e4-6810-4b55-ac8b-cda9c65fb6a8";
        String sub_demo = "sub-c-87929408-98b3-11e7-bec3-c65ebd354f7d";
        String demo_name = "testchannel";
        String pub_cc2 = "pub-c-6b25a820-7f65-4fcd-a213-6e1545ce389f";
        String sub_cc2 = "sub-c-5598fc3c-afe0-11e7-a852-92b7c98bd364";
        String cc2_name = "chacha2";

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.demo_chat:
                if (checked)
                    openTextChatActivity(pub_demo, sub_demo, demo_name);
                    break;
            case R.id.chacha2:
                if (checked)
                    openTextChatActivity(pub_cc2, sub_cc2, cc2_name);
                    break;
        }
    }

    public void openTextChatActivity(String pub_k, String sub_k, String chatname){
        Intent chat = new Intent(MainActivity.this, TextChatActivity.class);
        chat.putExtra("pub_key", pub_k);
        chat.putExtra("sub_key", sub_k);
        chat.putExtra("chat_name", chatname);
        finish();
        startActivity(chat);
    }

*/
}