package com.example.chatapp.c_listChatUser.viewModelList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.a_authentication.useCase.LogoutUseCase
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList
import com.example.chatapp.c_listChatUser.modelList.ListChatUsers
import com.example.chatapp.c_listChatUser.repository_List.ListChatRepository
import com.example.chatapp.c_listChatUser.stateAndEventList.ListChatEvent
import com.example.chatapp.c_listChatUser.stateAndEventList.ListChatState
import com.example.chatapp.c_listChatUser.useCase.DeleteChatUseCase
import com.example.chatapp.e_messageChatId.b_data.repository_msg.MessageRepository
import com.example.chatapp.mapper.toListChatUsers
import kotlinx.coroutines.Job
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

    private lateinit var currentUserId: String

    private val _listChatState = MutableStateFlow<ListChatState>(ListChatState.Idle)
    val listChatState = _listChatState.asStateFlow()

    private val _chatMessageEvent = MutableSharedFlow<ListChatEvent>()
    val chatMessageEvent = _chatMessageEvent.asSharedFlow()

    private var allChats: List<ListChatUsers> = emptyList()
    fun getAllChat() {
        currentUserId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            chatRepository.getAllChat(currentUserId)
                .onStart {
                    _listChatState.value = ListChatState.Loading
                }
                .catch {
                    _chatMessageEvent.emit(
                        ListChatEvent.Error(it.message ?: "ErrorOnGetAllChat")
                    )
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
            deleteChatUseCase(chatId = chatId)
                .onSuccess {
                    _chatMessageEvent.emit(
                        ListChatEvent.SuccessDeleteChat
                    )
                }
                .onFailure {
                    _chatMessageEvent.emit(
                        ListChatEvent.Error(it.message ?: "Error on delete chat")
                    )
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
                ListChatEvent.NavigationToMessageListChat(
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
        if (filterChats.isEmpty()) {
            _listChatState.value = ListChatState.SearchEmpty(query)
        } else {
            _listChatState.value = ListChatState.Success(filterChats)
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
