package com.example.erp_system_

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class BeforeMainActivity : AppCompatActivity() {

    private var optionSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_before_main)

        // Find the teacher and student card views
        val teacherCard = findViewById<CardView>(R.id.teacherCard)
        val studentCard = findViewById<CardView>(R.id.studentCard)

        // Set click listeners for the cards
        teacherCard.setOnClickListener {
            if (!optionSelected) {
                showOTPVerificationDialog()
            }
        }

        studentCard.setOnClickListener {
            if (!optionSelected) {
                navigateToMainActivity("Student")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutAndNavigateToLoginActivity()
                true
            }
            R.id.action_profile -> {
                // Handle profile action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Handle back press to log out and go to the login activity
        logoutAndNavigateToLoginActivity()
    }

    private fun logoutAndNavigateToLoginActivity() {
        // Handle logout and navigation to login activity
        // This code might vary based on your authentication mechanism
    }

    private fun showOTPVerificationDialog() {
        // Create a custom dialog for OTP entry
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_otpverification_dialog)

        // Find views in the dialog
        val otpEditText = dialog.findViewById<EditText>(R.id.editTextOTP)
        val verifyButton = dialog.findViewById<Button>(R.id.buttonVerifyOTP)
        val cancelButton = dialog.findViewById<Button>(R.id.buttonCancel)

        // Set click listener for the verify button
        verifyButton.setOnClickListener {
            val enteredOTP = otpEditText.text.toString()
            val correctOTP = "123456" // Predefined correct OTP

            if (enteredOTP == correctOTP) {
                // If the entered OTP is correct, dismiss the dialog and proceed to TeacherActivity
                dialog.dismiss()
                navigateToTeacherActivity()
            } else {
                // If the entered OTP is incorrect, show an error message
                Toast.makeText(this, "Incorrect OTP, please try again", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for the cancel button
        cancelButton.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog if the user cancels
        }

        // Show the dialog
        dialog.show()
    }

    private fun navigateToTeacherActivity() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        // Finish the BeforeMainActivity if you don't want to return to it
        finish()
        optionSelected = true
    }

    private fun navigateToMainActivity(selectedOption: String) {
        val intent = Intent(this, MainActivity::class.java)
        // Pass the selected option to the MainActivity
        intent.putExtra("SELECTED_OPTION", selectedOption)
        startActivity(intent)
        // Finish the BeforeMainActivity if you don't want to return to it
        finish()
        optionSelected = true
    }
}
