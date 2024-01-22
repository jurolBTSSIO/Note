package fr.vannes.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextContent;
    private TextView textViewTitle;
    private ImageButton imageButtonSave;
    private DatabaseReference database;
    boolean isEditMode = false;

    private String userId, title, content, date, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Je lie l'activite a son fichier XML
        textViewTitle = findViewById(R.id.page_title);
        editTextTitle = findViewById(R.id.title);
        editTextContent = findViewById(R.id.content);
        imageButtonSave = findViewById(R.id.saveButton);

        // Je recupere les donnees de l'intent
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null || !docId.isEmpty()) {
            isEditMode = true;
        }

        //Je les affiche dans les champs
        editTextTitle.setText(title);
        editTextContent.setText(content);
        if (isEditMode) {
            textViewTitle.setText("Edit note");
        } else {
            textViewTitle.setText("Add note");
        }


        database = FirebaseDatabase.getInstance().getReference("notes");

        // Je cree un listener pour le bouton de sauvegarde
        imageButtonSave.setOnClickListener(v ->
                {
                    saveNote();
                    addNoteChangeListener();
                }
        );
    }

    /**
     * Cette methode permet de sauvegarder une note
     */
    private void saveNote() {

        if (isEditMode) {
            userId = docId;
        } else {
            // Je genere une cle unique pour la note
            userId = database.push().getKey();
        }

        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        // Je verifie que les donnees sont valides
        if (title.isEmpty() || content.isEmpty()) {
            editTextTitle.setError("Title and content are required");
            return;
        }

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