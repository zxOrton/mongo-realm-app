package com.sendo.mongo_realm_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.bson.Document;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MainActivity extends AppCompatActivity {

    String APP_ID = "app-1-ooytg";

    EditText etName;
    EditText etHobby;
    Button btnSubmit;

    MongoDatabase mongoDatabase;
    MongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.et_name);
        etHobby = (EditText) findViewById(R.id.et_hobby);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        Realm.init(this);
        App app = new App(new AppConfiguration.Builder(APP_ID).build());

        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    Log.v("user", "success");
                }
                else {
                    Log.v("user", "failed");
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = app.currentUser();
                mongoClient = user.getMongoClient("mongodb-atlas");
                mongoDatabase = mongoClient.getDatabase("test");
                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("users");

                mongoCollection.insertOne(new Document("userId", user.getId()).append("name", etName.getText().toString()).append("hobby", etHobby.getText().toString())).getAsync(result -> {
                    if (result.isSuccess()) {
                        Log.v("data", "success");
                    } else {
                        Log.v("data", result.getError().toString());
                    }
                });
            }
        });
    }

}