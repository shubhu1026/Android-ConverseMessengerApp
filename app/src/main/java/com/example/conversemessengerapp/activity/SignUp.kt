package com.example.conversemessengerapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.conversemessengerapp.databinding.ActivitySignupBinding
import com.example.conversemessengerapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@SignUp, Login::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if(password == confirmPassword) {
                signUp(name, email, password)
            }else{
            Toast.makeText(this, "Passwords do not match",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signUp(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val status:String = ""
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!,status)

                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    Toast.makeText(this@SignUp, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        status: String,
    ) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid, status))
    }
}