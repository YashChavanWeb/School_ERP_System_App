package com.example.erp_system_

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.erp_system_.R
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder class
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.nameTextView)
        val userImageView = itemView.findViewById<ImageView>(R.id.userImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // Set fixed name and image for the chat room
        holder.textName.text = "Chat Room"

        // Load ERP logo image using Glide or any other method
        Glide.with(context)
            .load(R.drawable.erplogo) // assuming erp_logo is the image resource
            .into(holder.userImageView)

        // Set click listener to start ChatMainActivity
        holder.itemView.setOnClickListener {
            // No need to pass user details for the chat room
            val intent = Intent(context, ChatMainActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }
}
