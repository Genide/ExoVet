package com.example.daniel.myapplication.dummy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.myapplication.MapsActivity;
import com.example.daniel.myapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AnimalList extends ActionBarActivity {

    Animal[] animals = new Animal[]{
            new Animal("Bird"),
            new Animal("Cat"),
            new Animal("Dog"),
            new Animal("Ferret"),
            new Animal("Fish"),
            new Animal("Guinea Pig"),
            new Animal("Horse"),
            new Animal("Lizard"),
            new Animal("Rabbit"),
            new Animal("Rodents")
    };

    int[] pictures = new int[]{
            R.drawable.bird,
            R.drawable.cat_paw_print,
            R.drawable.dog_paw_print,
            R.drawable.ferret_paw_print,
            R.drawable.fish,
            R.drawable.guinea_pig_paw_print,
            R.drawable.horseshoe,
            R.drawable.trex_foot_print,
            R.drawable.rabbit,
            R.drawable.rat
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);

        // Each row in the list stores the animal's name and a picture of their paw print
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i = 0; i < animals.length; i++)
        {
            HashMap<String, String> listing = new HashMap<String, String>();
            listing.put("name", animals[i].getName());
            listing.put("paw", Integer.toString(pictures[i]));
            aList.add(listing);
        }

        String[] from = {"name", "paw"};
        int[] to = {R.id.name, R.id.paw};


        ListView listView = (ListView)findViewById (R.id.listView1);

        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

        listView.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayoutParent = (LinearLayout) view;
                LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent.getChildAt(1);
                TextView species = (TextView) linearLayoutChild.getChildAt(0);

                String item = species.getText().toString();
                Toast.makeText(getBaseContext(), "You selected the " + item, Toast.LENGTH_LONG).show();
                Intent getMapIntent = new Intent(AnimalList.this, MapsActivity.class);
                getMapIntent.putExtra("animal", item);
                startActivityForResult(getMapIntent,0);
            }
        };

        listView.setOnItemClickListener(itemClickListener);
           /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //String animalPicked = String.valueOf(adapterView.getItemAtPosition(position));
                String animal = String.valueOf(adapterView.getItemAtPosition(position));
            String animal = ((TextView)view).getText().toString();
            Toast.makeText(getBaseContext(), "You selected the " + animal, Toast.LENGTH_LONG).show();
            Intent getMapIntent = new Intent(AnimalList.this, MapsActivity.class);

            // To send data use putExtra with a String name followed by its value
            getMapIntent.putExtra("animal", animal);
            startActivityForResult(getMapIntent,0);
            }
        });
        */
    }
}
