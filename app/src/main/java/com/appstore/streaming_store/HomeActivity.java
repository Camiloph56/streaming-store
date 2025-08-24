package com.appstore.streaming_store;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appstore.streaming_store.database.AdminSQLitOpenHelper;
import com.appstore.streaming_store.models.User;

public class HomeActivity extends AppCompatActivity {

    private TextView userName, saldo;

    private AdminSQLitOpenHelper dbHelper;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        userName = findViewById(R.id.perfilName);
        saldo = findViewById(R.id.saldo);

         dbHelper = new AdminSQLitOpenHelper(this, "streaming_db", null, 1);
        prefs = getSharedPreferences("user_session", MODE_PRIVATE);

        loadUser();

    }


    private void loadUser() {
        // Obtener el ID del usuario desde SharedPreferences
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            // Obtener los datos del usuario desde la BD
            User user = dbHelper.getUserById(userId);

            if (user != null) {
                // Mostrar los datos en los TextView
                userName.setText(user.getName());
                saldo.setText(String.format("%,.2f", user.getSaldo()));

            } else {
                Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró sesión de usuario", Toast.LENGTH_SHORT).show();
        }
    }


}