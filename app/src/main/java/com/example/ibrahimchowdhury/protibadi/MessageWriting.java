package com.example.ibrahimchowdhury.protibadi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Ibrahim Chowdhury on 3/13/2017.
 */


public class MessageWriting extends AppCompatActivity {

    EditText message;
    Button saveBtn;
    Button cancelBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writing_message);
        message = (EditText)findViewById(R.id.eMessage);
        saveBtn = (Button) findViewById(R.id.mSave);
        cancelBtn = (Button)findViewById(R.id.mCancel);
    }
    public void saveMessage(View view){

    }
    public void cancelActivity(View view){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
}
