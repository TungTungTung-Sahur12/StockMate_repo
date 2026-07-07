package edu.cit.cortes.stockmate

import android.content.Context

class SessionManager private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("stockmate_session", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUserRole(role: String) {
        prefs.edit().putString(KEY_ROLE, role).apply()
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_ROLE, null)
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_ROLE = "user_role"
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
