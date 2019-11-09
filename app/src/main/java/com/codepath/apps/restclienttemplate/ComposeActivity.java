package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    RelativeLayout composeLayout;
    EditText edtTweet;
    Button btnTweet;

    TwitterClient client = TwitterApp.getRestClient(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        composeLayout = findViewById(R.id.composeLayout);
        edtTweet = findViewById(R.id.edtTweet);
        btnTweet = findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tweetContent = edtTweet.getText().toString();
                if (tweetContent.isEmpty()) {
                    Snackbar.make(composeLayout, "Could not add empty tweet!", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(ComposeActivity.this, "Could not add empty tweet!", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Snackbar.make(composeLayout, "Too many characters!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(composeLayout, tweetContent, Snackbar.LENGTH_LONG).show();

                // make API call to Twitter to publish tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "on publish success");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet" + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "json could not be parsed!");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "on publish failure " + throwable);
                    }
                });
            }
        });


    }
}
