package com.example.chatapp.c_listChatUser.ui_List

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.a_authentication.useCase.LogoutUseCase
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList
import com.example.chatapp.c_listChatUser.utiles.listChatError.ListChatError
import com.example.chatapp.c_listChatUser.utiles.listChatError.ListChatErrorMapper
import com.example.chatapp.c_listChatUser.modelList.ListChatUsers
import com.example.chatapp.c_listChatUser.repository_List.ListChatRepository
import com.example.chatapp.c_listChatUser.utiles.stateAndEventList.ListChatEvent
import com.example.chatapp.c_listChatUser.utiles.stateAndEventList.ListChatState
import com.example.chatapp.c_listChatUser.repository_List.useCase.DeleteChatUseCase
import com.example.chatapp.mapper.toListChatUsers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ListChatViewModel(
    private val chatRepository: ListChatRepository,
    private val userRepository: RepositoryUserList,
    private val sessionManager: SessionManager,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {


    private val _listChatState = MutableStateFlow<ListChatState>(ListChatState.Idle)
    val listChatState = _listChatState.asStateFlow()

    private val _chatMessageEvent = MutableSharedFlow<ListChatEvent>()
    val chatMessageEvent = _chatMessageEvent.asSharedFlow()

    private var allChats: List<ListChatUsers> = emptyList()
    fun getAllChat() {
        val currentUserId = sessionManager.currentUserId ?: run {
            _listChatState.value = ListChatState.Error(ListChatError.NoSession)
            return
        }
        viewModelScope.launch {
            chatRepository.getAllChat(currentUserId)
                .onStart {
                    _listChatState.value = ListChatState.Loading
                }
                .catch { throwable ->
                    _listChatState.value = ListChatState.Error(ListChatErrorMapper.map(throwable))
                }
                .collect { chats ->
                    val items = chats.map { chat ->
                        val otherUserId = chat.participants.first { it != currentUserId }
                        val user = userRepository.getUserById(otherUserId)
                        chat.toListChatUsers(
                            user = user ?: User(), currentUserId = currentUserId
                        )
                    }
                    allChats = items

                    if (items.isNotEmpty()) {
                        _listChatState.value = ListChatState.Success(items)
                    } else {
                        _listChatState.value = ListChatState.Empty
                    }
                }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            try {
                deleteChatUseCase(chatId)
                _chatMessageEvent.emit(ListChatEvent.DeleteChatSuccess)
            } catch (e: Throwable) {
                _chatMessageEvent.emit(ListChatEvent.ShowError(ListChatErrorMapper.map(e)))
            }
        }
    }

    fun logout() {
        logoutUseCase()
        viewModelScope.launch {
            _chatMessageEvent.emit(ListChatEvent.NavigationToLogin)
        }
    }

    fun onUserClick(user: ListChatUsers) {
        viewModelScope.launch {
            _chatMessageEvent.emit(
                ListChatEvent.OpenChatMessage(
                    userUid = user.userId
                )
            )
        }
    }

    fun searchChats(query: String) {

        if (query.isBlank()) {
            _listChatState.value =
                if (allChats.isEmpty()) {
                    ListChatState.Empty
                } else {
                    ListChatState.Success(allChats)
                }
            return
        }

        val filterChats = allChats.filter {
            it.userName.contains(query, ignoreCase = true)
        }
        _listChatState.value = if (filterChats.isEmpty()) {
            ListChatState.SearchEmpty(query)
        } else {
            ListChatState.Success(filterChats)
        }
    }
}


class ListChatViewModelFactory(
    private val chatRepository: ListChatRepository,
    private val userRepository: RepositoryUserList,
    private val sessionManager: SessionManager,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val logoutUseCase: LogoutUseCase


) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListChatViewModel::class.java)) {

            return ListChatViewModel(
                chatRepository = chatRepository,
                userRepository = userRepository,
                sessionManager = sessionManager,
                deleteChatUseCase = deleteChatUseCase,
                logoutUseCase = logoutUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}
