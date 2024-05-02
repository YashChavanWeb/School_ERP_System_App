package com.example.erp_system_

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ChatMainActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mdbRef: DatabaseReference
    private lateinit var messageList: ArrayList<Message>

    private var chatRoomId: String? = null
    private lateinit var senderUid: String
    private lateinit var receiverUid: String
    private var receiverName: String = ""
    private var senderName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.sentButton)

        mdbRef = FirebaseDatabase.getInstance().reference
        senderUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        receiverUid = intent.getStringExtra("receiverUid") ?: ""

        // Retrieve sender's name from Firebase
        mdbRef.child("users").child(senderUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    senderName = it.fullName ?: ""
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        // Retrieve receiver's name from Firebase
        mdbRef.child("users").child(receiverUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    receiverName = it.fullName ?: ""
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        // Create a chat room ID by concatenating sender's and receiver's IDs
        chatRoomId = if (senderUid < receiverUid) {
            "$senderUid-$receiverUid"
        } else {
            "$receiverUid-$senderUid"
        }

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        sendButton.setOnClickListener {
            val message = messageBox.text.toString().trim()
            if (message.isNotEmpty()) {
                val messageId = mdbRef.child("chats").child("chatRooms").child(chatRoomId!!).child("messages").push().key
                if (messageId != null) {
                    val messageObject = Message(message, senderUid, senderName) // Pass sender's name to the message object
                    mdbRef.child("chats").child("chatRooms").child(chatRoomId!!).child("messages").child(messageId).setValue(messageObject)
                        .addOnSuccessListener {
                            messageBox.setText("")
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure
                        }
                }
            }
        }

        mdbRef.child("chats").child("chatRooms").child(chatRoomId!!).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    message?.let {
                        messageList.add(it)
                        messageAdapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    // Handle changed messages if necessary
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    // Handle removed messages if necessary
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    // Handle moved messages if necessary
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }
}
