package com.example.erp_system_

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewSignUp: TextView
    private lateinit var textViewForgotPassword: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var imageButtonTogglePassword: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance()

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        progressBar = findViewById(R.id.progressBar)
        textViewSignUp = findViewById(R.id.textViewSignUp)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
        imageButtonTogglePassword = findViewById(R.id.imageButtonTogglePassword)

        // Set onClickListener for eye icon to toggle password visibility
        imageButtonTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Set onClickListener for login button
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                // Empty fields
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Show progress bar to indicate loading state
                progressBar.visibility = View.VISIBLE

                // Authenticate user with Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = mAuth.currentUser
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                            navigateToBeforeMainActivity()
                        } else {
                            // If sign in fails, display a message to the user.
                            when {
                                task.exception is FirebaseAuthInvalidUserException -> {
                                    Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show()
                                }
                                task.exception is FirebaseAuthInvalidCredentialsException -> {
                                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            progressBar.visibility = View.GONE
                        }
                    }
            }
        }

        // Set onClickListener for sign up text
        textViewSignUp.setOnClickListener {
            // Start SignUpActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Set onClickListener for forgot password text
        textViewForgotPassword.setOnClickListener {
            // Show OTP verification dialog
            val otpVerificationDialog = OTPVerificationDialog()
            otpVerificationDialog.show(supportFragmentManager, "otp_verification_dialog")
        }
    }

    private fun togglePasswordVisibility() {
        if (editTextPassword.transformationMethod == null) {
            // Password is currently visible, hide it
            editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            imageButtonTogglePassword.setImageResource(R.drawable.eye_closed)
        } else {
            // Password is currently hidden, show it
            editTextPassword.transformationMethod = null
            imageButtonTogglePassword.setImageResource(R.drawable.eye_open)
        }

        // Move cursor to the end of the text after changing transformation method
        editTextPassword.setSelection(editTextPassword.text.length)
    }



    private fun navigateToBeforeMainActivity() {
        startActivity(Intent(this, BeforeMainActivity::class.java))
        finish()
    }
}
