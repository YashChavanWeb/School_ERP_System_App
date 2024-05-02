package com.example.erp_system_

class Notice(
    val title: String,
    val description: String,
    val category: String,
    val date: String,
    val imageUrl: String = ""
) {
    // Default constructor needed for Firebase
    constructor() : this("", "", "", "")
}
