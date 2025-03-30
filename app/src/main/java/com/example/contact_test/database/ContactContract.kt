package com.example.contact_test.database

import android.provider.BaseColumns

object ContactContract {
    // Định nghĩa bảng Contact
    object ContactEntry : BaseColumns {
        const val TABLE_NAME = "contacts"
        const val COLUMN_NAME = "name"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_DEPARTMENT_ID = "department_id"
        const val COLUMN_AVATAR_URI = "avatar_uri"
    }

    // Định nghĩa bảng Department
    object DepartmentEntry : BaseColumns {
        const val TABLE_NAME = "departments"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
    }

    // Các câu lệnh SQL để tạo bảng
    const val SQL_CREATE_CONTACTS_TABLE = """
        CREATE TABLE ${ContactEntry.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY,
            ${ContactEntry.COLUMN_NAME} TEXT NOT NULL,
            ${ContactEntry.COLUMN_POSITION} TEXT,
            ${ContactEntry.COLUMN_PHONE} TEXT,
            ${ContactEntry.COLUMN_EMAIL} TEXT,
            ${ContactEntry.COLUMN_DEPARTMENT_ID} INTEGER,
            ${ContactEntry.COLUMN_AVATAR_URI} TEXT,
            FOREIGN KEY (${ContactEntry.COLUMN_DEPARTMENT_ID}) 
            REFERENCES ${DepartmentEntry.TABLE_NAME} (${BaseColumns._ID})
        )
    """

    const val SQL_CREATE_DEPARTMENTS_TABLE = """
        CREATE TABLE ${DepartmentEntry.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY,
            ${DepartmentEntry.COLUMN_NAME} TEXT NOT NULL,
            ${DepartmentEntry.COLUMN_PHONE} TEXT,
            ${DepartmentEntry.COLUMN_EMAIL} TEXT
        )
    """

    // Câu lệnh xóa bảng
    const val SQL_DELETE_CONTACTS_TABLE = "DROP TABLE IF EXISTS ${ContactEntry.TABLE_NAME}"
    const val SQL_DELETE_DEPARTMENTS_TABLE = "DROP TABLE IF EXISTS ${DepartmentEntry.TABLE_NAME}"
}