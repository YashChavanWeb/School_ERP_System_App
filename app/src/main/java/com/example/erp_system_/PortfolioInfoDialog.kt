package com.example.erp_system_

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class PortfolioInfoDialog : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_portfolio_info_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("User Profile")
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        userId = auth.currentUser?.uid ?: return // Return if user is not signed in
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

        val imageViewProfile = view.findViewById<ImageView>(R.id.imageViewProfile)
        val textViewName = view.findViewById<TextView>(R.id.textViewName)
        val textViewEmail = view.findViewById<TextView>(R.id.textViewEmail)
        val textViewDivision = view.findViewById<TextView>(R.id.textViewDivision)
        val buttonClose = view.findViewById<Button>(R.id.buttonClose)

        // Retrieve division from the database
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val division = snapshot.child("division").getValue(String::class.java)

                if (division.isNullOrEmpty()) {
                    // Generate a random division if not already set
                    val divisions = listOf("A", "B", "C", "D")
                    val randomDivision = divisions.random()
                    textViewDivision.text = "Division: $randomDivision"

                    // Store the division in the database
                    databaseReference.child("division").setValue(randomDivision)
                } else {
                    // Use the retrieved division
                    textViewDivision.text = "Division: $division"
                }

                val fullName = snapshot.child("fullName").getValue(String::class.java)
                val email = snapshot.child("email").getValue(String::class.java)
                val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)

                textViewName.text = "Name: $fullName"
                textViewEmail.text = "Email: $email"

                // Load user's image using Glide library
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext()).load(imageUrl).into(imageViewProfile)
                } else {
                    // If imageUrl is empty or null, you can load a placeholder image
                    // Glide.with(requireContext()).load(R.drawable.default_profile_image).into(imageViewProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        buttonClose.setOnClickListener {
            // Close the dialog
            dismiss()
        }
    }
}
