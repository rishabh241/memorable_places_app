package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView memoList;
    static ArrayList<String> arrayList = new ArrayList<String>();
    static ArrayList<LatLng> location = new ArrayList<LatLng>();
    static ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memoList= findViewById(R.id.places);
        arrayList.add("Add your place");
        location.add(new LatLng(0,0));
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        memoList.setAdapter(adapter);
        memoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MemoMap.class);
                intent.putExtra("Place",i);
                startActivity(intent);
            }
        });
    }
}