package com.example.contact_test.model

data class Contact(
    val id: Long = 0,
    val name: String,
    val position: String,
    val phone: String,
    val email: String,
    val departmentId: Long = 0,
    val avatarUri: String? = null
)
