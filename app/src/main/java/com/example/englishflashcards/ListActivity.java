package com.example.englishflashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import data.FlashCard;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        getSupportActionBar().setTitle("VIEWING All FLASHCARDS");
        getSupportActionBar().setElevation(8);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_list);
        Intent intent = super.getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.MainActivitySPCardsSet));
        ArrayList<FlashCard> flashCardArrayList = (ArrayList<FlashCard>) bundle.getSerializable(getString(R.string.MainActivitySPCardsSet));
        RecyclerView recyclerView = findViewById(R.id.flashCardsRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, flashCardArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}