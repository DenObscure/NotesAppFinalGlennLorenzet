package com.example.notesapp_final_glennlorenzet;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditNoteActivity extends AppCompatActivity {

    EditText title;
    EditText content;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent i = getIntent();

        title = findViewById(R.id.Edittitletext);
        title.setText(i.getStringExtra("title"));

        content = findViewById(R.id.Editcontenttext);
        content.setText(i.getStringExtra("content"));

        id = i.getStringExtra("id");
    }

    public void saveNote(View view)
    {
        title = findViewById(R.id.Edittitletext);
        content = findViewById(R.id.Editcontenttext);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("newTitle", title.getText().toString());
        returnIntent.putExtra("newContent", content.getText().toString());
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("action", "edit");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void deleteNote(View view)
    {
        title = findViewById(R.id.Edittitletext);
        content = findViewById(R.id.Editcontenttext);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "delete");
        returnIntent.putExtra("id", id);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
