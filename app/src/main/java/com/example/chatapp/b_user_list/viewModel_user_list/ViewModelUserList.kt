package com.example.chatapp.b_user_list.viewModel_user_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList
import com.example.chatapp.b_user_list.uiState_event.EventUserList
import com.example.chatapp.b_user_list.uiState_event.StateUserList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ViewModelUserList(
    private val repository: RepositoryUserList,
    private val sessionManager: SessionManager
) : ViewModel() {


    private val _stateUserList = MutableStateFlow<StateUserList>(StateUserList.Idle)
    val stateUserList = _stateUserList.asStateFlow()

    private val _eventUserList = MutableSharedFlow<EventUserList>()
    val eventUserList = _eventUserList.asSharedFlow()


    private var allUsers: List<User> = emptyList()

    fun gatAllUsers() {
        val currentUserId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            repository.getAllUser(currentUserId = currentUserId)
                .onStart {
                    _stateUserList.value = StateUserList.Loading
                }.catch {
                    _stateUserList.value = StateUserList.Error(it.message ?: "error getAllUser")
                }
                .collect { user ->
                    allUsers = user

                    if (user.isNotEmpty()) {
                        _stateUserList.value = StateUserList.Success(users = user)
                    }
                    if (user.isEmpty()) {
                        _stateUserList.value = StateUserList.Empty
                    }
                }
        }
    }

    fun onUserClick(user: User) {
        viewModelScope.launch {
            _eventUserList.emit(EventUserList.NavigationToMessageUserList(user))
        }
    }


    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _stateUserList.value =
                if (allUsers.isEmpty()) {
                    StateUserList.Empty
                } else {
                    StateUserList.Success(allUsers)
                }
            return
        }
        val filterUser = allUsers.filter {
            it.name.contains(query, ignoreCase = true)
        }
        if (filterUser.isEmpty()) {
            _stateUserList.value = StateUserList.SearchEmpty(query)
        } else {
            _stateUserList.value = StateUserList.Success(filterUser)
        }
    }

}


class UserListViewModelFactory(
    private val repository: RepositoryUserList,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelUserList::class.java))
            return ViewModelUserList(
                repository = repository,
                sessionManager = sessionManager
            ) as T

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}
