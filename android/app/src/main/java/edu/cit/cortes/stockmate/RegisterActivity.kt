package edu.cit.cortes.stockmate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.cortes.stockmate.model.RegisterRequest
import edu.cit.cortes.stockmate.viewmodel.AuthUiState
import edu.cit.cortes.stockmate.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameLayout = findViewById<TextInputLayout>(R.id.nameLayout)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val confirmPasswordLayout = findViewById<TextInputLayout>(R.id.confirmPasswordLayout)
        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirmPasswordInput)
        val registerButton = findViewById<MaterialButton>(R.id.registerButton)
        val loginLink = findViewById<MaterialButton>(R.id.loginLink)

        listOf(nameInput, emailInput, passwordInput, confirmPasswordInput).forEach { input ->
            input.doOnTextChanged { _, _, _, _ ->
                when (input.id) {
                    R.id.nameInput -> nameLayout.error = null
                    R.id.emailInput -> emailLayout.error = null
                    R.id.passwordInput -> passwordLayout.error = null
                    R.id.confirmPasswordInput -> confirmPasswordLayout.error = null
                }
            }
        }

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                passwordLayout.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordLayout.error = "Passwords do not match"
                return@setOnClickListener
            }

            authViewModel.register(RegisterRequest(name, email, password, confirmPassword))
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        authViewModel.uiState.observe(this) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    registerButton.isEnabled = false
                    registerButton.text = "Creating account..."
                }
                is AuthUiState.RegistrationSuccess -> {
                    registerButton.isEnabled = true
                    registerButton.text = "Register"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is AuthUiState.Error -> {
                    registerButton.isEnabled = true
                    registerButton.text = "Register"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }
}
