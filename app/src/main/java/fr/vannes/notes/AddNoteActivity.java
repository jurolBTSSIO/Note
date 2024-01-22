package fr.vannes.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextContent;
    private ImageButton imageButtonSave;
    private DatabaseReference database;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Je lie l'activite a son fichier XML
        editTextTitle = findViewById(R.id.title);
        editTextContent = findViewById(R.id.content);
        imageButtonSave = findViewById(R.id.saveButton);

        database = FirebaseDatabase.getInstance().getReference("notes");

        // Je cree un listener pour le bouton de sauvegarde
        imageButtonSave.setOnClickListener(v ->
                {
                    saveNote();
                }
        );
    }

    /**
     * Cette methode permet de sauvegarder une note
     */
    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        // Je verifie que les donnees sont valides
        if (title.isEmpty() || content.isEmpty()) {
            editTextTitle.setError("Title and content are required");
            return;
        }

        // Je genere une cle unique pour la note
        userId = database.push().getKey();

        // Je cree une note
        Note note = new Note(title, content);

        // J'ajoute la note dans la base de donnees
        database.child(userId).setValue(note);

        Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Cette methode permet d'ajouter un listener sur la note
     */
    private void addNoteChangeListener() {
        database.child(userId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Note note = snapshot.getValue(Note.class);
                if (note == null) {
                    Toast.makeText(getApplicationContext(), "Note not found", Toast.LENGTH_LONG).show();
                    return;
                }
                editTextTitle.setText(note.getTitle());
                editTextContent.setText(note.getContent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Note not found", Toast.LENGTH_LONG).show();
            }
        });
    }
}