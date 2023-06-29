package com.example.baseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    var isok = false
    var values: List<String> = emptyList()

    val myRef = Firebase.database.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        readFromDatabase()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()


            readFromDatabase()
            Log.d("login", "Values are: $values")

            for (value in values) {
                if (value.startsWith("&id&$email&")) {
                    isok = true
                    Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show()

                    if (("&isTeacher&true&") in value) {
                        val intent = Intent(this, MainTeacherMenuActivity::class.java)
                        intent.putExtra("userData", value)
                        intent.putStringArrayListExtra("AllData", ArrayList(values))
                        startActivity(intent)
                    }
                    else {
                        val intent = Intent(this, MainUserMenuActivity::class.java)
                        intent.putExtra("userData", value)
                        intent.putStringArrayListExtra("AllData", ArrayList(values))
                        startActivity(intent)
                    }
                    break
                }
            }
            Log.d("login", "Isok: $isok")

            if (!isok){
                Toast.makeText(this, "Пароль или логин не верный", Toast.LENGTH_SHORT).show()
            }
            isok = false

        }
    }


    private fun readFromDatabase() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                values = dataSnapshot.children.mapNotNull { it.value as? String }
                Log.d("database", "Values are: $values")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
