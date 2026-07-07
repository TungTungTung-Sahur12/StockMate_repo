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
import edu.cit.cortes.stockmate.model.LoginRequest
import edu.cit.cortes.stockmate.viewmodel.AuthUiState
import edu.cit.cortes.stockmate.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        val registerLink = findViewById<MaterialButton>(R.id.registerLink)

        emailInput.doOnTextChanged { text, _, _, _ ->
            emailLayout.error = null
        }
        passwordInput.doOnTextChanged { _, _, _, _ ->
            passwordLayout.error = null
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(LoginRequest(email, password))
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        authViewModel.uiState.observe(this) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    loginButton.isEnabled = false
                    loginButton.text = "Logging in..."
                }
                is AuthUiState.LoginSuccess -> {
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                    SessionManager.getInstance(this).apply {
                        saveAuthToken(state.authResponse.token)
                        saveUserRole(state.authResponse.role)
                    }
                    Toast.makeText(this, "Welcome ${state.authResponse.name}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java).apply {
                        putExtra("name", state.authResponse.name)
                        putExtra("role", state.authResponse.role)
                    }
                    startActivity(intent)
                    finish()
                }
                is AuthUiState.Error -> {
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }
}
