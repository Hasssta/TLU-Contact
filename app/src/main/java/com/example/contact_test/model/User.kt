package com.example.contact_test.model

data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val fullName: String = "",
    val email: String = "",
    val role: String = "user", // Có thể là "admin", "user"
    val contactId: Long = 0 // ID của contact liên kết với user
)
