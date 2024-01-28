package fr.vannes.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;
import java.util.List;

import fr.vannes.notes.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonLoginGithub;
    ProgressBar progressBar;
    TextView textViewCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Je lie l'activite a son fichier XML
        editTextUsername = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.loginButton);
        buttonLoginGithub = findViewById(R.id.githubLoginButton);
        progressBar = findViewById(R.id.progressBar);
        textViewCreateAccount = findViewById(R.id.createAccountButton);

        // Je recupere l'instance de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Je cree un listener pour le bouton de connexion
        buttonLogin.setOnClickListener(v -> loginWithPassword());
        buttonLoginGithub.setOnClickListener(v -> loginWithGithub(v));

        // Je cree un listener pour le bouton de creation de compte
        textViewCreateAccount.setOnClickListener(v
                -> startActivity(new Intent(LoginActivity.this,
                CreateAccountActivity.class)));
    }

    private void loginWithGithub(View v) {
        startActivity(new Intent(LoginActivity.this, LoginWithGithubActivity.class));
    }

    /**
     * Cette methode permet de se connecter
     */
    private void loginWithPassword() {
        // Je recupere les donnees entrees par l'utilisateur
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        // Je cree un booleen pour verifier si les donnees sont valides
        boolean valid = isValid(username, password);

        // Si il n'est pas valide, je ne fais rien
        if(!valid) {
            return;
        }

        // Je change la visibilite des elements
        changeInProgress(true);
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    changeInProgress(false);

                    if (task.isSuccessful()) {
                        // Je verifie si l'email est verifie
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            // Je redirige l'utilisateur vers l'activite principale
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Je demande a l'utilisateur de verifier son email
                            Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                        }
                    }  else {
                        // Je recupere le message d'erreur
                        String message = task.getException().getMessage();
                        // J'affiche le message d'erreur
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Cette methode permet de verifier si les donnees sont valides
     * @param inProgress
     */
    void changeInProgress(boolean inProgress) {

        // Je change la visibilite des elements en fonction de l'etat de la creation du compte
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.VISIBLE);
        }
    }
    boolean isValid (String username, String password) {

        // Je verifie si les donnees sont valides
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            editTextUsername.setError("Invalid Email");
            editTextUsername.setFocusable(true);
            return false;
            // Je verifie si le mot de passe est valide
        } if (password.length() < 6) {
            editTextPassword.setError("Password length at least 6 characters");
            editTextPassword.setFocusable(true);
            return false;
            // Je verifie si le mot de passe de confimation est valide
        }
        return true;
    }
}