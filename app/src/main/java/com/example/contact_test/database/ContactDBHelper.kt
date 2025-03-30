package com.example.contact_test.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.contact_test.model.Contact
import com.example.contact_test.model.Department
import com.example.contact_test.model.User

class ContactDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "ContactManager.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng Department trước vì Contact có khóa ngoại tham chiếu đến Department
        db.execSQL(ContactContract.SQL_CREATE_DEPARTMENTS_TABLE)
        db.execSQL(ContactContract.SQL_CREATE_CONTACTS_TABLE)
        // Tạo bảng User
        db.execSQL(UserContract.SQL_CREATE_USERS_TABLE)

        // Thêm một số dữ liệu mẫu
        insertSampleData(db)
        // Thêm dữ liệu mẫu cho users
        insertSampleUsers(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Xóa bảng cũ và tạo lại
        db.execSQL(UserContract.SQL_DELETE_USERS_TABLE)
        db.execSQL(ContactContract.SQL_DELETE_CONTACTS_TABLE)
        db.execSQL(ContactContract.SQL_DELETE_DEPARTMENTS_TABLE)
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        // Thêm một số đơn vị mẫu
        val deptValues1 = ContentValues().apply {
            put(ContactContract.DepartmentEntry.COLUMN_NAME, "Khoa Công nghệ thông tin")
            put(ContactContract.DepartmentEntry.COLUMN_PHONE, "024.7306.6666")
            put(ContactContract.DepartmentEntry.COLUMN_EMAIL, "cntt@tlu.edu.vn")
        }
        val deptId1 = db.insert(ContactContract.DepartmentEntry.TABLE_NAME, null, deptValues1)

        val deptValues2 = ContentValues().apply {
            put(ContactContract.DepartmentEntry.COLUMN_NAME, "Phòng Đào tạo")
            put(ContactContract.DepartmentEntry.COLUMN_PHONE, "024.7306.5555")
            put(ContactContract.DepartmentEntry.COLUMN_EMAIL, "daotao@tlu.edu.vn")
        }
        val deptId2 = db.insert(ContactContract.DepartmentEntry.TABLE_NAME, null, deptValues2)

        // Thêm một số liên hệ mẫu
        val contactValues1 = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, "Nguyễn Văn A")
            put(ContactContract.ContactEntry.COLUMN_POSITION, "Trưởng khoa")
            put(ContactContract.ContactEntry.COLUMN_PHONE, "0987654321")
            put(ContactContract.ContactEntry.COLUMN_EMAIL, "nguyenvana@tlu.edu.vn")
            put(ContactContract.ContactEntry.COLUMN_DEPARTMENT_ID, deptId1)
        }
        db.insert(ContactContract.ContactEntry.TABLE_NAME, null, contactValues1)

        val contactValues2 = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, "Trần Thị B")
            put(ContactContract.ContactEntry.COLUMN_POSITION, "Trưởng phòng")
            put(ContactContract.ContactEntry.COLUMN_PHONE, "0123456789")
            put(ContactContract.ContactEntry.COLUMN_EMAIL, "tranthib@tlu.edu.vn")
            put(ContactContract.ContactEntry.COLUMN_DEPARTMENT_ID, deptId2)
        }
        db.insert(ContactContract.ContactEntry.TABLE_NAME, null, contactValues2)
    }

    private fun insertSampleUsers(db: SQLiteDatabase) {
        // Thêm admin
        val adminValues = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_USERNAME, "admin")
            put(UserContract.UserEntry.COLUMN_PASSWORD, "admin")
            put(UserContract.UserEntry.COLUMN_FULLNAME, "Administrator")
            put(UserContract.UserEntry.COLUMN_EMAIL, "admin@example.com")
            put(UserContract.UserEntry.COLUMN_ROLE, "admin")
            put(UserContract.UserEntry.COLUMN_CONTACT_ID, 0) // Admin không liên kết với contact cụ thể
        }
        db.insert(UserContract.UserEntry.TABLE_NAME, null, adminValues)

        // Thêm user1 - liên kết với contact_id = 1
        val user1Values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_USERNAME, "user1")
            put(UserContract.UserEntry.COLUMN_PASSWORD, "password1")
            put(UserContract.UserEntry.COLUMN_FULLNAME, "Nguyễn Văn A")
            put(UserContract.UserEntry.COLUMN_EMAIL, "nguyenvana@tlu.edu.vn")
            put(UserContract.UserEntry.COLUMN_ROLE, "user")
            put(UserContract.UserEntry.COLUMN_CONTACT_ID, 1) // Liên kết với Nguyễn Văn A
        }
        db.insert(UserContract.UserEntry.TABLE_NAME, null, user1Values)

        // Thêm user2 - liên kết với contact_id = 2
        val user2Values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_USERNAME, "user2")
            put(UserContract.UserEntry.COLUMN_PASSWORD, "password2")
            put(UserContract.UserEntry.COLUMN_FULLNAME, "Trần Thị B")
            put(UserContract.UserEntry.COLUMN_EMAIL, "tranthib@tlu.edu.vn")
            put(UserContract.UserEntry.COLUMN_ROLE, "user")
            put(UserContract.UserEntry.COLUMN_CONTACT_ID, 2) // Liên kết với Trần Thị B
        }
        db.insert(UserContract.UserEntry.TABLE_NAME, null, user2Values)
    }

    // Phương thức thêm liên hệ mới
    fun addContact(contact: Contact): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_POSITION, contact.position)
            put(ContactContract.ContactEntry.COLUMN_PHONE, contact.phone)
            put(ContactContract.ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactContract.ContactEntry.COLUMN_DEPARTMENT_ID, contact.departmentId)
            put(ContactContract.ContactEntry.COLUMN_AVATAR_URI, contact.avatarUri)
        }
        return db.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
    }

    // Phương thức thêm đơn vị mới
    fun addDepartment(department: Department): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.DepartmentEntry.COLUMN_NAME, department.name)
            put(ContactContract.DepartmentEntry.COLUMN_PHONE, department.phone)
            put(ContactContract.DepartmentEntry.COLUMN_EMAIL, department.email)
        }
        return db.insert(ContactContract.DepartmentEntry.TABLE_NAME, null, values)
    }

    // Phương thức lấy tất cả liên hệ
    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME,
            ContactContract.ContactEntry.COLUMN_POSITION,
            ContactContract.ContactEntry.COLUMN_PHONE,
            ContactContract.ContactEntry.COLUMN_EMAIL,
            ContactContract.ContactEntry.COLUMN_DEPARTMENT_ID,
            ContactContract.ContactEntry.COLUMN_AVATAR_URI
        )

        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            ContactContract.ContactEntry.COLUMN_NAME
        )

        with(cursor) {
            while (moveToNext()) {
                val contact = getContactFromCursor(this)
                contacts.add(contact)
            }
        }
        cursor.close()

        return contacts
    }

    // Phương thức lấy tất cả đơn vị
    fun getAllDepartments(): List<Department> {
        val departments = mutableListOf<Department>()
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            ContactContract.DepartmentEntry.COLUMN_NAME,
            ContactContract.DepartmentEntry.COLUMN_PHONE,
            ContactContract.DepartmentEntry.COLUMN_EMAIL
        )

        val cursor = db.query(
            ContactContract.DepartmentEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            ContactContract.DepartmentEntry.COLUMN_NAME
        )

        with(cursor) {
            while (moveToNext()) {
                val department = getDepartmentFromCursor(this)
                departments.add(department)
            }
        }
        cursor.close()

        return departments
    }

    // Phương thức tìm kiếm liên hệ
    fun searchContacts(query: String): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = this.readableDatabase
        val selection = "${ContactContract.ContactEntry.COLUMN_NAME} LIKE ? OR " +
                "${ContactContract.ContactEntry.COLUMN_PHONE} LIKE ? OR " +
                "${ContactContract.ContactEntry.COLUMN_EMAIL} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")

        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            ContactContract.ContactEntry.COLUMN_NAME
        )

        with(cursor) {
            while (moveToNext()) {
                val contact = getContactFromCursor(this)
                contacts.add(contact)
            }
        }
        cursor.close()

        return contacts
    }

    // Phương thức tìm kiếm đơn vị
    fun searchDepartments(query: String): List<Department> {
        val departments = mutableListOf<Department>()
        val db = this.readableDatabase
        val selection = "${ContactContract.DepartmentEntry.COLUMN_NAME} LIKE ? OR " +
                "${ContactContract.DepartmentEntry.COLUMN_PHONE} LIKE ? OR " +
                "${ContactContract.DepartmentEntry.COLUMN_EMAIL} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")

        val cursor = db.query(
            ContactContract.DepartmentEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            ContactContract.DepartmentEntry.COLUMN_NAME
        )

        with(cursor) {
            while (moveToNext()) {
                val department = getDepartmentFromCursor(this)
                departments.add(department)
            }
        }
        cursor.close()

        return departments
    }

    // Phương thức xóa liên hệ
    fun deleteContact(contactId: Long): Int {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())
        return db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
    }

    // Phương thức xóa đơn vị
    fun deleteDepartment(departmentId: Long): Int {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(departmentId.toString())
        return db.delete(ContactContract.DepartmentEntry.TABLE_NAME, selection, selectionArgs)
    }

    // Helper method để lấy Contact từ Cursor
    private fun getContactFromCursor(cursor: Cursor): Contact {
        val idIndex = cursor.getColumnIndex(BaseColumns._ID)
        val nameIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_NAME)
        val positionIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_POSITION)
        val phoneIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_PHONE)
        val emailIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_EMAIL)
        val deptIdIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_DEPARTMENT_ID)
        val avatarUriIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_AVATAR_URI)

        val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
        val name = if (nameIndex >= 0) cursor.getString(nameIndex) else ""
        val position = if (positionIndex >= 0) cursor.getString(positionIndex) else ""
        val phone = if (phoneIndex >= 0) cursor.getString(phoneIndex) else ""
        val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""
        val deptId = if (deptIdIndex >= 0) cursor.getLong(deptIdIndex) else 0
        val avatarUri = if (avatarUriIndex >= 0) cursor.getString(avatarUriIndex) else null

        return Contact(id, name, position, phone, email, deptId, avatarUri)
    }

    // Helper method để lấy Department từ Cursor
    private fun getDepartmentFromCursor(cursor: Cursor): Department {
        val idIndex = cursor.getColumnIndex(BaseColumns._ID)
        val nameIndex = cursor.getColumnIndex(ContactContract.DepartmentEntry.COLUMN_NAME)
        val phoneIndex = cursor.getColumnIndex(ContactContract.DepartmentEntry.COLUMN_PHONE)
        val emailIndex = cursor.getColumnIndex(ContactContract.DepartmentEntry.COLUMN_EMAIL)

        val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
        val name = if (nameIndex >= 0) cursor.getString(nameIndex) else ""
        val phone = if (phoneIndex >= 0) cursor.getString(phoneIndex) else ""
        val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""

        return Department(id, name, phone, email)
    }

    // Phương thức cập nhật thông tin liên hệ
    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_POSITION, contact.position)
            put(ContactContract.ContactEntry.COLUMN_PHONE, contact.phone)
            put(ContactContract.ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactContract.ContactEntry.COLUMN_DEPARTMENT_ID, contact.departmentId)
            if (contact.avatarUri != null) {
                put(ContactContract.ContactEntry.COLUMN_AVATAR_URI, contact.avatarUri)
            }
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(contact.id.toString())

        return db.update(ContactContract.ContactEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    // Phương thức cập nhật thông tin đơn vị
    fun updateDepartment(department: Department): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.DepartmentEntry.COLUMN_NAME, department.name)
            put(ContactContract.DepartmentEntry.COLUMN_PHONE, department.phone)
            put(ContactContract.DepartmentEntry.COLUMN_EMAIL, department.email)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(department.id.toString())

        return db.update(ContactContract.DepartmentEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    // Phương thức kiểm tra đăng nhập
//    fun checkUser(username: String, password: String): User? {
//        val db = this.readableDatabase
//        val selection = "${UserContract.UserEntry.COLUMN_USERNAME} = ? AND ${UserContract.UserEntry.COLUMN_PASSWORD} = ?"
//        val selectionArgs = arrayOf(username, password)
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
//            val contactId = if (contactIdIndex >= 0) cursor.getLong(contactIdIndex) else 0
//
//            user = User(id, name, pass, fullname, email, role, contactId)
//        }
//
//        cursor.close()
//        return user
//    }

    // Phương thức kiểm tra đăng nhập
    fun checkUser(username: String, password: String): User? {
        val db = this.readableDatabase

        try {
            // Log để debug
            Log.d("LoginDebug", "Checking user: $username with password: $password")

            val selection = "${UserContract.UserEntry.COLUMN_USERNAME} = ? AND ${UserContract.UserEntry.COLUMN_PASSWORD} = ?"
            val selectionArgs = arrayOf(username, password)

            val cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            // Log số lượng kết quả tìm được
            Log.d("LoginDebug", "Found ${cursor.count} matching users")

            var user: User? = null

            if (cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndex(BaseColumns._ID)
                val usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERNAME)
                val passwordIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)
                val fullnameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_FULLNAME)
                val emailIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL)
                val roleIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ROLE)
                val contactIdIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_CONTACT_ID)

                val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
                val name = if (usernameIndex >= 0) cursor.getString(usernameIndex) else ""
                val pass = if (passwordIndex >= 0) cursor.getString(passwordIndex) else ""
                val fullname = if (fullnameIndex >= 0) cursor.getString(fullnameIndex) else ""
                val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""
                val role = if (roleIndex >= 0) cursor.getString(roleIndex) else "user"
                val contactId = if (contactIdIndex >= 0) cursor.getLong(contactIdIndex) else 0

                user = User(id, name, pass, fullname, email, role, contactId)

                // Log thông tin user tìm được
                Log.d("LoginDebug", "Found user: $fullname with role: $role")
            }

            cursor.close()
            return user
        } catch (e: Exception) {
            Log.e("LoginDebug", "Error checking user: ${e.message}")
            return null
        }
    }

    // Phương thức lấy user theo contact_id
    fun getUserByContactId(contactId: Long): User? {
        val db = this.readableDatabase
        val selection = "${UserContract.UserEntry.COLUMN_CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())

        val cursor = db.query(
            UserContract.UserEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(BaseColumns._ID)
            val usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USERNAME)
            val passwordIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)
            val fullnameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_FULLNAME)
            val emailIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL)
            val roleIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ROLE)
            val contactIdIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_CONTACT_ID)

            val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
            val name = if (usernameIndex >= 0) cursor.getString(usernameIndex) else ""
            val pass = if (passwordIndex >= 0) cursor.getString(passwordIndex) else ""
            val fullname = if (fullnameIndex >= 0) cursor.getString(fullnameIndex) else ""
            val email = if (emailIndex >= 0) cursor.getString(emailIndex) else ""
            val role = if (roleIndex >= 0) cursor.getString(roleIndex) else "user"
            val cId = if (contactIdIndex >= 0) cursor.getLong(contactIdIndex) else 0

            user = User(id, name, pass, fullname, email, role, cId)
        }

        cursor.close()
        return user
    }

    // Phương thức thêm user mới
    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_USERNAME, user.username)
            put(UserContract.UserEntry.COLUMN_PASSWORD, user.password)
            put(UserContract.UserEntry.COLUMN_FULLNAME, user.fullName)
            put(UserContract.UserEntry.COLUMN_EMAIL, user.email)
            put(UserContract.UserEntry.COLUMN_ROLE, user.role)
            put(UserContract.UserEntry.COLUMN_CONTACT_ID, user.contactId)
        }
        return db.insert(UserContract.UserEntry.TABLE_NAME, null, values)
    }

    // Phương thức kiểm tra user đã tồn tại
    fun checkUserExists(username: String): Boolean {
        val db = this.readableDatabase
        val selection = "${UserContract.UserEntry.COLUMN_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        val cursor = db.query(
            UserContract.UserEntry.TABLE_NAME,
            arrayOf(BaseColumns._ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


}
