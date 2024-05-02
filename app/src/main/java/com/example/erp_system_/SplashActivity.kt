package com.example.erp_system_

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var imageViewLogo: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        imageViewLogo = findViewById(R.id.imageViewLogo)
        progressBar = findViewById(R.id.progressBar)

        val fadeInDuration = 1000L // 1 second
        ObjectAnimator.ofFloat(imageViewLogo, "alpha", 0f, 1f).apply {
            duration = fadeInDuration
            start()
        }
        ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f).apply {
            duration = fadeInDuration
            start()
        }

        // Add scaling animations
        val scaleUpDuration = 1500L // 1.5 seconds
        ObjectAnimator.ofFloat(imageViewLogo, "scaleX", 1f, 1.5f).apply {
            duration = scaleUpDuration
            start()
        }
        ObjectAnimator.ofFloat(imageViewLogo, "scaleY", 1f, 1.5f).apply {
            duration = scaleUpDuration
            start()
        }

        // Using a delay to redirect after animation
        imageViewLogo.postDelayed({
            checkUserLoggedIn()
        }, 2000L) // 2000 milliseconds = 2 seconds delay
    }

    private fun checkUserLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in, move to main activity
            navigateToMainActivity()
        } else {
            // User is not logged in, move to login activity
            navigateToLoginActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, BeforeMainActivity::class.java))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
