package com.example.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import adapter.ContactAdapter;
import db.DbHelper;
import model.Contact;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactArrayList;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rview);
        contactAdapter = new ContactAdapter(this);
        dbHelper = new DbHelper(this);
        contactArrayList = dbHelper.getAll();
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

        contactAdapter.setContactArrayList(contactArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contactAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        contactArrayList = dbHelper.getAll();
        contactAdapter.setContactArrayList(contactArrayList);
        contactAdapter.notifyDataSetChanged();
    }



}