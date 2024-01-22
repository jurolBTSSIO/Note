package fr.vannes.notes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Je lie l'activite a son fichier XML
        addNoteButton = findViewById(R.id.addNoteButton);

        addNoteButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));
    }
}