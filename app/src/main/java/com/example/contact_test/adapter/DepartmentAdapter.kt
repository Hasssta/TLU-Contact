//package com.example.contact_test.adapter
//
//import android.content.Intent
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.example.contact_test.R
//import com.example.contact_test.model.Department
//
//class DepartmentAdapter(
//    private var departments: List<Department>,
//    private val onDepartmentDelete: (Department) -> Unit
//) : RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {
//
//    class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val name: TextView = view.findViewById(R.id.department_name)
//        val phone: TextView = view.findViewById(R.id.department_phone)
//        val email: TextView = view.findViewById(R.id.department_email)
//        val moreButton: ImageButton = view.findViewById(R.id.more_button)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_department, parent, false)
//        return DepartmentViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
//        val department = departments[position]
//
//        holder.name.text = department.name
//        holder.phone.text = department.phone
//        holder.email.text = department.email
//
//        // Xử lý sự kiện click vào phone để gọi điện
//        holder.phone.setOnClickListener {
//            val intent = Intent(Intent.ACTION_DIAL).apply {
//                data = Uri.parse("tel:${department.phone}")
//            }
//            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
//                holder.itemView.context.startActivity(intent)
//            } else {
//                Toast.makeText(
//                    holder.itemView.context,
//                    "Không thể thực hiện cuộc gọi",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//
//        // Xử lý sự kiện click vào email để gửi mail
//        holder.email.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SENDTO).apply {
//                data = Uri.parse("mailto:${department.email}")
//            }
//            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
//                holder.itemView.context.startActivity(intent)
//            } else {
//                Toast.makeText(holder.itemView.context, "Không thể gửi email", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//
//        // Xử lý sự kiện nhấn giữ để xóa đơn vị
//        holder.itemView.setOnLongClickListener {
//            onDepartmentDelete(department)
//            true
//        }
//
//        // Xử lý sự kiện click vào nút more
//        holder.moreButton.setOnClickListener {
//            // Hiển thị menu tùy chọn (có thể dùng PopupMenu)
//            showPopupMenu(holder.moreButton, department)
//        }
//    }
//
//    override fun getItemCount() = departments.size
//
//    // Phương thức cập nhật danh sách khi có thay đổi
//    fun updateDepartments(newDepartments: List<Department>) {
//        departments = newDepartments
//        notifyDataSetChanged()
//    }
//
//    // Hiển thị menu tùy chọn
//    private fun showPopupMenu(view: View, department: Department) {
//        val popup = android.widget.PopupMenu(view.context, view)
//        popup.menuInflater.inflate(R.menu.department_menu, popup.menu)
//
//        popup.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.menu_call -> {
//                    val intent = Intent(Intent.ACTION_DIAL).apply {
//                        data = Uri.parse("tel:${department.phone}")
//                    }
//                    if (intent.resolveActivity(view.context.packageManager) != null) {
//                        view.context.startActivity(intent)
//                    }
//                    true
//                }
//
//                R.id.menu_email -> {
//                    val intent = Intent(Intent.ACTION_SENDTO).apply {
//                        data = Uri.parse("mailto:${department.email}")
//                    }
//                    if (intent.resolveActivity(view.context.packageManager) != null) {
//                        view.context.startActivity(intent)
//                    }
//                    true
//                }
//
//                R.id.menu_delete -> {
//                    onDepartmentDelete(department)
//                    true
//                }
//
//                else -> false
//            }
//        }
//
//        popup.show()
//    }
//}

package com.example.contact_test.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_test.R
import com.example.contact_test.model.Department

class DepartmentAdapter(
    private var departments: List<Department>,
    private val onDepartmentDelete: (Department) -> Unit,
    private val onDepartmentEdit: (Department) -> Unit // Thêm callback cho chức năng sửa
) : RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {

    class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.department_name)
        val phone: TextView = view.findViewById(R.id.department_phone)
        val email: TextView = view.findViewById(R.id.department_email)
        val moreButton: ImageButton = view.findViewById(R.id.more_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_department, parent, false)
        return DepartmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val department = departments[position]

        holder.name.text = department.name
        holder.phone.text = department.phone
        holder.email.text = department.email

        // Xử lý sự kiện click vào phone để gọi điện
        holder.phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${department.phone}")
            }
            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show()
            }
        }

        // Xử lý sự kiện click vào email để gửi mail
        holder.email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${department.email}")
            }
            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Không thể gửi email", Toast.LENGTH_SHORT).show()
            }
        }

        // Xử lý sự kiện click vào item để sửa đơn vị
        holder.itemView.setOnClickListener {
            onDepartmentEdit(department)
        }

        // Xử lý sự kiện click vào nút more
        holder.moreButton.setOnClickListener {
            // Hiển thị menu tùy chọn (có thể dùng PopupMenu)
            showPopupMenu(holder.moreButton, department)
        }
    }

    override fun getItemCount() = departments.size

    // Phương thức cập nhật danh sách khi có thay đổi
    fun updateDepartments(newDepartments: List<Department>) {
        departments = newDepartments
        notifyDataSetChanged()
    }

    // Hiển thị menu tùy chọn
    private fun showPopupMenu(view: View, department: Department) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.department_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_call -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${department.phone}")
                    }
                    if (intent.resolveActivity(view.context.packageManager) != null) {
                        view.context.startActivity(intent)
                    }
                    true
                }
                R.id.menu_email -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${department.email}")
                    }
                    if (intent.resolveActivity(view.context.packageManager) != null) {
                        view.context.startActivity(intent)
                    }
                    true
                }
                R.id.menu_edit -> {
                    onDepartmentEdit(department)
                    true
                }
                R.id.menu_delete -> {
                    onDepartmentDelete(department)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}