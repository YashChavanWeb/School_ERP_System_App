package com.example.erp_system_.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.erp_system_.Notice
import com.example.erp_system_.NoticeAdapter
import com.example.erp_system_.R
import com.google.firebase.database.*

class SlideshowFragment : Fragment() {

    private lateinit var listViewNotices: ListView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        // Initialize ListView
        listViewNotices = root.findViewById(R.id.listViewNotices)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("notices")

        // Fetch and display notice data from Firebase
        fetchNoticeDataFromFirebase()

        return root
    }

    private fun fetchNoticeDataFromFirebase() {
        val notices = ArrayList<Notice>()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val notice = snapshot.getValue(Notice::class.java)
                        notice?.let {
                            notices.add(it)
                        }
                    }

                    // Set up adapter for ListView
                    val adapter = NoticeAdapter(requireContext(), R.layout.notice_show_kayout, notices)
                    listViewNotices.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}
