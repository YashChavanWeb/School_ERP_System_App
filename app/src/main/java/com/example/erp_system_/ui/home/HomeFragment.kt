package com.example.erp_system_.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp_system_.databinding.FragmentHomeBinding
import com.example.erp_system_.QuizAdapter
import com.example.erp_system_.Question
import com.example.erp_system_.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var quizAdapter: QuizAdapter
    private lateinit var database: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView setup
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        database = FirebaseDatabase.getInstance().reference.child("quizzes")

        quizAdapter = QuizAdapter(emptyList())
        recyclerView.adapter = quizAdapter

        retrieveQuizData()

        homeViewModel.text.observe(viewLifecycleOwner) {
            // You can manipulate any UI elements or ViewModel data here
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retrieveQuizData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizList = mutableListOf<Question>()
                for (quizSnapshot in snapshot.children) {
                    for (questionSnapshot in quizSnapshot.child("questions").children) {
                        val question = questionSnapshot.getValue(Question::class.java)
                        question?.let { quizList.add(it) }
                    }
                }
                quizAdapter.updateData(quizList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to retrieve quiz data: ${error.message}")
            }
        })
    }
}

