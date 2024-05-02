package com.example.erp_system_

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment

class OTPVerificationDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_otpverification_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("OTP Verification")
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextOTP = view.findViewById<EditText>(R.id.editTextOTP)
        val buttonVerify = view.findViewById<Button>(R.id.buttonVerifyOTP)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        buttonVerify.setOnClickListener {
            val enteredOTP = editTextOTP.text.toString()
            val correctOTP = "123456" // Replace with actual OTP sent to user's email or phone
            if (enteredOTP == correctOTP) {
                // Show progress bar
                progressBar.visibility = View.VISIBLE
                // Simulate a delay before redirecting to the main activity
                view.postDelayed({
                    // Hide progress bar
                    progressBar.visibility = View.GONE
                    // Redirect to main activity
                    startActivity(Intent(activity, MainActivity::class.java))
                    // Close this dialog
                    dismiss()
                }, 2000) // Adjust the delay as needed
            } else {
                // Incorrect OTP, show error message
                editTextOTP.error = "Incorrect OTP"
            }
        }

        buttonCancel.setOnClickListener {
            // User cancels OTP verification, dismiss dialog
            dismiss()
        }
    }
}


