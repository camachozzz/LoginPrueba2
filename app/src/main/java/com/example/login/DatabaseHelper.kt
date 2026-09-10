package com.example.login

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "pasanaku.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(user: String, pass: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put("username", user)
            put("password", pass)
        }
        val res = db.insert("users", null, cv)
        return res != -1L
    }

    fun login(user: String, pass: String): Boolean {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", arrayOf(user, pass))
        val ok = c.count > 0
        c.close()
        return ok
    }

    fun existeUsuario(user: String): Boolean {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT * FROM users WHERE username = ?", arrayOf(user))
        val ok = c.count > 0
        c.close()
        return ok
    }
}