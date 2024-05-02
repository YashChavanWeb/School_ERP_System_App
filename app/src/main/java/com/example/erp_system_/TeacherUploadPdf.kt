package com.example.erp_system_


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class TeacherUploadPdf : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var buttonUploadPdf: Button
    private lateinit var buttonPost: Button
    private lateinit var textViewSelectedFiles: TextView
    private lateinit var recyclerViewSelectedFiles: RecyclerView
    private lateinit var selectedFilesAdapter: SelectedFilesAdapter
    private lateinit var storageReference: StorageReference
    private var pdfUris: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_upload_pdf)

        editTextTitle = findViewById(R.id.editTextTitle)
        buttonUploadPdf = findViewById(R.id.buttonUploadPdf)
        buttonPost = findViewById(R.id.buttonPost)
        textViewSelectedFiles = findViewById(R.id.textViewSelectedFiles)
        recyclerViewSelectedFiles = findViewById(R.id.recyclerViewSelectedFiles)

        storageReference = FirebaseStorage.getInstance().reference.child("pdfs")

        selectedFilesAdapter = SelectedFilesAdapter()
        recyclerViewSelectedFiles.apply {
            layoutManager = LinearLayoutManager(this@TeacherUploadPdf)
            adapter = selectedFilesAdapter
        }

        buttonUploadPdf.setOnClickListener {
            selectPdfFiles()
        }

        buttonPost.setOnClickListener {
            postToFirebase()
        }
    }

    private fun selectPdfFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select PDFs"), PDF_REQUEST_CODE)
    }

    private fun postToFirebase() {
        if (pdfUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one PDF file", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")

        // Upload each selected PDF file
        for (pdfUri in pdfUris) {
            val fileName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.pdf"
            val reference = storageReference.child(fileName)

            reference.putFile(pdfUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL for the uploaded PDF
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        // Save PDF metadata to the Realtime Database
                        val pdfTitle = editTextTitle.text.toString()
                        val pdfUrl = uri.toString()

                        val pdfData = hashMapOf(
                            "title" to pdfTitle,
                            "url" to pdfUrl
                        )

                        val pdfId = databaseReference.push().key
                        if (pdfId != null) {
                            databaseReference.child(pdfId).setValue(pdfData)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    // You can add more handling here if needed
                }
        }

        // Clear the list of selected PDF files after uploading
        pdfUris.clear()
        selectedFilesAdapter.notifyDataSetChanged()

        Toast.makeText(this, "PDFs uploaded successfully", Toast.LENGTH_SHORT).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Check if multiple files are selected
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    pdfUris.add(uri)
                }
            } else if (data.data != null) {
                // Single file selected
                val uri = data.data!!
                pdfUris.add(uri)
            }

            // Update UI to show selected files
            showSelectedFiles()
        }
    }

    private fun showSelectedFiles() {
        if (pdfUris.isNotEmpty()) {
            textViewSelectedFiles.text = "Selected PDFs:"
            selectedFilesAdapter.setData(pdfUris)
            recyclerViewSelectedFiles.visibility = RecyclerView.VISIBLE
        } else {
            textViewSelectedFiles.text = "No PDFs selected"
            recyclerViewSelectedFiles.visibility = RecyclerView.GONE
        }
    }

    companion object {
        private const val PDF_REQUEST_CODE = 1
    }
}
