package com.lakshmi.devchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;


public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "AppPrefsFile";

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText nick = (EditText) findViewById(R.id.username_view);

        SharedPreferences app_prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String pref_username = app_prefs.getString("username", null);
        if (pref_username != null) {
            nick.setText(pref_username);
        }


    }

    public void onRadioButtonClicked(View view) {
        String pub_demo = "pub-c-910212e4-6810-4b55-ac8b-cda9c65fb6a8";
        String sub_demo = "sub-c-87929408-98b3-11e7-bec3-c65ebd354f7d";
        String demo_name = "testchannel";
        String pub_cc2 = "pub-c-6b25a820-7f65-4fcd-a213-6e1545ce389f";
        String sub_cc2 = "sub-c-5598fc3c-afe0-11e7-a852-92b7c98bd364";
        String cc2_name = "chacha2";

        final EditText nick = (EditText) findViewById(R.id.username_view);
        final String user_name = nick.getText().toString();

        SharedPreferences.Editor app_prefs = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        app_prefs.putString("username", user_name);
        app_prefs.commit();

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.demo_chat:
                if (checked)
                    openTextChatActivity(pub_demo, sub_demo, demo_name, user_name);
                    break;
            case R.id.chacha2:
                if (checked)
                    openTextChatActivity(pub_cc2, sub_cc2, cc2_name, user_name);
                    break;
        }
    }

    public void openTextChatActivity(String pub_k, String sub_k, String chatname, String username){
        Intent chat = new Intent(MainActivity.this, TextChatActivity.class);
        chat.putExtra("pub_key", pub_k);
        chat.putExtra("sub_key", sub_k);
        chat.putExtra("chat_name", chatname);
        chat.putExtra("user_name", username);
        finish();
        startActivity(chat);
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
                finish();
                System.exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}