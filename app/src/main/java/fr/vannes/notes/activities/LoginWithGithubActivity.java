package fr.vannes.notes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;

import fr.vannes.notes.R;

public class LoginWithGithubActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private Button loginBtn;
    private EditText githubEdit;
    private FirebaseAuth auth;
    private OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_github);

        loginBtn = findViewById(R.id.github_login_btn);
        githubEdit = findViewById(R.id.githubId);

        auth = FirebaseAuth.getInstance();

        ArrayList<String> scopes = new ArrayList<>();
        scopes.add("user:email");
        provider.setScopes(scopes);

        loginBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(githubEdit.getText().toString())) {
                Toast.makeText(this, "Enter your github email", Toast.LENGTH_LONG).show();
            } else {
                signInWithGithubProvider();
            }
        });
    }

    private void signInWithGithubProvider() {
        Task<AuthResult> pendingResultTask = auth.getPendingAuthResult();

        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener(authResult -> {
                Toast.makeText(this, "User exists", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
                Log.e("Error1: ", e.toString());
            });
        } else {
            auth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(authResult -> {
                        firebaseUser = auth.getCurrentUser();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("githubUserName", firebaseUser.getDisplayName());
                        startActivity(intent);
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
                        Log.e("Error2: ", e.toString());
                    });
        }
    }
}