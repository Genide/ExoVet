package com.example.daniel.myapplication.dummy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.daniel.myapplication.MapsActivity;
import com.example.daniel.myapplication.R;

public class AnimalActivity extends ActionBarActivity {
    private String[] animals = {"Dog","Cat","Fish","Bird","Reptile","Rabbit","Guinea Pig","Rodent","Ferret","Horse"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);

        ListAdapter theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, animals);
        ListView theListView = (ListView)findViewById(R.id.listView);
        theListView.setAdapter(theAdapter);
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String animalPicked = String.valueOf(adapterView.getItemAtPosition(position));
                String message = "You selected the " + String.valueOf(adapterView.getItemAtPosition(position));
                Toast.makeText(AnimalActivity.this, message, Toast.LENGTH_SHORT).show();
                Intent getMapIntent = new Intent(AnimalActivity.this, MapsActivity.class);

                // To send data use putExtra with a String name followed by its value
                getMapIntent.putExtra("animal", animalPicked);
                startActivityForResult(getMapIntent,0);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_animal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
