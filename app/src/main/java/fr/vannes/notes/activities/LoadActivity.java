package fr.vannes.notes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.vannes.notes.R;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        // Je cree un Handler pour lancer une nouvelle activite apres 3 secondes
        // Il existe deux utilisations principales d'un gestionnaire : (1) planifier l'execution de messages
        // et d'executables à un moment donne dans le futur ;
        // et (2) pour mettre en file d'attente une action à effectuer sur un thread different du votre.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    // Si l'utilisateur n'est pas connecte, je lance l'activite de connexion
                    startActivity(new Intent(LoadActivity.this, LoginActivity.class));
                } else {
                    // Sinon, je lance l'activite principale
                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                }
               finish();
            }
        }, 1000); // 1000 = 1 seconde
    }
}