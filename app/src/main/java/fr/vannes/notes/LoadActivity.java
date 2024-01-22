package fr.vannes.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                // Si l'utilisateur est connecte, je lance l'activite principale
                if (currentUser == null) {
                    Intent i = new Intent(LoadActivity.this, LoginActivity.class);
                    startActivity(i);
                    // Sinon, je lance l'activite de creation de compte
                } else {
                    Intent i = new Intent(LoadActivity.this, MainActivity.class);
                    startActivity(i);
                }
                finish(); // Je ferme l'activite courante
            }
        }, 1000); // 1000 = 1 seconde
    }
}