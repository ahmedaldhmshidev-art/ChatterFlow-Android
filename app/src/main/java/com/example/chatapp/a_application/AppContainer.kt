package com.example.chatapp.a_application

import android.content.Context
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_authentication.repositoryAuth.AuthRepository
import com.example.chatapp.a_authentication.useCase.CheckSessionUseCase
import com.example.chatapp.a_authentication.useCase.LoginUseCase
import com.example.chatapp.a_authentication.useCase.LogoutUseCase
import com.example.chatapp.a_authentication.useCase.RegisterUseCase
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.ChangePasswordUseCase
import com.example.chatapp.c_listChatUser.repository_List.ListChatRepository
import com.example.chatapp.d_chat_Document.repository_document.ChatDocumentRepository
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.GetAccountCurrentUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.UpdateInfoAccountUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting.AppSetting
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting.PreferencesManager
import com.example.chatapp.c_listChatUser.repository_List.useCase.DeleteChatUseCase
import com.example.chatapp.d_chat_Document.utiles.uesCase.InitializeChatUseCase
import com.example.chatapp.e_messageChatId.c_domain.delivered.DeliveredObserver
import com.example.chatapp.e_messageChatId.c_domain.seen.SeenObserver
import com.example.chatapp.mapper.StatusManager
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.UnreadManager
import com.example.chatapp.e_messageChatId.b_data.repository_msg.MessageRepository
import com.example.chatapp.e_messageChatId.b_data.repository_msg.SendMessageBatch
import com.example.chatapp.e_messageChatId.b_data.repository_msg.UpdateChatDocumentLastMsg
import com.example.chatapp.e_messageChatId.c_domain.delivered.DeliveredMsgUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.GetAllMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.IncreaseUnreadUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.OpenChatUseCase
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.ResetUnreadUseCase
import com.example.chatapp.e_messageChatId.c_domain.seen.SeenMsgUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.DeletedMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.EditedMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.RemoveAllMessageUseCase
import com.example.chatapp.e_messageChatId.c_domain.useCase.SendMsgUseCase
import com.example.chatapp.e_messageChatId.info.useCase.GetOtherUserUseCase
import com.example.chatapp.e_messageChatId.viewModel_msg.SyncPipelineMsg
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {
    // firebase
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // session
    val sessionManager: SessionManager by lazy {
        SessionManager(firebaseAuth)
    }

    // Repository
    private val messageRepository: MessageRepository by lazy { // msgRepo
        MessageRepository(firestore)
    }

    val userRepository: RepositoryUserList by lazy { // userListRepo
        RepositoryUserList(firestore)
    }
    val chatDocumentRepository: ChatDocumentRepository by lazy {
        ChatDocumentRepository(firestore = firestore)
    }

    val listChatRepository: ListChatRepository by lazy {
        ListChatRepository(firestore = firestore)
    }


    private val authRepository: AuthRepository by lazy {
        AuthRepository(
            auth = firebaseAuth,
            firestore = firestore,
        )
    }

    // managers
    private val statusManager: StatusManager by lazy {
        StatusManager()
    }

    private val unreadManager: UnreadManager by lazy {
        UnreadManager(firestore = firestore)
    }

    private val updateChatDocumentLastMsg: UpdateChatDocumentLastMsg by lazy {
        UpdateChatDocumentLastMsg(firestore = firestore)
    }

    // sync pipeline
    private val syncPipelineMsg: SyncPipelineMsg by lazy {
        SyncPipelineMsg(
            messageRepository = messageRepository,
            updateChatDocumentLastMsg = updateChatDocumentLastMsg,
            statusManager = statusManager,
            sendMessageBatch
        )
    }

    // userCases
    val sendMsgUseCase: SendMsgUseCase by lazy {
        SendMsgUseCase(
            pipelineMsg = syncPipelineMsg
        )
    }
    private val deliveredMsgUseCase: DeliveredMsgUseCase by lazy {
        DeliveredMsgUseCase(
            pipelineMsg = syncPipelineMsg
        )
    }

    private val seenMsgUseCase: SeenMsgUseCase by lazy {
        SeenMsgUseCase(
            pipelineMsg = syncPipelineMsg,
        )
    }
    val getAllMessageUseCase: GetAllMessageUseCase by lazy {
        GetAllMessageUseCase(
            msgRepository = messageRepository
        )
    }
    val openChatUseCase: OpenChatUseCase by lazy {
        OpenChatUseCase(
            msgRepository = messageRepository,
            userRepository = userRepository,
            seenMsgUseCase = seenMsgUseCase,
            resetUnreadUseCase = resetUnreadUseCase
        )
    }
    private val increaseUnreadUseCase by lazy {
        IncreaseUnreadUseCase(
            unreadManager = unreadManager
        )
    }
    private val resetUnreadUseCase by lazy {
        ResetUnreadUseCase(
            unreadManager = unreadManager
        )
    }
    val removeAllMessageUseCase by lazy {
        RemoveAllMessageUseCase(
            messageRepository, updateChatDocumentLastMsg, resetUnreadUseCase
        )
    }
    val getAccountCurrentUseCase by lazy {
        GetAccountCurrentUseCase(repository = userRepository, sessionManager = sessionManager)
    }
    val deleteChatUseCase by lazy {
        DeleteChatUseCase(listChatRepository)
    }

    val preferencesManager by lazy {
        PreferencesManager(context.applicationContext)
    }


    // observer
    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private val deliveredObserver by lazy {
        DeliveredObserver(
            fireStore = firestore,
            scope = applicationScope,
            sessionManager = sessionManager,
            deliveredMsgUseCase = deliveredMsgUseCase
        )
    }

    fun startObserverDelivered() {
        deliveredObserver.start()
    }

    fun stopObserverDelivered() {
        deliveredObserver.stop()
    }

    val seenObserver: SeenObserver by lazy {
        SeenObserver(
            fireStore = firestore,
            scope = applicationScope,
            sessionManager = sessionManager,
            seenMsgUseCase = seenMsgUseCase,
            resetUnreadUseCase = resetUnreadUseCase
        )
    }

    //useCase auth
    val loginUseCase by lazy {
        LoginUseCase(authRepository)
    }
    val registerUseCase by lazy {
        RegisterUseCase(authRepository)
    }
    val checkSessionUseCase by lazy {
        CheckSessionUseCase(sessionManager)
    }
    val editedMessageUseCase by lazy {
        EditedMessageUseCase(pipelineMsg = syncPipelineMsg)
    }
    val deletedMessageUseCase by lazy {
        DeletedMessageUseCase(pipelineMsg = syncPipelineMsg)
    }
    val getOtherUserUseCase by lazy {
        GetOtherUserUseCase(userRepository)
    }
    val updateInfoAccountUseCase by lazy {
        UpdateInfoAccountUseCase(sessionManager, userRepository)
    }
    val changePasswordUseCase by lazy {
        ChangePasswordUseCase(authRepository)
    }
    val initializeChatUseCase by lazy {
        InitializeChatUseCase(repository = chatDocumentRepository)
    }

    // logout
    val logoutUseCase by lazy {
        LogoutUseCase(
            sessionManager = sessionManager,
            deliveredObserver = deliveredObserver
        )
    }

    // sentMessageWithBatch
    val sendMessageBatch by lazy {
        SendMessageBatch(
            firestore = firestore,
            chatDocumentLastMsg = updateChatDocumentLastMsg,
            unreadManager = unreadManager
        )
    }


}