package edu.cit.cortes.stockmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.cit.cortes.stockmate.data.AdminRepository
import edu.cit.cortes.stockmate.model.CreateStaffRequest
import edu.cit.cortes.stockmate.model.UserResponse
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AdminRepository = AdminRepository(application.applicationContext)
    private val _uiState = MutableLiveData<AdminUiState>(AdminUiState.Idle)
    val uiState: LiveData<AdminUiState> = _uiState

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            repository.getUsers().fold(
                onSuccess = { _uiState.value = AdminUiState.UsersLoaded(it) },
                onFailure = { _uiState.value = AdminUiState.Error(it.message ?: "Unable to load users.") }
            )
        }
    }

    fun createStaff(request: CreateStaffRequest) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            repository.createStaff(request).fold(
                onSuccess = { _uiState.value = AdminUiState.StaffCreated(it) },
                onFailure = { _uiState.value = AdminUiState.Error(it.message ?: "Failed to create staff.") }
            )
        }
    }

    fun updateStatus(userId: Long, isActive: Boolean) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            repository.updateStatus(userId, isActive).fold(
                onSuccess = { _uiState.value = AdminUiState.StatusUpdated(it) },
                onFailure = { _uiState.value = AdminUiState.Error(it.message ?: "Failed to update status.") }
            )
        }
    }
}

sealed class AdminUiState {
    object Idle : AdminUiState()
    object Loading : AdminUiState()
    data class UsersLoaded(val users: List<UserResponse>) : AdminUiState()
    data class StaffCreated(val user: UserResponse) : AdminUiState()
    data class StatusUpdated(val user: UserResponse) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}
