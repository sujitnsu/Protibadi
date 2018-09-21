package com.example.ibrahimchowdhury.protibadi;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ih Chowdhury on 01-04-17.
 */

public class SaveNumber extends AppCompatActivity {

    SQLiteDatabase mydatabase;

    private EditText NNumber, NName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberbox);

        NNumber = (EditText) findViewById(R.id.newNumber);
        NName = (EditText) findViewById(R.id.newName);
        mydatabase = openOrCreateDatabase("protibadi",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Contact(Name VARCHAR,Number VARCHAR);");

    }
    public void add(View view){

        String name, number;
        number = NNumber.getText().toString();
        name = NName.getText().toString();
        mydatabase.execSQL("INSERT INTO Contact VALUES('"+name+"','"+number+"');");
        Toast.makeText(this,"New Data Inserted",Toast.LENGTH_SHORT).show();
        NNumber.setText(null);
        NName.setText(null);


    }

    public void cancel(View view){
        Intent intent = new Intent(SaveNumber.this,MainActivity.class);
        startActivity(intent);
        mydatabase.close();
        SaveNumber.this.finish();

    }
}
