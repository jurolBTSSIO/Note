package fr.vannes.notes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.vannes.notes.R;
import fr.vannes.notes.entities.Note;
import fr.vannes.notes.openAI.OpenAIRequest;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent, editKeywords;
    private TextView textViewTitle;
    private ImageButton imageButtonSave, imageButtonDelete, imageButtonOpenAI;
    private DatabaseReference database;
    private boolean isEditMode = false;
    private String userId, title, content, docId;

    /**
     * Cette methode est appelee lors de la creation de l'activite
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Je lie l'activite a son fichier XML
        textViewTitle = findViewById(R.id.page_title);
        editTextTitle = findViewById(R.id.title);
        editTextContent = findViewById(R.id.content);
        editKeywords = findViewById(R.id.keywords);
        imageButtonSave = findViewById(R.id.saveButton);
        imageButtonDelete = findViewById(R.id.deleteButton);
        imageButtonOpenAI = findViewById(R.id.sendButton);

        // Je recupere les donnees de l'intent
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        // Je verifie si je suis en mode edition
        if (docId != null) {
            isEditMode = true;
        }

        //Je les affiche dans les champs
        editTextTitle.setText(title);
        editTextContent.setText(content);

        // Si je suis en mode edition, j'affiche le bouton de suppression
        if (isEditMode) {
            textViewTitle.setText("Edit note");
            imageButtonDelete.setVisibility(View.VISIBLE);
        } else {
            textViewTitle.setText("Add note");
        }

        // Je lie l'activite a la base de donnees
        database = FirebaseDatabase.getInstance().getReference("notes");

        // Je cree un listener pour le bouton de sauvegarde
        imageButtonSave.setOnClickListener(v -> saveNote());

        // Je cree un listener pour le bouton de suppression
        imageButtonDelete.setOnClickListener(v -> deleteNote());

        // Je cree un listener pour le bouton d'envoi
        imageButtonOpenAI.setOnClickListener(v -> {
            String keywords = editKeywords.getText().toString();
            //sendRequest(keywords);
        });
    }

    /**
     * Cette methode permet d'envoyer une requete a l'API OpenAI
     * @param keywords
     */
    private void sendRequest(String keywords) {
        OpenAIRequest.performAsyncChatRequest(keywords, response -> {
            if (response != null && !response.isEmpty()) {
                // Afficher la réponse dans une notification Toast
                Toast.makeText(getApplicationContext(), "OpenAI Response: " + response, Toast.LENGTH_SHORT).show();
                editTextContent.setText(response);
            } else {
                Toast.makeText(getApplicationContext(), "Empty or null response from OpenAI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cette methode permet de supprimer une note
     */
    private void deleteNote() {
        database.child(docId).removeValue();
        Toast.makeText(getApplicationContext(), "Note deleted", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Cette methode permet de sauvegarder une note
     */
    private void saveNote() {

        // Je recupere l'id de la note
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
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setDate(new SimpleDateFormat("EEEE, dd-MMMM-yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        // J'ajoute la note dans la base de donnees
        database.child(userId).setValue(note);

        Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}