package com.example.twitter_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    
    ListView levi;
    List<Map<String, String>> tweetData = new ArrayList<Map<String, String>>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        
        setTitle(ParseUser.getCurrentUser().getUsername() + "'s Feed: ");
        levi = findViewById(R.id.tweetListView);
        
        mQueryFollowedUsers();
    }
    
    public void mQueryFollowedUsers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Squawk");
        
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null & objects.size() > 0) {
                    
                    for (ParseObject tweet : objects) {
                        Map<String,String> tweetInfo = new HashMap<String, String>();
                        
                        tweetInfo.put("content", tweet.getString("message"));
                        tweetInfo.put("username", tweet.getString("username"));
                        
                        tweetData.add(tweetInfo);
                    }
    
                    SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), tweetData, android.R.layout.simple_list_item_2, new String[]{"username", "content"}, new int[]{android.R.id.text1, android.R.id.text2});
                    levi.setAdapter(simp);
                }
            }
        });
    }
}
