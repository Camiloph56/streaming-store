package com.appstore.streaming_store;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appstore.streaming_store.database.AdminSQLitOpenHelper;
import com.appstore.streaming_store.models.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText name1, email1, phone1, document1, password1;

    private AdminSQLitOpenHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name1 = findViewById(R.id.name);
        email1 = findViewById(R.id.email);
        phone1 = findViewById(R.id.phone);
        document1 = findViewById(R.id.document);
        password1 = findViewById(R.id.password);

        dbHelper = new AdminSQLitOpenHelper(this, "streaming_db", null, 1);


    }

    public void signUp(View v){

        String name = name1.getText().toString();
        String email = email1.getText().toString().trim();
        String phone = phone1.getText().toString();
        String document = document1.getText().toString();
        String password = password1.getText().toString();

        // Validaciones
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || document.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si ya existe el usuario
        if (dbHelper.checkUserExists(document, email)) {
            Toast.makeText(this, "Documento o email ya registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(name, email, phone, document, password);

        boolean success = dbHelper.registerUser(newUser);


        if (success) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            cleanForm();

            finish();


        } else {
            Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show();
        }



    }

    private void cleanForm(){
        name1.setText("");
        email1.setText("");
        phone1.setText("");
        document1.setText("");
        password1.setText("");
    }
}