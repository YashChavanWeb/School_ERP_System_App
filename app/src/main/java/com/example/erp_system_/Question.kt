package com.example.erp_system_


data class Question(
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val marks: Int,
    var selectedOption: String = "", // Default value indicating no option selected
    var submitted: Boolean = false // Indicates whether the question has been submitted
) {
    // Default constructor required for Firebase
    constructor() : this("", "", "", "", "", "", 0)
}
