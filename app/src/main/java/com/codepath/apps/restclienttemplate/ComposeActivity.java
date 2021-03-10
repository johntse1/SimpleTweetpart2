package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG="";
    public static final int MAX_TWEET_LENGTH=280;

    EditText etCompose;
    Button btnTweet;
    TwitterClient client;
    TextInputLayout inputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet= findViewById(R.id.btnTweet);
        inputLayout= findViewById(R.id.inputLayout);

        client = TwitterApp.getRestClient(this);

        //Listener for button makes API call for twitter
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent=  etCompose.getText().toString();
                //API call
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length()>MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Tweet too long", Toast.LENGTH_LONG).show();
                    btnTweet.setEnabled(false);
                    if(tweetContent.length()<=MAX_TWEET_LENGTH){
                        btnTweet.setEnabled(true);
                    }
                    return;
                }
                //Toast.makeText(ComposeActivity.this,tweetContent,Toast.LENGTH_LONG).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.e(TAG,"onSuccess to publish tweet");
                        try {
                            Tweet tweet =Tweet.fromJson(json.jsonObject);
                            Log.i(TAG,"published tweet says "+tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to publish tweet",throwable);
                    }
                });
            }
        });
    }
}