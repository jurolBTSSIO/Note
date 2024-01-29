package fr.vannes.notes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import fr.vannes.notes.entities.Note;
import fr.vannes.notes.adapters.NoteAdapter;
import fr.vannes.notes.R;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addNoteButton;
    private RecyclerView recyclerView;
    private ImageButton menuButton;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Je lie l'activite a son fichier XML
        addNoteButton = findViewById(R.id.addNoteButton);
        recyclerView = findViewById(R.id.recyclerView);
        menuButton = findViewById(R.id.menuButton);

        // J'ajoute un listener sur le bouton de menu
        menuButton.setOnClickListener(v -> showMenu());
        // J'ajoute un listener sur le bouton d'ajout de note
        addNoteButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        // Set up du RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Set up du FirebaseRecyclerAdapter
        Query query = FirebaseDatabase.getInstance().getReference("notes");
        FirebaseRecyclerOptions<Note> options = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.startListening();
    }

    /**
     * Cette methode permet d'afficher le menu
     */
    private void showMenu() {
        Log.d("MainActivity", "showMenu() called");

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuButton);
        popupMenu.getMenu().add("Log out");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            Log.d("MainActivity", "Menu item clicked: " + item.getTitle());

            // Je vérifie si l'utilisateur a cliqué sur le bouton de déconnexion
            if (item.getTitle().equals("Log out")) {
                FirebaseAuth.getInstance().signOut();
                Log.d("MainActivity", "User signed out");

                // Ajouter ces lignes pour démarrer LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

}