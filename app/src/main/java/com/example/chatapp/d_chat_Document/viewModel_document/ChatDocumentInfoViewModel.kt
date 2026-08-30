package com.example.chatapp.d_chat_Document.viewModel_document

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.d_chat_Document.utiles.chatDocumentError.ChatDocumentError
import com.example.chatapp.d_chat_Document.utiles.chatDocumentError.ChatDocumentErrorMapper
import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.example.chatapp.d_chat_Document.repository_document.ChatDocumentRepository
import com.example.chatapp.d_chat_Document.utiles.uesCase.InitializeChatUseCase
import com.example.chatapp.d_chat_Document.utiles.uiStateEvent.ChatDocumentEvent
import com.example.chatapp.d_chat_Document.utiles.uiStateEvent.ChatDocumentState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatDocumentInfoViewModel(
    private val repository: ChatDocumentRepository,
    private val initializeChatUseCase: InitializeChatUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow<ChatDocumentState>(ChatDocumentState.Idle)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ChatDocumentEvent>()
    val event = _event.asSharedFlow()

    fun createChatDocumentIfNotExist(
        chatId: String, participants: List<String>
    ) {
        viewModelScope.launch {
            _state.value = ChatDocumentState.Loading
            try {
                initializeChatUseCase(chatId, participants)
                _state.value = ChatDocumentState.Success
            } catch (e: Throwable) {
                _state.value = ChatDocumentState.Error(ChatDocumentErrorMapper.map(e))
                Log.d("enexlkjhgfghjkl", "$e")

            }
        }
    }


    private val _chatDocument = MutableStateFlow<ChatDocument?>(null)
    private val chatDocument = _chatDocument.asStateFlow()

    fun getChatDocument(chatId: String) {
        viewModelScope.launch {
            repository.getChatDocument(chatId)
                .catch { throwable ->
                    _event.emit(ChatDocumentEvent.Error(ChatDocumentErrorMapper.map(throwable)))
                }
                .collect { chat ->
                    _chatDocument.value = chat
                }
        }
    }


    val isOtherUserTyping: StateFlow<Boolean> =
        chatDocument.map { chat ->
            val currentUserId = sessionManager.currentUserId
            val otherUserId = chat?.participants?.firstOrNull { it != currentUserId }

            chat?.typingUsers?.get(otherUserId) == true
        }
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(5000), false
            )

    // typing
    private var typingJob: Job? = null
    fun onTyping(chatId: String) {

        val currentUserId = sessionManager.currentUserId ?: run {
            _state.value = ChatDocumentState.Error(ChatDocumentError.NoSession)
            return
        }

        typingJob?.cancel()

        typingJob = viewModelScope.launch {
            try {
                repository.updateTyping(
                    chatId = chatId,
                    userId = currentUserId,
                    isTyping = true
                )
                delay(2000)

                repository.updateTyping(
                    chatId = chatId,
                    userId = currentUserId,
                    isTyping = false
                )
            } catch (e: Throwable) {
                _event.emit(ChatDocumentEvent.Error(ChatDocumentErrorMapper.map(e)))
            }
        }
    }

    // هذه نستدعيها عند اضغط عل ارسال او الخروج من المحادثة
    fun stopOnTyping(chatId: String) {
        val currentUserId = sessionManager.currentUserId ?: run {
            _state.value = ChatDocumentState.Error(ChatDocumentError.NoSession)
            return
        }
        typingJob?.cancel()
        viewModelScope.launch {
            try {
                repository.updateTyping(
                    chatId = chatId,
                    userId = currentUserId,
                    isTyping = false
                )
            } catch (e: Throwable) {
                _event.emit(ChatDocumentEvent.Error(ChatDocumentErrorMapper.map(e)))
            }
        }
    }

    // هذه تستدعيها عند موت viewModel فيها نكنسل اي مهلة موقتة باقيه لم تتوقف
    override fun onCleared() {
        typingJob?.cancel()
        super.onCleared()
    }
}


class ChatDocumentViewModelFactory(
    private val repository: ChatDocumentRepository,
    private val initializeChatUseCase: InitializeChatUseCase,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDocumentInfoViewModel::class.java)) {
            return ChatDocumentInfoViewModel(
                repository = repository,
                initializeChatUseCase,
                sessionManager = sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}