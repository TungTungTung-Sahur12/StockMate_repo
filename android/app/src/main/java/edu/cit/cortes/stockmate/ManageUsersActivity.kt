package edu.cit.cortes.stockmate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.cortes.stockmate.model.CreateStaffRequest
import edu.cit.cortes.stockmate.model.UserResponse
import edu.cit.cortes.stockmate.viewmodel.AdminUiState
import edu.cit.cortes.stockmate.viewmodel.AdminViewModel

class ManageUsersActivity : AppCompatActivity() {

    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        val nameLayout = findViewById<TextInputLayout>(R.id.nameLayout)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val createButton = findViewById<MaterialButton>(R.id.createButton)
        val backToDashboard = findViewById<MaterialButton>(R.id.backToDashboard)
        val recyclerView = findViewById<RecyclerView>(R.id.userRecyclerView)

        userAdapter = UserAdapter { user ->
            adminViewModel.updateStatus(user.userId, !user.isActive)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

        createButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            adminViewModel.createStaff(CreateStaffRequest(name, email, password))
        }

        adminViewModel.uiState.observe(this) { state ->
            when (state) {
                is AdminUiState.Loading -> createButton.isEnabled = false
                is AdminUiState.UsersLoaded -> {
                    createButton.isEnabled = true
                    userAdapter.submitList(state.users)
                }
                is AdminUiState.StaffCreated -> {
                    createButton.isEnabled = true
                    Toast.makeText(this, "Staff account created", Toast.LENGTH_SHORT).show()
                    nameInput.text?.clear()
                    emailInput.text?.clear()
                    passwordInput.text?.clear()
                    adminViewModel.loadUsers()
                }
                is AdminUiState.StatusUpdated -> {
                    createButton.isEnabled = true
                    Toast.makeText(this, "Staff status updated", Toast.LENGTH_SHORT).show()
                    adminViewModel.loadUsers()
                }
                is AdminUiState.Error -> {
                    createButton.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

        backToDashboard.setOnClickListener {
            finish()
        }
        adminViewModel.loadUsers()
    }
}
