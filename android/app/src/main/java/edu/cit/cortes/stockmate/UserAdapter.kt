package edu.cit.cortes.stockmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import edu.cit.cortes.stockmate.model.UserResponse

class UserAdapter(
    private val onStatusToggle: (UserResponse) -> Unit
) : ListAdapter<UserResponse, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText = itemView.findViewById<TextView>(R.id.userName)
        private val emailText = itemView.findViewById<TextView>(R.id.userEmail)
        private val roleText = itemView.findViewById<TextView>(R.id.userRole)
        private val statusText = itemView.findViewById<TextView>(R.id.userStatus)
        private val actionButton = itemView.findViewById<MaterialButton>(R.id.userActionButton)

        fun bind(user: UserResponse) {
            nameText.text = user.name
            emailText.text = user.email
            roleText.text = user.role
            statusText.text = if (user.isActive) "Active" else "Inactive"
            actionButton.text = if (user.isActive) "Deactivate" else "Activate"
            actionButton.isEnabled = user.role != "ADMIN"
            actionButton.setOnClickListener { onStatusToggle(user) }
        }
    }
}

private class UserDiffCallback : DiffUtil.ItemCallback<UserResponse>() {
    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse) = oldItem.userId == newItem.userId
    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse) = oldItem == newItem
}
