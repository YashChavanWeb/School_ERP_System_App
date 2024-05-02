package com.example.erp_system_

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class TeacherShareNotice : AppCompatActivity() {
    private lateinit var editTextNoticeTitle: EditText
    private lateinit var editTextNoticeDescription: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var textViewNoticeDate: TextView

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null // Variable to store selected image URI
    private var isUploading = false // Variable to track if upload is in progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_share_notice)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Initialize views
        editTextNoticeTitle = findViewById(R.id.editTextNoticeTitle)
        editTextNoticeDescription = findViewById(R.id.editTextNoticeDescription)
        imagePreview = findViewById(R.id.imagePreview)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        textViewNoticeDate = findViewById(R.id.textViewNoticeDate)

        // Setup spinner for notice category
        val categories = arrayOf("Student", "Teachers", "All")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter

        // Set current date for the notice
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        textViewNoticeDate.text = currentDate
    }

    // Method to handle adding image
    fun addImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // Handle image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imagePreview.setImageURI(imageUri)
            imagePreview.visibility = View.VISIBLE
        }
    }

    // Method to handle sharing notice
    fun shareNotice(view: View) {
        // Check if upload is already in progress
        if (isUploading) {
            Toast.makeText(this, "Upload in progress, please wait", Toast.LENGTH_SHORT).show()
            return
        }

        // Set upload flag to true
        isUploading = true

        // Show loading indicator
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Rest of the code to upload notice
        val title = editTextNoticeTitle.text.toString()
        val description = editTextNoticeDescription.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Check if an image is selected
        if (imageUri != null) {
            // Upload image to Firebase Storage
            val imageRef = storageReference.child("images/${UUID.randomUUID()}")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // Save notice to Firebase Realtime Database with image URL
                        val ref = database.reference.child("notices").push()
                        val notice = Notice(title, description, category, date, imageUrl)
                        ref.setValue(notice)
                        // Show success message
                        Toast.makeText(this, "Notice shared successfully", Toast.LENGTH_SHORT).show()
                        // Reset UI
                        resetUI()
                        // Dismiss loading indicator and reset upload flag
                        progressDialog.dismiss()
                        isUploading = false
                    }
                }
                .addOnFailureListener { e ->
                    // Handle image upload failure
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    // Dismiss loading indicator and reset upload flag
                    progressDialog.dismiss()
                    isUploading = false
                }
        } else {
            // Save notice to Firebase Realtime Database without image URL
            val ref = database.reference.child("notices").push()
            val notice = Notice(title, description, category, date)
            ref.setValue(notice)
            // Show success message
            Toast.makeText(this, "Notice shared successfully", Toast.LENGTH_SHORT).show()
            // Reset UI
            resetUI()
            // Dismiss loading indicator and reset upload flag
            progressDialog.dismiss()
            isUploading = false
        }
    }

    // Method to reset UI
    private fun resetUI() {
        // Clear input fields
        editTextNoticeTitle.text.clear()
        editTextNoticeDescription.text.clear()

        // Reset image preview
        imagePreview.setImageDrawable(null)
        imagePreview.visibility = View.GONE

        // Reset spinner selection
        spinnerCategory.setSelection(0)

        // Reset image URI
        imageUri = null
    }
}
