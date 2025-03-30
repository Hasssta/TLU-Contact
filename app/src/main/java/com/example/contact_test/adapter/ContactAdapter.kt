//package com.example.contact_test.adapter
//
//import android.content.Intent
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.example.contact_test.R
//import com.example.contact_test.model.Contact
//
//class ContactAdapter(
//    private var contacts: List<Contact>,
//    private val onContactDelete: (Contact) -> Unit
//) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
//
//    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val avatar: ImageView = view.findViewById(R.id.avatar)
//        val name: TextView = view.findViewById(R.id.cb_name)
//        val position: TextView = view.findViewById(R.id.cb_position)
//        val phone: TextView = view.findViewById(R.id.cb_sdt)
//        val email: TextView = view.findViewById(R.id.cb_email)
//        val callButton: ImageButton = view.findViewById(R.id.call_button)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_contact, parent, false)
//        return ContactViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
//        val contact = contacts[position]
//
//        holder.name.text = contact.name
//        holder.position.text = contact.position
//        holder.phone.text = contact.phone
//        holder.email.text = contact.email
//
//        // Xử lý sự kiện click vào nút gọi điện
//        holder.callButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_DIAL).apply {
//                data = Uri.parse("tel:${contact.phone}")
//            }
//            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
//                holder.itemView.context.startActivity(intent)
//            } else {
//                Toast.makeText(holder.itemView.context, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Xử lý sự kiện nhấn giữ để xóa liên hệ
//        holder.itemView.setOnLongClickListener {
//            onContactDelete(contact)
//            true
//        }
//
//        // Xử lý sự kiện click vào email để gửi mail
//        holder.email.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SENDTO).apply {
//                data = Uri.parse("mailto:${contact.email}")
//            }
//            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
//                holder.itemView.context.startActivity(intent)
//            } else {
//                Toast.makeText(holder.itemView.context, "Không thể gửi email", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun getItemCount() = contacts.size
//
//    // Phương thức cập nhật danh sách khi có thay đổi
//    fun updateContacts(newContacts: List<Contact>) {
//        contacts = newContacts
//        notifyDataSetChanged()
//    }
//}

package com.example.contact_test.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.contact_test.R
import com.example.contact_test.model.Contact
import com.google.android.material.imageview.ShapeableImageView

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onContactLongClick: (Contact) -> Unit,
    private val onContactEdit: (Contact) -> Unit // Thêm callback cho chức năng sửa
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ShapeableImageView = view.findViewById(R.id.avatar)
        val name: TextView = view.findViewById(R.id.cb_name)
        val position: TextView = view.findViewById(R.id.cb_position)
        val phone: TextView = view.findViewById(R.id.cb_sdt)
        val email: TextView = view.findViewById(R.id.cb_email)
        val callButton: ImageButton = view.findViewById(R.id.call_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.name.text = contact.name
        holder.position.text = contact.position
        holder.phone.text = contact.phone
        holder.email.text = contact.email

        // Xử lý sự kiện click vào nút gọi điện
        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${contact.phone}")
            }
            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show()
            }
        }

        // Xử lý sự kiện nhấn giữ để xóa liên hệ
        holder.itemView.setOnLongClickListener {
            onContactLongClick(contact)
            true
        }

        // Xử lý sự kiện click vào item để sửa liên hệ
        holder.itemView.setOnClickListener {
            onContactEdit(contact)
        }

        // Xử lý sự kiện click vào email để gửi mail
        holder.email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${contact.email}")
            }
            if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Không thể gửi email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = contacts.size

    // Phương thức cập nhật danh sách khi có thay đổi
    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}