package com.appstore.streaming_store.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import androidx.annotation.Nullable;

import com.appstore.streaming_store.models.User;
import com.appstore.streaming_store.models.UserService;
import com.appstore.streaming_store.models.Historic;
import com.appstore.streaming_store.models.StreamingService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminSQLitOpenHelper extends SQLiteOpenHelper {
    public AdminSQLitOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creacion de tabla usuario
        String createTableUser = ("CREATE TABLE user" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "phone TEXT NOT NULL, " +
                "document TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "saldo REAL DEFAULT 1000000.0, " +
                "token text)");
        db.execSQL(createTableUser);

        //creacion de tabla servicios de streaming
        String createTableStreaming = ("CREATE TABLE streaming" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL UNIQUE," +
                "logo TEXT," +
                "banner TEXT," +
                "description TEXT," +
                "monto REAL NOT NULL)");
        db.execSQL(createTableStreaming);

        //creacion de tabla servicios-user
        String createTableStreamingUser = ("CREATE TABLE user_service" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "service_id INTEGER NOT NULL," +
                "activo INTEGER DEFAULT 1," + //activo = 1 / cancelado = 0
                "buy_date DATETIME," +
                "cancellation_date DATETIME," +
                "FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE," +
                "FOREIGN KEY (service_id) REFERENCES streaming (id) ON DELETE CASCADE)");
        db.execSQL(createTableStreamingUser);

        //creacion de tabla historial
        String createTableHistoric = ("CREATE TABLE historic" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "service_id INTEGER NOT NULL," +
                "type_transaction TEXT NOT NULL," + //activo = 1 / cancelado = 0
                "amount REAL NOT NULL," +
                "residue_remaing REAL NOT NULL," +
                "transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE," +
                "FOREIGN KEY (service_id) REFERENCES streaming (id) ON DELETE CASCADE)");
        db.execSQL(createTableHistoric);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Registrar el usuario
    public boolean registerUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("document", user.getDocument());
        values.put("password", user.getPassword());

        try{
            long result = db.insert("user",null, values);
            return result != -1;
        } catch (SQLiteConstraintException e){
            Log.e("DB_ERROR", "Error al insertar usuario: " +e.getMessage());
            return false;
        } catch (Exception e){
            Log.e("DB_ERROR", "Error inesperado" + e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    //buscar usuario
    public User loginUser(String document, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT id, name, email, phone, document, saldo, token FROM user WHERE document = ? AND password = ?",
                    new String[]{document, password}
            );

            if (cursor != null && cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setDocument(cursor.getString(cursor.getColumnIndexOrThrow("document")));
                user.setSaldo(cursor.getDouble(cursor.getColumnIndexOrThrow("saldo")));
                user.setToken(cursor.getString(cursor.getColumnIndexOrThrow("token")));

                return user;
            }
            return null;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error en login: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

    }


    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT id, name, email, phone, document, saldo, token FROM user WHERE id = ?",
                    new String[]{String.valueOf(userId)}
            );

            if (cursor != null && cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setDocument(cursor.getString(cursor.getColumnIndexOrThrow("document")));
                user.setSaldo(cursor.getDouble(cursor.getColumnIndexOrThrow("saldo")));
                user.setToken(cursor.getString(cursor.getColumnIndexOrThrow("token")));

                return user;
            }
            return null;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener usuario: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public boolean updateUserSaldo(int userId, double newSaldo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("saldo", newSaldo);

        try {
            int affectedRows = db.update("user", values, "id = ?",
                    new String[]{String.valueOf(userId)});
            return affectedRows > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al actualizar saldo: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean saveToken(int userId, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);

        try {
            int affectedRows = db.update("user", values, "id = ?",
                    new String[]{String.valueOf(userId)});
            return affectedRows > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al guardar token: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean verifyActiveSession(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT token FROM user WHERE id = ? AND token IS NOT NULL",
                    new String[]{String.valueOf(userId)}
            );
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error verificar sesión: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public boolean logoutUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull("token");

        try {
            int affectedRows = db.update("user", values, "id = ?",
                    new String[]{String.valueOf(userId)});
            return affectedRows > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al cerrar sesión: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean checkUserExists(String document, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM user WHERE document = ? OR email = ?",
                    new String[]{document, email}
            );
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }


    // metodos para el streamingservice

    // GET ALL STREAMING SERVICES
    public List<StreamingService> getAllStreamingServices() {
        List<StreamingService> services = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM streaming", null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StreamingService service = new StreamingService();
                    service.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    service.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                    service.setLogo(cursor.getString(cursor.getColumnIndexOrThrow("logo")));
                    service.setBanner(cursor.getString(cursor.getColumnIndexOrThrow("banner")));
                    service.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    service.setMonto(cursor.getDouble(cursor.getColumnIndexOrThrow("monto")));

                    services.add(service);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener servicios: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return services;
    }



}























