package fr.vannes.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fr.vannes.notes.R;
import fr.vannes.notes.utilities.Util;

public class CreateAccountActivity extends AppCompatActivity {
    // Je declare les variables
    EditText editTextUsername, editTextPassword, editTextConfirmPassword;
    Button buttonCreateAccount;
    ProgressBar progressBar;
    TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Je lie l'activite a son fichier XML
        setContentView(R.layout.activity_create_account);
        editTextUsername = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.passwordConfirm);
        buttonCreateAccount = findViewById(R.id.createAccountButton);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.sign);

        // Je cree un listener pour le bouton de creation de compte
        buttonCreateAccount.setOnClickListener(v -> createAccount());

        // Je cree un listener pour le texte de connexion
        textViewLogin.setOnClickListener(v -> finish());
    }

    /**
     * Cette methode permet de creer un compte
     */
    void createAccount() {
        // Je recupere les donnees entrees par l'utilisateur
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Je cree un booleen pour verifier si les donnees sont valides
        boolean valid = isValid(username, password, confirmPassword);

        // Si il n'est pas valide, je ne fais rien
        if(!valid) {
            return;
        }

        // Je cree un compte dans Firebase
        createAccountInFirebase(username, password);
    }

    /**
     * Cette methode permet de creer un compte dans Firebase
     * @param username
     * @param password
     */
    void createAccountInFirebase(String username, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Je change la visibilite des elements en fonction de l'etat de la creation du compte
                        changeInProgress(false);

                        // Si la tache est reussie, je lance l'activite principale
                        if (task.isSuccessful()) {
                            // Je notifie l'utilisateur que le compte a ete cree
                            Util.showToast(CreateAccountActivity.this, "Account created, Check your email to complete");
                            // J'envoie un email de verification
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            // Je ferme l'activite courante
                            finish();
                        } else {
                            // Je notifie l'utilisateur que le compte n'a pas ete cree
                            Util.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    /**
     * Cette methode permet de changer la visibilite des elements en fonction de l'etat de la creation du compte
     * @param inProgress
     */
    void changeInProgress(boolean inProgress) {

        // Je change la visibilite des elements en fonction de l'etat de la creation du compte
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            buttonCreateAccount.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonCreateAccount.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Cette methode permet de verifier si les donnees entrees par l'utilisateur sont valides
     * @param username
     * @param password
     * @param confirmPassword
     * @return
     */
    boolean isValid (String username, String password, String confirmPassword) {

        // Je verifie si les donnees sont valides
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            editTextUsername.setError("Invalid Email");
            editTextUsername.setFocusable(true);
            return false;
            // Je verifie si le mot de passe est valide
        } else if (password.length() < 6) {
            editTextPassword.setError("Password length at least 6 characters");
            editTextPassword.setFocusable(true);
            return false;
            // Je verifie si le mot de passe de confimation est valide
        } else if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Password doesn't match");
            editTextConfirmPassword.setFocusable(true);
            return false;
        }
        return true;
    }
}