package com.example.erp_system_



class User {
    var fullName: String? = null
    var email: String? = null
    var uid: String? = null
    var division: String = "" // Add division field
    var imageUrl: String = "" // Add imageUrl property with default value

    // Default constructor
    constructor(){}

    // Parameterized constructor
    constructor(fullName: String, email: String, uid: String, imageUrl: String = ""){
        this.fullName = fullName
        this.email = email
        this.uid = uid
        this.imageUrl = imageUrl
    }

    // Function to generate roll number based on initials of full name
    fun generateRollNumber(): String {
        val initials = fullName?.split(" ")?.mapNotNull { it.firstOrNull()?.toUpperCase() }?.joinToString("")
        return initials ?: ""
    }
}
