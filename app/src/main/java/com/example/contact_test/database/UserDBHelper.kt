//package com.example.contact_test.database
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import android.provider.BaseColumns
//import com.example.contact_test.model.User
//
//class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    companion object {
//        const val DATABASE_VERSION = 2
//        const val DATABASE_NAME = "UserManager.db"
//    }
//
//    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(UserContract.SQL_CREATE_USERS_TABLE)
//        insertDefaultUsers(db)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL(UserContract.SQL_DELETE_USERS_TABLE)
//        onCreate(db)
//    }
//
//    private fun insertDefaultUsers(db: SQLiteDatabase) {
//        // Thêm admin
//        val adminValues = ContentValues().apply {
//            put(UserContract.UserEntry.COLUMN_USERNAME, "admin")
//            put(UserContract.UserEntry.COLUMN_PASSWORD, "admin")
//            put(UserContract.UserEntry.COLUMN_FULLNAME, "Administrator")
//            put(UserContract.UserEntry.COLUMN_EMAIL, "admin@example.com")
//            put(UserContract.UserEntry.COLUMN_ROLE, "admin")
//            put(UserContract.UserEntry.COLUMN_CONTACT_ID, 0) // Admin không liên kết với contact cụ thể
//        }
//        db.insert(UserContract.UserEntry.TABLE_NAME, null, adminValues)
//
//        // Thêm user thường - liên kết với contact_id = 1
//        val user1Values = ContentValues().apply {
//            put(UserContract.UserEntry.COLUMN_USERNAME, "user1")
//            put(UserContract.UserEntry.COLUMN_PASSWORD, "password1")
//            put(UserContract.UserEntry.COLUMN_FULLNAME, "Nguyễn Văn A")
//            put(UserContract.UserEntry.COLUMN_EMAIL, "nguyenvana@tlu.edu.vn")
//            put(UserContract.UserEntry.COLUMN_ROLE, "user")
//            put(UserContract.UserEntry.COLUMN_CONTACT_ID, 1) // Liên kết với Nguyễn Văn A
//        }
//        db.insert(UserContract.UserEntry.TABLE_NAME, null, user1Values)
//
//        // Thêm user thường - liên kết với contact_id = 2
//        val user2Values = ContentValues().apply {
//            put(UserContract.UserEntry.COLUMN_USERNAME, "user2")
//            put(UserContract.UserEntry.COLUMN_PASSWORD, "password2")
//            put(UserContract.UserEntry.COLUMN_FULLNAME, "Trần Thị B")
//            put(UserContract.UserEntry.COLUMN_EMAIL, "tranthib@tlu.edu.vn")
//            put(UserContract.UserEntry.COLUMN_ROLE, "user")
//            put(UserContract.UserEntry.COLUMN_CONTACT_ID, 2) // Liên kết với Trần Thị B
//        }
//        db.insert(UserContract.UserEntry.TABLE_NAME, null, user2Values)
//    }
//
////    fun checkUser(username: String, password: String): User? {
////        val db = this.readableDatabase
////        val selection = "${UserContract.UserEntry.COLUMN_USERNAME} = ? AND ${UserContract.UserEntry.COLUMN_PASSWORD} = ?"
////        val selectionArgs = arrayOf(username, password)
////
////        val cursor = db.query(
////            UserContract.UserEntry.TABLE_NAME,
////            null,
////            selection,
////            selectionArgs,
////            null,
////            null,
////            null
////        )
////
////        var user: User? = null
////
////        if (cursor.moveToFirst()) {
////            val idIndex = cursor.getColumnIndex(BaseColumns._ID)
////            val usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERNAME)
////            val passwordIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)
////            val fullnameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_FULLNAME)
////            val emailIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL)
////            val roleIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ROLE)
////            val contactIdIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_CONTACT_ID)
////
////            val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
////            val name = if (usernameIndex >= 0) cursor.getString(usernameIndex) else ""
////            val pass = if (passwordIndex >= 0) cursor.getString(passwordIndex) else ""
////            val fullname = if (fullnameIndex >= 0) cursor.getString(fullnameIndex) else ""
////            val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""
////            val role = if (roleIndex >= 0) cursor.getString(roleIndex) else "user"
////            val contactId = if (contactIdIndex >= 0) cursor.getLong(contactIdIndex) else 0
////
////            user = User(id, name, pass, fullname, email, role, contactId)
////        }
////
////        cursor.close()
////        return user
////    }
//
//    fun checkUser(username: String, password: String): User? {
//        val db = this.readableDatabase
//        val query = "SELECT * FROM ${UserContract.UserEntry.TABLE_NAME} " +
//                "WHERE ${UserContract.UserEntry.COLUMN_USERNAME} = ? " +
//                "AND ${UserContract.UserEntry.COLUMN_PASSWORD} = ?"
//        val selectionArgs = arrayOf(username, password)
//
//        val cursor = db.rawQuery(query, selectionArgs)
//        var user: User? = null
//
//        if (cursor.moveToFirst()) {
//            val idIndex = cursor.getColumnIndex(BaseColumns._ID)
//            val usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERNAME)
//            val passwordIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)
//            val fullnameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_FULLNAME)
//            val emailIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL)
//            val roleIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ROLE)
//            val contactIdIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_CONTACT_ID)
//
//            val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
//            val name = if (usernameIndex >= 0) cursor.getString(usernameIndex) else ""
//            val pass = if (passwordIndex >= 0) cursor.getString(passwordIndex) else ""
//            val fullname = if (fullnameIndex >= 0) cursor.getString(fullnameIndex) else ""
//            val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""
//            val role = if (roleIndex >= 0) cursor.getString(roleIndex) else "user"
//            val contactId = if (contactIdIndex >= 0) cursor.getLong(contactIdIndex) else 0
//
//            user = User(id, name, pass, fullname, email, role, contactId)
//        }
//
//        cursor.close()
//        return user
//    }
//
//
//    fun getUserByContactId(contactId: Long): User? {
//        val db = this.readableDatabase
//        val selection = "${UserContract.UserEntry.COLUMN_CONTACT_ID} = ?"
//        val selectionArgs = arrayOf(contactId.toString())
//
//        val cursor = db.query(
//            UserContract.UserEntry.TABLE_NAME,
//            null,
//            selection,
//            selectionArgs,
//            null,
//            null,
//            null
//        )
//
//        var user: User? = null
//
//        if (cursor.moveToFirst()) {
//            val idIndex = cursor.getColumnIndex(BaseColumns._ID)
//            val usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERNAME)
//            val passwordIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)
//            val fullnameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_FULLNAME)
//            val emailIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL)
//            val roleIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ROLE)
//            val contactIdIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_CONTACT_ID)
//
//            val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
//            val name = if (usernameIndex >= 0) cursor.getString(usernameIndex) else ""
//            val pass = if (passwordIndex >= 0) cursor.getString(passwordIndex) else ""
//            val fullname = if (fullnameIndex >= 0) cursor.getString(fullnameIndex) else ""
//            val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""
//            val role = if (roleIndex >= 0) cursor.getString(roleIndex) else "user"
//            val cId = if (contactIdIndex >= 0) cursor.getLong(contactIdIndex) else 0
//
//            user = User(id, name, pass, fullname, email, role, cId)
//        }
//
//        cursor.close()
//        return user
//    }
//}
