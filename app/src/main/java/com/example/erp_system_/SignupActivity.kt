package com.example.erp_system_

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
class SignupActivity : AppCompatActivity() {

    private lateinit var editTextFullName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonSignUp: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewLogin: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imageViewLogo: ImageView
    private lateinit var buttonSelectImage: Button
    private val PICK_IMAGE_REQUEST = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance()
        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        // Initialize views
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        progressBar = findViewById(R.id.progressBar)
        textViewLogin = findViewById(R.id.textViewLogin)
        imageViewLogo = findViewById(R.id.imageViewLogo)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)


        buttonSelectImage.setOnClickListener {
            openGallery()
        }

        buttonSignUp.setOnClickListener {
            val fullName = editTextFullName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                // All fields are required
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Invalid email format
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                // Password must be at least 6 characters long
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                // Passwords do not match
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (imageUri == null) {
                // No image selected
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            } else {
                // Show progress bar to indicate loading state
                progressBar.visibility = View.VISIBLE

                // Register the user with Firebase Authentication
                registerUser(email, password, fullName)
            }
        }

        // Set onClickListener for login text
        textViewLogin.setOnClickListener {
            // Navigate to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageViewLogo.setImageURI(imageUri)
        }
    }

    private fun registerUser(email: String, password: String, fullName: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success
                    val user = mAuth.currentUser
                    uploadImageToFirebaseStorage(user?.uid, fullName, email)
                } else {
                    // If sign in fails, display a message to the user.
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Sign up failed. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun uploadImageToFirebaseStorage(userId: String?, fullName: String, email: String) {
        if (imageUri != null) {
            val imageRef = storageReference.child("$userId.jpg")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, get download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        addUserToDatabase(userId, fullName, email, imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    // Error occurred while uploading image
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
        } else {
            // No image selected, add user details without image URL
            addUserToDatabase(userId, fullName, email, "")
        }
    }

    private fun addUserToDatabase(userId: String?, fullName: String, email: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userRef = usersRef.child(userId ?: return) // Ensure userId is not null

        // Generate a random division (A, B, C, or D)
        val divisions = listOf("A", "B", "C", "D")
        val randomDivision = divisions.random()

        val userData = HashMap<String, Any>()
        userData["fullName"] = fullName
        userData["email"] = email
        userData["imageUrl"] = imageUrl
        userData["division"] = randomDivision // Include the random division

        userRef.setValue(userData)
            .addOnSuccessListener {
                // Data successfully added to the database
                Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BeforeMainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // Error occurred while adding data to the database
                Toast.makeText(this, "Failed to add user to database: ${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
    }


}
