package com.appstore.streaming_store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appstore.streaming_store.database.AdminSQLitOpenHelper;

public class MainActivity extends AppCompatActivity {
    private AdminSQLitOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new AdminSQLitOpenHelper(this, "streaming_db", null, 1);



    }

    public void logIn(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void sigUp(View v){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

}