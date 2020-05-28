package com.example.twitter_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    
    ArrayList<String> users = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    
    ListView levi;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    
        MenuInflater muInflater = new MenuInflater(this);
        muInflater.inflate(R.menu.tweet_menu, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        if (item.getItemId() == R.id.tweetOption) {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
    
            final EditText tweetContent = new EditText(this);
            builder.setView(tweetContent);
            builder.setTitle("Send a Squawk");
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ParseObject object = new ParseObject("Squawk");
                    object.put("username", ParseUser.getCurrentUser().getUsername());
                    object.put("message", tweetContent.getText().toString());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "Squak Sent Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to send Squawk", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
        
                }
            });
            builder.show();
            
        } else if (item.getItemId() == R.id.logoutOption) {
    
            ParseUser.getCurrentUser().logOut();
            
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        
        setTitle("Following:");
        
        levi = findViewById(R.id.levi);
        levi.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    
        mQueryParseUsers();
    }
    
    private void mSetUpListView() {
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
        levi.setAdapter(arrayAdapter);
    
        if (ParseUser.getCurrentUser().get("isFollowing") == null) {
            List<String> list = new ArrayList<>();
            ParseUser.getCurrentUser().put("isFollowing", list);
            ParseUser.getCurrentUser().saveInBackground();
        }
        
//        ParseUser.getCurrentUser().getList("isFollowing").clear();
//        ParseUser.getCurrentUser().saveInBackground();
        
        for (String username : users) {
            if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
                levi.setItemChecked(users.indexOf(username), true);
            }
        }
    
        try {
            arrayAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        
        }
        
        // OnItemClick.. not OnClick
        levi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
                CheckedTextView box = (CheckedTextView) view;
                
                if (box.isChecked()) {
                    
                    mParseAddOrRemove("add", position);
                    
                } else {
                    
                    mParseAddOrRemove("remove", position);
                    
                }
            }
        });
    
    }
    
    public void mQueryParseUsers() {
        
        users.clear();
        
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            users.add(user.getUsername());
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
                mSetUpListView();
            }
        });
    }
    
    public void mParseAddOrRemove(String command, int position) {
        /** Parse Lists don't work well any more, we have to delete the list and rewrite it */
        /** TODO: VERY Inefficient Code since this is done each time the user clicks a list
         **       IDEALLY THIS SHOULD BE DONE ONCE ONLY, WHEN THE USER LOGS OUT OR CLOSES APP */
        /** onPause onStop onDestroy are all much better replacements */
        
        if (command.equals("add")) {
            ParseUser.getCurrentUser().getList("isFollowing").add(users.get(position));
            List newlist = ParseUser.getCurrentUser().getList("isFollowing");
            ParseUser.getCurrentUser().remove("isFollowing");
            ParseUser.getCurrentUser().put("isFollowing", newlist);
            ParseUser.getCurrentUser().saveInBackground();
        } else {
            ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));
            List newlist = ParseUser.getCurrentUser().getList("isFollowing");
            ParseUser.getCurrentUser().remove("isFollowing");
            ParseUser.getCurrentUser().put("isFollowing", newlist);
            ParseUser.getCurrentUser().saveInBackground();
        }
    }
    
    public void mGoToFeed(View view) {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }
}
