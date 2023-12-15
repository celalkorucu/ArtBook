package com.celalkorucu.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.celalkorucu.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding ;
    ArrayList<Art> artList ;
ArtAdapter artAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        artList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artAdapter = new ArtAdapter(artList);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        binding.recyclerView.setAdapter(artAdapter);
        getData();
    }

    public void getData(){

        try {
            SQLiteDatabase database = openOrCreateDatabase("Arts",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT*FROM arts",null);
            int artNameIx = cursor.getColumnIndex("artName");
            int idIx = cursor.getColumnIndex("id");
            while(cursor.moveToNext()){
                String artName = cursor.getString(artNameIx);
                int id = cursor.getInt(idIx);
                Art art = new Art(id,artName);
                artList.add(art);
            }
            artAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu , menu);

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_art){

            System.out.println("Girdi");
            Intent intent = new Intent(MainActivity.this , ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }
}