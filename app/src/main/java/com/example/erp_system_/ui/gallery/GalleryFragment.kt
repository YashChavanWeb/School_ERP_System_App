package com.example.erp_system_.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.example.erp_system_.PdfAdapter
import com.example.erp_system_.PdfItem
import com.example.erp_system_.R

class GalleryFragment : Fragment() {

    private lateinit var pdfAdapter: PdfAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfList: MutableList<PdfItem>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        // Initialize RecyclerView and layout manager
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize PDF list and adapter
        pdfList = mutableListOf()
        pdfAdapter = PdfAdapter(pdfList)
        recyclerView.adapter = pdfAdapter

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")

        // Fetch PDF data from Firebase
        fetchPdfDataFromFirebase()

        return view
    }

    private fun fetchPdfDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (pdfSnapshot in dataSnapshot.children) {
                        val pdfTitle = pdfSnapshot.child("title").getValue(String::class.java) ?: ""
                        val pdfUrl = pdfSnapshot.child("pdfUrl").getValue(String::class.java) ?: ""
                        val pdfItem = PdfItem(pdfTitle, pdfUrl)
                        pdfList.add(pdfItem)
                    }
                    pdfAdapter.notifyDataSetChanged()
                } else {
                    // Handle case where no PDFs are available
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}
