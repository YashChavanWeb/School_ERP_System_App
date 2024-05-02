package com.example.erp_system_



import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyScoreDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("My Score")

        // Retrieve user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Retrieve user's quiz score from Firebase
        val userQuizReportRef = FirebaseDatabase.getInstance().reference.child("QuizReport").child(userId)
        userQuizReportRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val score = dataSnapshot.child("marks").getValue(Int::class.java) ?: 0
                // Set the message of the dialog with the retrieved score
                builder.setMessage("Your quiz score is $score")

                // Show the dialog after setting the message
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Show a message if failed to retrieve the score
                builder.setMessage("Failed to retrieve quiz score.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
        })

        // Return an empty dialog for now, the actual dialog will be shown after the score is retrieved
        return builder.create()
    }
}

