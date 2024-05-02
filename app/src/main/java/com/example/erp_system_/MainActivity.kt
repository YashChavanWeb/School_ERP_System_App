package com.example.erp_system_

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.erp_system_.ui.home.HomeFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            val id = item.itemId
            when (id) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                }
                R.id.nav_gallery -> {
                    navController.navigate(R.id.nav_gallery)
                }
                R.id.nav_slideshow -> {
                    navController.navigate(R.id.nav_slideshow)
                }
            }
            true
        }
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this,
                    ChatActivity::class.java
                )
            )
        }

        // Update user's email and profile image in navigation drawer header
        updateNavigationHeader()
    }

    private fun updateNavigationHeader() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val textViewEmail =
            navigationView.getHeaderView(0).findViewById<TextView>(R.id.textViewEmail)
        val imageViewProfile =
            navigationView.getHeaderView(0).findViewById<ImageView>(R.id.imageViewProfile)
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val userEmail = user.email
            textViewEmail.text = userEmail

            // Load profile image using Glide library
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.erplogo) // Placeholder image if userProfileImageUrl is null
                .error(R.drawable.erplogo) // Error image if Glide fails to load the image
                .circleCrop()
                .into(imageViewProfile)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                true
            }
            R.id.action_profile -> {
                // Show profile info dialog
                val profileDialog = PortfolioInfoDialog()
                profileDialog.show(supportFragmentManager, "ProfileInfoDialog")
                true
            }
            R.id.action_my_score -> {
                // Show my score dialog
                val scoreDialog = MyScoreDialogFragment()
                scoreDialog.show(supportFragmentManager, "MyScoreDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        startActivity(
            Intent(this, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, BeforeMainActivity::class.java))
        finish()
    }
}
