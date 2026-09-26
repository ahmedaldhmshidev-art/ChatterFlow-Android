package com.example.chatapp.e_messageChatId.viewModel_msg


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.e_messageChatId.Error.MessageErrorMapper
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.c_domain.seen.SeenObserver
import com.example.chatapp.e_messageChatId.c_domain.useCase.DeletedMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.EditedMessageUseCase
import com.example.chatapp.e_messageChatId.stateAndEvent_msg.ChatScreenState
import com.example.chatapp.e_messageChatId.stateAndEvent_msg.SendState
import com.example.chatapp.e_messageChatId.c_domain.useCase.GetAllMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.OpenChatUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.RemoveAllMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.SendMsgUseCase
import com.example.chatapp.e_messageChatId.stateAndEvent_msg.MessageEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageViewModel(
    private val getAllMessageUseCase: GetAllMessageUseCase,
    private val sendMessageUseCase: SendMsgUseCase,
    private val openChatUseCase: OpenChatUseCase,
    private val seenObserver: SeenObserver,
    private val editedMessageUseCase: EditedMessageUseCase,
    private val deletedMessageUseCase: DeletedMessageUseCase,
    private val removeAllMessageUseCase: RemoveAllMessageUseCase,

    ) : ViewModel() {

    private val _event = MutableSharedFlow<MessageEvent>()
    val event = _event.asSharedFlow()


//    private var currentUserId: String? = null

    private val _chatState = MutableStateFlow(ChatScreenState())
    val chatState = _chatState.asStateFlow()
    private var messageJob: Job? = null

    fun openChat(chatId: String, currentUserId: String, otherUserId: String) {
//        this.currentUserId = currentUserId
        messageJob?.cancel()
        messageJob = viewModelScope.launch {

            _chatState.update { it.copy(isLoading = true) }
            try {
                seenObserver.start(chatId = chatId)

                val toolbar = openChatUseCase(
                    chatId = chatId, currentUserId = currentUserId, otherUserId = otherUserId
                ) // return userById

                getAllMessageUseCase(chatId).collect { getMsg ->
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            toolbarUser = toolbar,
                            messages = getMsg,
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Message_ViewModel", "Message_ViewModel Failed to open chat", e)

                _chatState.update {
                    it.copy(
                        isLoading = false
                    )
                }
                _event.emit(
                    MessageEvent.ShowError(
                        messageError = MessageErrorMapper.map(e)
                    )
                )
            }
        }
    }

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState = _sendState.asStateFlow()

    fun sendMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        messageText: String
    ) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            try {
                sendMessageUseCase(
                    chatId = chatId,
                    senderId = senderId,
                    receiverId = receiverId,
                    msgText = messageText
                )
                _sendState.value = SendState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Message_ViewModel", "Message_ViewModel Failed to send message chat", e)
                _sendState.value = SendState.Idle
                _event.emit(
                    MessageEvent.ShowError(
                        messageError = MessageErrorMapper.map(e)
                    )
                )
            }
        }
    }

    // edited message
    fun editMessage(message: MessageText, newText: String) {
        viewModelScope.launch {
            try {
                editedMessageUseCase(message = message, newText = newText)
                _event.emit(
                    MessageEvent.EditSuccess
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Message_ViewModel", "Message_ViewModel Failed to edit message chat", e)
                _event.emit(
                    MessageEvent.ShowError(
                        messageError = MessageErrorMapper.map(e)
                    )
                )
            }
        }
    }

    // deleted message
    fun deleteMessage(message: MessageText) {
        viewModelScope.launch {
            try {
                deletedMessageUseCase(message = message)
                _event.emit(
                    MessageEvent.DeleteSuccess
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Message_ViewModel", "Message_ViewModel Failed to delete message chat", e)
                _event.emit(
                    MessageEvent.ShowError(
                        MessageErrorMapper.map(e)
                    )
                )
            }
        }
    }

    // copy message
    fun copyMessage(text: String) {
        viewModelScope.launch {
            _event.emit(MessageEvent.CopyMessage(copyText = text))
        }
    }

    // remove all message
    fun removeAllMessage(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                removeAllMessageUseCase(
                    chatId = chatId, currentUserId = currentUserId
                )
                _event.emit(MessageEvent.RemoveAllMessageSuccess)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Message_ViewModel", "Message_ViewModel Failed to removeAllMessage ", e)
                _event.emit(
                    MessageEvent.ShowError(
                        messageError = MessageErrorMapper.map(e)
                    )
                )
            }
        }
    }


    fun onClose() {
        seenObserver.stop()
    }

    override fun onCleared() {
        messageJob?.cancel()
//        seenObserver.destroy()
        seenObserver.stop()
        super.onCleared()
    }
}

class MessageViewModelFactory(
    private val getAllMessageUseCase: GetAllMessageUseCase,
    private val sendMessageUseCase: SendMsgUseCase,
    private val openChatUseCase: OpenChatUseCase,
    private val seenObserver: SeenObserver,
    private val editedMessageUseCase: EditedMessageUseCase,
    private val deletedMessageUseCase: DeletedMessageUseCase,
    private val removeAllMessageUseCase: RemoveAllMessageUseCase,


    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(
                getAllMessageUseCase = getAllMessageUseCase,
                openChatUseCase = openChatUseCase,
                seenObserver = seenObserver,
                sendMessageUseCase = sendMessageUseCase,
                editedMessageUseCase = editedMessageUseCase,
                deletedMessageUseCase = deletedMessageUseCase,
                removeAllMessageUseCase = removeAllMessageUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}
