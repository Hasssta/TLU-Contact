package com.example.contact_test.database

import android.provider.BaseColumns

object UserContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FULLNAME = "fullname"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ROLE = "role"
        const val COLUMN_CONTACT_ID = "contact_id" // Liên kết với contact
    }

    const val SQL_CREATE_USERS_TABLE = """
        CREATE TABLE ${UserEntry.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY,
            ${UserEntry.COLUMN_USERNAME} TEXT UNIQUE NOT NULL,
            ${UserEntry.COLUMN_PASSWORD} TEXT NOT NULL,
            ${UserEntry.COLUMN_FULLNAME} TEXT,
            ${UserEntry.COLUMN_EMAIL} TEXT,
            ${UserEntry.COLUMN_ROLE} TEXT,
            ${UserEntry.COLUMN_CONTACT_ID} INTEGER,
            FOREIGN KEY (${UserEntry.COLUMN_CONTACT_ID}) 
            REFERENCES ${ContactContract.ContactEntry.TABLE_NAME} (${BaseColumns._ID})
        )
    """

    const val SQL_DELETE_USERS_TABLE = "DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}"
}
