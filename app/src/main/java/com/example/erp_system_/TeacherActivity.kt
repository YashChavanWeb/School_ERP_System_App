package com.example.erp_system_

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the card view for the "Create Quiz" option
        val createQuizCard = findViewById<CardView>(R.id.option4Card)
        val uploadPdf = findViewById<CardView>(R.id.option1Card)
        val attendence = findViewById<CardView>(R.id.option3Card)
        val shareNotice = findViewById<CardView>(R.id.sharenotice)


        // Set OnClickListener to the card view
        createQuizCard.setOnClickListener {
            // Start TeacherCreateQuizActivity when the card view is clicked
            startActivity(Intent(this, TeacherCreateQuiz::class.java))
        }

        // Set OnClickListener to the card view
        uploadPdf.setOnClickListener {
            // Start TeacherCreateQuizActivity when the card view is clicked
            startActivity(Intent(this, TeacherUploadPdf::class.java))
        }

        // Set OnClickListener to the card view
        attendence.setOnClickListener {
            // Start TeacherCreateQuizActivity when the card view is clicked
            startActivity(Intent(this, AttendenceActivity::class.java))
        }

        // Set OnClickListener to the card view
        shareNotice.setOnClickListener {
            // Start TeacherCreateQuizActivity when the card view is clicked
            startActivity(Intent(this, TeacherShareNotice::class.java))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToBeforeMainActivity()
    }

    private fun navigateToBeforeMainActivity() {
        val intent = Intent(this, BeforeMainActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity if you don't want to return to it
    }
}
