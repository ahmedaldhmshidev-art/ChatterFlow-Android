package com.example.chatapp.d_chat_Document.viewModel_document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.example.chatapp.d_chat_Document.repository_document.ChatDocumentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatDocumentInfoViewModel(
    private val repository: ChatDocumentRepository,
    private val sessionManager: SessionManager
):ViewModel() {

    fun createChatDocumentIfNotExist(
        chatId: String , participants:List<String>
    ){
        viewModelScope.launch {
            repository.createChatDocumentIfNotExist(
                chatId= chatId , participants = participants
            )
        }
    }



    private val _chatDocument = MutableStateFlow<ChatDocument?>(null)
    private val chatDocument = _chatDocument. asStateFlow()

    fun getAllChatDocument(chatId:String) {
        viewModelScope.launch {
            repository.getAllChatDocument(chatId).collect { chat ->
                _chatDocument.value = chat
            }
        }
    }
        val typingText: StateFlow<String> =
            chatDocument.map { chat ->
                val otherUserId = chat?.participants?.firstOrNull { it != sessionManager.currentUserId }

                val isTyping = chat?.typingUsers?.get(otherUserId) == true

                if (isTyping)
                    "typing..."
                else ""
            }.stateIn(
                viewModelScope, SharingStarted
                    .WhileSubscribed(5000) // يشتغل فقط اذا UI يراقب  ويتوقف بعد 5 ثواني من المراقية من UI
                , ""
            )


    // typing
    private var typingJob: Job? = null
    fun onTyping(chatId: String , userId:String) {
        typingJob?.cancel()

        typingJob = viewModelScope.launch {
            repository.updateTyping(
                chatId = chatId, userId = userId, isTyping = true
            )
            delay(2000)
            // اذا انتهت المهله ولم يتم كتابة حرف نرسل false
            repository.updateTyping(chatId, userId, false)
        }
    }

    // هذه نستدعيها عند اضغط عل ارسال او الخروج من المحادثة
    fun stopOnTyping(chatId: String , userId: String){
        typingJob?.cancel()
        viewModelScope.launch {
            repository.updateTyping(
                chatId,userId,false
            )
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
    private val sessionManager: SessionManager

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ChatDocumentInfoViewModel ::class.java)) {
            return ChatDocumentInfoViewModel(
                repository = repository,
                sessionManager = sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}