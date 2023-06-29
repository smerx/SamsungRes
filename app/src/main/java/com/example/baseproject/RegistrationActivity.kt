package com.example.baseproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var userTypeRadioGroup: RadioGroup
    private lateinit var studentRadioButton: RadioButton
    private lateinit var teacherRadioButton: RadioButton
    val base = Firebase.database.getReference()
    var values: List<String> = emptyList()
    var isbusy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup)
        studentRadioButton = findViewById(R.id.studentRadioButton)
        teacherRadioButton = findViewById(R.id.teacherRadioButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val isTeacher = teacherRadioButton.isChecked

            readFromDatabase()

            for (value in values) {
                if (value.startsWith("&id&$email&")) {
                    isbusy = true
                    break
                }
            }

            if (isbusy) {
                Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show()
            }
            else {
             val text = "&id&$email&password&$password&isTeacher&$isTeacher&"
             writeToDatabase(text, email)
            }


            Log.d("Registration", "Email: $email, Password: $password, isTeacher: $isTeacher")
            Toast.makeText(this, "Успешно", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun writeToDatabase(word: String, id: String) {
        Firebase.database.getReference(id).setValue(word)
    }

    private fun readFromDatabase() {
        base.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                values = dataSnapshot.children.mapNotNull { it.value as? String }
                Log.d("database", "Values are: $values")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
