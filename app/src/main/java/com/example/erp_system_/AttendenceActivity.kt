package com.example.erp_system_

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.Calendar
import com.example.erp_system_.R


class AttendenceActivity : AppCompatActivity() {

    private lateinit var divisionDropdown: Spinner
    private lateinit var userListView: ListView
    private lateinit var dateEditText: EditText
    private lateinit var submitBtn: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var attendanceRef: DatabaseReference
    private lateinit var userListAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendence)

        divisionDropdown = findViewById(R.id.divisionDropdown)
        userListView = findViewById(R.id.userListView)
        dateEditText = findViewById(R.id.dateEditText)
        submitBtn = findViewById(R.id.submitBtn)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        attendanceRef = database.getReference("Attendance")

        // Initialize the spinner with division values
        val divisions = arrayOf("A", "B", "C", "D")
        val divisionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, divisions)
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        divisionDropdown.adapter = divisionAdapter

        // Initialize the ListView adapter
        userListAdapter = UserListAdapter(this, mutableListOf())
        userListView.adapter = userListAdapter

        // Set OnClickListener for date EditText
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Set OnClickListener for submit button
        submitBtn.setOnClickListener {
            submitAttendance()
        }

        // Set OnItemSelectedListener for division dropdown
        divisionDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDivision = parent?.getItemAtPosition(position).toString()
                retrieveUsersByDivision(selectedDivision)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun retrieveUsersByDivision(division: String) {
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            usersRef.orderByChild("division").equalTo(division)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userList = mutableListOf<User>()
                        for (userSnapshot in snapshot.children) {
                            val fullName = userSnapshot.child("fullName").getValue(String::class.java)
                            if (fullName != null) {
                                val user = User(fullName, "", userSnapshot.key ?: "")
                                userList.add(user)
                            }
                        }
                        displayUsers(userList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database error
                    }
                })
        }
    }

    private fun displayUsers(userList: List<User>) {
        userListAdapter.clear()
        userListAdapter.addAll(userList)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                dateEditText.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun submitAttendance() {
        // Get the selected date
        val selectedDate = dateEditText.text.toString()

        // Get the selected division
        val selectedDivision = divisionDropdown.selectedItem.toString()

        // Iterate over each user in the ListView and get their attendance status
        val attendanceMap = mutableMapOf<String, String>()
        for (i in 0 until userListView.count) {
            val view = userListView.getChildAt(i)
            val presentRadioButton = view.findViewById<RadioButton>(R.id.presentRadioButton)
            val absentRadioButton = view.findViewById<RadioButton>(R.id.absentRadioButton)
            val userName = userListAdapter.getItem(i)?.fullName ?: ""
            val status = if (presentRadioButton.isChecked) "Present" else "Absent"
            attendanceMap[userName] = status
        }

        // Store attendance in Firebase under the "Attendance" node
        val divisionAttendanceRef = attendanceRef.child(selectedDivision).child(selectedDate)
        divisionAttendanceRef.setValue(attendanceMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Attendance submitted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit attendance: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
