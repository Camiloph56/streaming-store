package com.appstore.streaming_store;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appstore.streaming_store.database.AdminSQLitOpenHelper;
import com.appstore.streaming_store.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText password, cedula;
    private AdminSQLitOpenHelper dbHelper;
    private CheckBox recordinPassword;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new AdminSQLitOpenHelper(this, "streaming_db", null, 1);

        password = findViewById(R.id.password);
        cedula = findViewById(R.id.document);
        recordinPassword = findViewById(R.id.recordingPassword);
        prefs = getSharedPreferences("user_session", MODE_PRIVATE);

        checkExistingSession();

    }

    private void checkExistingSession() {
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            // Verificar si la sesión sigue activa en la BD
            if (dbHelper.verifyActiveSession(userId)) {
                home(); // Ir directamente al home
            }
        }

    }

    public void sigIn(View v){
        //dbHelper.debugShowAllUsers();

        //dbHelper.verifyActiveSession();
        String document = cedula.getText().toString().trim();
        String contrasena = password.getText().toString().trim();
        boolean remember = recordinPassword.isChecked();

        if(document.isEmpty()){
            Toast.makeText(this, "¡Ingrese su documento!", Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena.isEmpty()){
            Toast.makeText(this, "¡Ingrese su contrasena!", Toast.LENGTH_LONG).show();
            return;
        }

        User user = dbHelper.loginUser(document, contrasena);


        if(user != null){
            saveSesion(user, remember);
            Toast.makeText(this, "Bienvenido "  + user.getName(), Toast.LENGTH_LONG).show();
            home();
        } else {
            Toast.makeText(this, "¡Cédula o contraseña incorrectos!", Toast.LENGTH_LONG).show();

        }
    }

    private void saveSesion(User user, boolean rememberSession){
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("user_id", user.getId());
        editor.putString("user_document", user.getDocument());
        editor.putString("user_name", user.getName());
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        if (rememberSession) {
            String token = java.util.UUID.randomUUID().toString();
            dbHelper.saveToken(user.getId(), token);
        }
    }


    public void home(){
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
}