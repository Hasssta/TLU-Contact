//package com.example.contact_test
//
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.contact_test.database.UserDBHelper
//import com.example.contact_test.model.User
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.checkbox.MaterialCheckBox
//import com.google.android.material.textfield.TextInputEditText
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var usernameEditText: TextInputEditText
//    private lateinit var passwordEditText: TextInputEditText
//    private lateinit var rememberCheckBox: MaterialCheckBox
//    private lateinit var loginButton: MaterialButton
//    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var userDBHelper: UserDBHelper
//
//    companion object {
//        private const val PREF_NAME = "LoginPrefs"
//        private const val KEY_USERNAME = "username"
//        private const val KEY_PASSWORD = "password"
//        private const val KEY_REMEMBER = "remember"
//        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
//        private const val KEY_USER_ROLE = "userRole"
//        private const val KEY_USER_ID = "userId"
//        private const val KEY_USER_CONTACT_ID = "userContactId"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        // Khởi tạo UserDBHelper
//        userDBHelper = UserDBHelper(this)
//
//        // Khởi tạo SharedPreferences
//        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
//
//        // Ánh xạ các view
//        usernameEditText = findViewById(R.id.username_edit_text)
//        passwordEditText = findViewById(R.id.password_edit_text)
//        rememberCheckBox = findViewById(R.id.remember_checkbox)
//        loginButton = findViewById(R.id.login_button)
//
//        // Kiểm tra xem người dùng đã đăng nhập trước đó chưa
//        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
//            navigateToMainActivity()
//            return
//        }
//
//        // Điền thông tin đăng nhập đã lưu (nếu có)
//        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
//            usernameEditText.setText(sharedPreferences.getString(KEY_USERNAME, ""))
//            passwordEditText.setText(sharedPreferences.getString(KEY_PASSWORD, ""))
//            rememberCheckBox.isChecked = true
//        }
//
//        // Xử lý sự kiện click vào nút đăng nhập
//        loginButton.setOnClickListener {
//            val username = usernameEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//
//            if (validateLogin(username, password)) {
//                // Lưu thông tin đăng nhập nếu "Ghi nhớ đăng nhập" được chọn
//                saveLoginInfo(username, password, rememberCheckBox.isChecked)
//
//                // Lấy thông tin người dùng
//                val user = userDBHelper.checkUser(username, password)
//
//                if (user != null) {
//                    // Lưu thông tin người dùng đăng nhập
//                    val editor = sharedPreferences.edit()
//                    editor.putBoolean(KEY_IS_LOGGED_IN, true)
//                    editor.putString(KEY_USER_ROLE, user.role)
//                    editor.putLong(KEY_USER_ID, user.id)
//                    editor.putLong(KEY_USER_CONTACT_ID, user.contactId)
//                    editor.apply()
//
//                    // Chuyển đến MainActivity
//                    navigateToMainActivity()
//                }
//            }
//        }
//
//        // Xử lý sự kiện click vào "Quên mật khẩu"
//        findViewById<TextView>(R.id.forgot_password_text).setOnClickListener {
//            Toast.makeText(this, "Chưa có chức năng này", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun validateLogin(username: String, password: String): Boolean {
//        // Kiểm tra username và password không được để trống
//        if (username.isEmpty()) {
//            usernameEditText.error = "Vui lòng nhập tên đăng nhập"
//            return false
//        }
//
//        if (password.isEmpty()) {
//            passwordEditText.error = "Vui lòng nhập mật khẩu"
//            return false
//        }
//
//        // Kiểm tra thông tin đăng nhập với cơ sở dữ liệu
//        val user = userDBHelper.checkUser(username, password)
//
//        if (user != null) {
//            Toast.makeText(this, "Tên đăng nhập ton tai", Toast.LENGTH_SHORT).show()
//            return true
//        }
//
//        Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
//        return false
//    }
//
//    private fun saveLoginInfo(username: String, password: String, remember: Boolean) {
//        val editor = sharedPreferences.edit()
//        if (remember) {
//            editor.putString(KEY_USERNAME, username)
//            editor.putString(KEY_PASSWORD, password)
//            editor.putBoolean(KEY_REMEMBER, true)
//        } else {
//            editor.remove(KEY_USERNAME)
//            editor.remove(KEY_PASSWORD)
//            editor.putBoolean(KEY_REMEMBER, false)
//        }
//        editor.apply()
//    }
//
//    private fun navigateToMainActivity() {
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish() // Đóng LoginActivity để không thể quay lại bằng nút Back
//    }
//}


package com.example.contact_test

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contact_test.database.ContactDBHelper
import com.example.contact_test.model.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var rememberCheckBox: MaterialCheckBox
    private lateinit var loginButton: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: ContactDBHelper // Thay đổi từ UserDBHelper sang ContactDBHelper

    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER = "remember"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ROLE = "userRole"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_CONTACT_ID = "userContactId"
        private const val TAG = "LoginDebug" // Tag cho debug logs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Khởi tạo ContactDBHelper thay vì UserDBHelper
        dbHelper = ContactDBHelper(this)

        // Debug: Kiểm tra users trong database
        debugCheckUsers()

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // Ánh xạ các view
        usernameEditText = findViewById(R.id.username_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        rememberCheckBox = findViewById(R.id.remember_checkbox)
        loginButton = findViewById(R.id.login_button)

        // Kiểm tra xem người dùng đã đăng nhập trước đó chưa
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToMainActivity()
            return
        }

        // Điền thông tin đăng nhập đã lưu (nếu có)
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            usernameEditText.setText(sharedPreferences.getString(KEY_USERNAME, ""))
            passwordEditText.setText(sharedPreferences.getString(KEY_PASSWORD, ""))
            rememberCheckBox.isChecked = true
        }

        // Xử lý sự kiện click vào nút đăng nhập
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateLogin(username, password)) {
                // Lưu thông tin đăng nhập nếu "Ghi nhớ đăng nhập" được chọn
                saveLoginInfo(username, password, rememberCheckBox.isChecked)

                // Lấy thông tin người dùng
                val user = dbHelper.checkUser(username, password)

                if (user != null) {
                    // Lưu thông tin người dùng đăng nhập
                    val editor = sharedPreferences.edit()
                    editor.putBoolean(KEY_IS_LOGGED_IN, true)
                    editor.putString(KEY_USER_ROLE, user.role)
                    editor.putLong(KEY_USER_ID, user.id)
                    editor.putLong(KEY_USER_CONTACT_ID, user.contactId)
                    editor.apply()

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                    // Chuyển đến MainActivity
                    navigateToMainActivity()
                }
            }
        }

        // Xử lý sự kiện click vào "Quên mật khẩu"
        findViewById<TextView>(R.id.forgot_password_text).setOnClickListener {
            Toast.makeText(this, "Chưa có chức năng này", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // Kiểm tra username và password không được để trống
        if (username.isEmpty()) {
            usernameEditText.error = "Vui lòng nhập tên đăng nhập"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Vui lòng nhập mật khẩu"
            return false
        }

        // Kiểm tra thông tin đăng nhập với cơ sở dữ liệu
        val user = dbHelper.checkUser(username, password)

        if (user != null) {
            Log.d(TAG, "Đăng nhập thành công với user: ${user.fullName}, role: ${user.role}")
            return true
        }

        Log.d(TAG, "Đăng nhập thất bại với username: $username")
        Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun saveLoginInfo(username: String, password: String, remember: Boolean) {
        val editor = sharedPreferences.edit()
        if (remember) {
            editor.putString(KEY_USERNAME, username)
            editor.putString(KEY_PASSWORD, password)
            editor.putBoolean(KEY_REMEMBER, true)
        } else {
            editor.remove(KEY_USERNAME)
            editor.remove(KEY_PASSWORD)
            editor.putBoolean(KEY_REMEMBER, false)
        }
        editor.apply()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Đóng LoginActivity để không thể quay lại bằng nút Back
    }

    // Hàm debug để kiểm tra users trong database
    private fun debugCheckUsers() {
        val db = dbHelper.readableDatabase

        try {
            val cursor = db.query(
                com.example.contact_test.database.UserContract.UserEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            Log.d(TAG, "Tổng số users trong database: ${cursor.count}")

            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(android.provider.BaseColumns._ID)
                val usernameIndex = cursor.getColumnIndex(com.example.contact_test.database.UserContract.UserEntry.COLUMN_USERNAME)
                val passwordIndex = cursor.getColumnIndex(com.example.contact_test.database.UserContract.UserEntry.COLUMN_PASSWORD)
                val fullnameIndex = cursor.getColumnIndex(com.example.contact_test.database.UserContract.UserEntry.COLUMN_FULLNAME)
                val roleIndex = cursor.getColumnIndex(com.example.contact_test.database.UserContract.UserEntry.COLUMN_ROLE)

                val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
                val username = if (usernameIndex >= 0) cursor.getString(usernameIndex) else "unknown"
                val password = if (passwordIndex >= 0) cursor.getString(passwordIndex) else "unknown"
                val fullname = if (fullnameIndex >= 0) cursor.getString(fullnameIndex) else "unknown"
                val role = if (roleIndex >= 0) cursor.getString(roleIndex) else "unknown"

                Log.d(TAG, "User ID: $id, Username: $username, Password: $password, Fullname: $fullname, Role: $role")
            }

            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi debug users: ${e.message}")
        }
    }
}
