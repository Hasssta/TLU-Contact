package com.example.contact_test

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_test.adapter.ContactAdapter
import com.example.contact_test.adapter.DepartmentAdapter
import com.example.contact_test.database.ContactDBHelper
import com.example.contact_test.model.Contact
import com.example.contact_test.model.Department
import com.example.contact_test.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
//import com.google.firebase.firestore.auth.User

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: ContactDBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var tabLayout: TabLayout

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var departmentAdapter: DepartmentAdapter

    private var contacts: List<Contact> = listOf()
    private var departments: List<Department> = listOf()
    private var currentTab = 0 // 0: Contacts, 1: Departments

    private var userRole: String = "user"
    private var userContactId: Long = 0

    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        userRole = sharedPreferences.getString("userRole", "user") ?: "user"
        userContactId = sharedPreferences.getLong("userContactId", 0)

        // Hiển thị vai trò người dùng trong Toolbar
        val userRoleText = findViewById<TextView>(R.id.user_role_text)
        userRoleText.text = if (userRole == "admin") "Admin" else "User"

        // Xử lý sự kiện click vào nút đăng xuất
        findViewById<ImageButton>(R.id.logout_button).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất") { _, _ ->
                    // Xóa trạng thái đăng nhập
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

                    // Chuyển về màn hình đăng nhập
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        // Khởi tạo database helper
        dbHelper = ContactDBHelper(this)

        // Ánh xạ các view
        recyclerView = findViewById(R.id.recycler_view)
        searchEditText = findViewById(R.id.search_edit_text)
        tabLayout = findViewById(R.id.tab_layout)

        // Thiết lập RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lấy dữ liệu từ database
        loadData()

        // Thiết lập adapter cho RecyclerView
        setupAdapters()

        // Xử lý sự kiện thay đổi tab
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
                updateRecyclerView()
                searchEditText.text.clear()

                // Cập nhật icon của FAB dựa trên tab hiện tại
                val fab = findViewById<FloatingActionButton>(R.id.fab_add_contact)
                if (currentTab == 0) {
                    fab.setImageResource(R.drawable.ic_add_contact)
                    fab.contentDescription = "Thêm liên hệ"
                } else {
                    fab.setImageResource(R.drawable.ic_add_department)
                    fab.contentDescription = "Thêm đơn vị"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Xử lý sự kiện tìm kiếm
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Xử lý sự kiện click vào FAB
        val fabAddContact = findViewById<FloatingActionButton>(R.id.fab_add_contact)
        fabAddContact.setOnClickListener {
            if (currentTab == 0) {
                // Kiểm tra quyền trước khi cho phép thêm liên hệ
                if (userRole == "admin") {
                    showAddContactDialog()
                } else {
                    Toast.makeText(this, "Bạn không có quyền thêm liên hệ mới", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Kiểm tra quyền trước khi cho phép thêm đơn vị
                if (userRole == "admin") {
                    showAddDepartmentDialog()
                } else {
                    Toast.makeText(this, "Bạn không có quyền thêm đơn vị mới", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData() {
        contacts = dbHelper.getAllContacts()
        departments = dbHelper.getAllDepartments()
    }

    private fun setupAdapters() {
        contactAdapter = ContactAdapter(
            contacts,
            { contact ->
                // Kiểm tra quyền xóa liên hệ
                if (userRole == "admin" || contact.id == userContactId) {
                    showDeleteContactDialog(contact)
                } else {
                    Toast.makeText(this, "Bạn không có quyền xóa thông tin này", Toast.LENGTH_SHORT).show()
                }
            },
            { contact ->
                // Chỉ cho phép sửa nếu là admin hoặc là contact của chính người dùng
                if (userRole == "admin" || contact.id == userContactId) {
                    showEditContactDialog(contact)
                } else {
                    Toast.makeText(this, "Bạn không có quyền sửa thông tin này", Toast.LENGTH_SHORT).show()
                }
            }
        )

        departmentAdapter = DepartmentAdapter(
            departments,
            { department ->
                // Kiểm tra quyền xóa đơn vị
                if (userRole == "admin") {
                    showDeleteDepartmentDialog(department)
                } else {
                    Toast.makeText(this, "Bạn không có quyền xóa đơn vị", Toast.LENGTH_SHORT).show()
                }
            },
            { department ->
                // Chỉ admin có quyền sửa đơn vị
                if (userRole == "admin") {
                    showEditDepartmentDialog(department)
                } else {
                    Toast.makeText(this, "Bạn không có quyền sửa thông tin này", Toast.LENGTH_SHORT).show()
                }
            }
        )

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        if (currentTab == 0) {
            recyclerView.adapter = contactAdapter
        } else {
            recyclerView.adapter = departmentAdapter
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            contactAdapter.updateContacts(contacts)
            departmentAdapter.updateDepartments(departments)
        } else {
            if (currentTab == 0) {
                val filteredContacts = dbHelper.searchContacts(query)
                contactAdapter.updateContacts(filteredContacts)
            } else {
                val filteredDepartments = dbHelper.searchDepartments(query)
                departmentAdapter.updateDepartments(filteredDepartments)
            }
        }
    }

//    private fun showAddContactDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
//        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
//        val positionEditText = dialogView.findViewById<EditText>(R.id.edit_position)
//        val phoneEditText = dialogView.findViewById<EditText>(R.id.edit_phone)
//        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_email)
//        val departmentSpinner = dialogView.findViewById<Spinner>(R.id.spinner_department)
//
//        // Thiết lập spinner cho danh sách đơn vị
//        val departmentNames = departments.map { it.name }.toMutableList()
//        departmentNames.add(0, "Chọn đơn vị")
//
//        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departmentNames)
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        departmentSpinner.adapter = spinnerAdapter
//
//        val dialog = AlertDialog.Builder(this)
//            .setView(dialogView)
//            .setPositiveButton("Thêm") { _, _ ->
//                val name = nameEditText.text.toString()
//                val position = positionEditText.text.toString()
//                val phone = phoneEditText.text.toString()
//                val email = emailEditText.text.toString()
//                val departmentPosition = departmentSpinner.selectedItemPosition
//
//                if (name.isNotBlank() && phone.isNotBlank()) {
//                    val departmentId = if (departmentPosition > 0) {
//                        departments[departmentPosition - 1].id
//                    } else {
//                        0
//                    }
//
//                    val newContact = Contact(
//                        name = name,
//                        position = position,
//                        phone = phone,
//                        email = email,
//                        departmentId = departmentId
//                    )
//
//                    dbHelper.addContact(newContact)
//                    loadData()
//                    contactAdapter.updateContacts(contacts)
//
//                    if (currentTab == 0) {
//                        updateRecyclerView()
//                    }
//
//                    Toast.makeText(this, "Đã thêm liên hệ mới", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Hủy", null)
//            .create()
//
//        dialog.show()
//    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
        val positionEditText = dialogView.findViewById<EditText>(R.id.edit_position)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.edit_phone)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_email)
        val departmentSpinner = dialogView.findViewById<Spinner>(R.id.spinner_department)

        // Thiết lập spinner cho danh sách đơn vị
        val departmentNames = departments.map { it.name }.toMutableList()
        departmentNames.add(0, "Chọn đơn vị")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departmentNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        departmentSpinner.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val name = nameEditText.text.toString()
                val position = positionEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()
                val departmentPosition = departmentSpinner.selectedItemPosition

                if (name.isNotBlank() && phone.isNotBlank()) {
                    val departmentId = if (departmentPosition > 0) {
                        departments[departmentPosition - 1].id
                    } else {
                        0
                    }

                    val newContact = Contact(
                        name = name,
                        position = position,
                        phone = phone,
                        email = email,
                        departmentId = departmentId
                    )

                    // Thêm contact mới và lấy ID của contact vừa thêm
                    val contactId = dbHelper.addContact(newContact)

                    if (contactId > 0) {
                        // Kiểm tra xem số điện thoại đã được sử dụng làm username chưa
                        if (!dbHelper.checkUserExists(phone)) {
                            // Tạo user mới với username và password là số điện thoại
                            val newUser = User(
                                username = phone,
                                password = phone,
                                fullName = name,
                                email = email,
                                role = "user",
                                contactId = contactId
                            )

                            // Thêm user mới vào database
                            val userId = dbHelper.addUser(newUser)

                            if (userId > 0) {
                                Toast.makeText(this, "Đã tạo tài khoản với username và password là số điện thoại", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Không thể tạo tài khoản tự động", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Số điện thoại đã được sử dụng làm username", Toast.LENGTH_SHORT).show()
                        }

                        // Cập nhật danh sách contacts
                        loadData()
                        contactAdapter.updateContacts(contacts)

                        if (currentTab == 0) {
                            updateRecyclerView()
                        }

                        Toast.makeText(this, "Đã thêm liên hệ mới", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Không thể thêm liên hệ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }



    private fun showEditContactDialog(contact: Contact) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_contact, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
        val positionEditText = dialogView.findViewById<EditText>(R.id.edit_position)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.edit_phone)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_email)
        val departmentSpinner = dialogView.findViewById<Spinner>(R.id.spinner_department)

        // Điền thông tin hiện tại của liên hệ
        nameEditText.setText(contact.name)
        positionEditText.setText(contact.position)
        phoneEditText.setText(contact.phone)
        emailEditText.setText(contact.email)

        // Thiết lập spinner cho danh sách đơn vị
        val departmentNames = departments.map { it.name }.toMutableList()
        departmentNames.add(0, "Chọn đơn vị")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departmentNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        departmentSpinner.adapter = spinnerAdapter

        // Chọn đơn vị hiện tại trong spinner
        val departmentIndex = departments.indexOfFirst { it.id == contact.departmentId }
        if (departmentIndex >= 0) {
            departmentSpinner.setSelection(departmentIndex + 1) // +1 vì có thêm "Chọn đơn vị" ở vị trí 0
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Cập nhật") { _, _ ->
                val name = nameEditText.text.toString()
                val position = positionEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()
                val departmentPosition = departmentSpinner.selectedItemPosition

                if (name.isNotBlank() && phone.isNotBlank()) {
                    val departmentId = if (departmentPosition > 0) {
                        departments[departmentPosition - 1].id
                    } else {
                        0
                    }

                    val updatedContact = Contact(
                        id = contact.id,
                        name = name,
                        position = position,
                        phone = phone,
                        email = email,
                        departmentId = departmentId,
                        avatarUri = contact.avatarUri
                    )

                    val result = dbHelper.updateContact(updatedContact)
                    if (result > 0) {
                        loadData()
                        contactAdapter.updateContacts(contacts)
                        Toast.makeText(this, "Đã cập nhật liên hệ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Không thể cập nhật liên hệ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }

    private fun showAddDepartmentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_department, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_department_name)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.edit_department_phone)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_department_email)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()

                if (name.isNotBlank()) {
                    val newDepartment = Department(
                        name = name,
                        phone = phone,
                        email = email
                    )

                    dbHelper.addDepartment(newDepartment)
                    loadData()
                    departmentAdapter.updateDepartments(departments)

                    if (currentTab == 1) {
                        updateRecyclerView()
                    }

                    Toast.makeText(this, "Đã thêm đơn vị mới", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Vui lòng nhập tên đơn vị", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }

    private fun showEditDepartmentDialog(department: Department) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_department, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_department_name)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.edit_department_phone)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_department_email)

        // Điền thông tin hiện tại của đơn vị
        nameEditText.setText(department.name)
        phoneEditText.setText(department.phone)
        emailEditText.setText(department.email)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Cập nhật") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()

                if (name.isNotBlank()) {
                    val updatedDepartment = Department(
                        id = department.id,
                        name = name,
                        phone = phone,
                        email = email
                    )

                    val result = dbHelper.updateDepartment(updatedDepartment)
                    if (result > 0) {
                        loadData()
                        departmentAdapter.updateDepartments(departments)
                        Toast.makeText(this, "Đã cập nhật đơn vị", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Không thể cập nhật đơn vị", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Vui lòng nhập tên đơn vị", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }

    private fun showDeleteContactDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Xóa liên hệ")
            .setMessage("Bạn có chắc muốn xóa liên hệ ${contact.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                dbHelper.deleteContact(contact.id)
                loadData()
                contactAdapter.updateContacts(contacts)
                Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDeleteDepartmentDialog(department: Department) {
        AlertDialog.Builder(this)
            .setTitle("Xóa đơn vị")
            .setMessage("Bạn có chắc muốn xóa đơn vị ${department.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                dbHelper.deleteDepartment(department.id)
                loadData()
                departmentAdapter.updateDepartments(departments)
                Toast.makeText(this, "Đã xóa đơn vị", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền gọi điện", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Quyền gọi điện bị từ chối", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
