package com.example.notesapp_final_glennlorenzet;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
    }

    public void saveNote(View view)
    {
        EditText title = findViewById(R.id.titletext);
        EditText content = findViewById(R.id.contenttext);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("title", title.getText().toString());
        returnIntent.putExtra("content", content.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
