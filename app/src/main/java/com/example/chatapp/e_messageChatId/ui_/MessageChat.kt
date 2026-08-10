package com.example.chatapp.e_messageChatId.ui_

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.d_chat_Document.viewModel_document.ChatDocumentInfoViewModel
import com.example.chatapp.d_chat_Document.viewModel_document.ChatDocumentViewModelFactory
import com.example.chatapp.databinding.FragmentMessageChatBinding
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.ui_.menu.ActionMessage
import com.example.chatapp.e_messageChatId.stateAndEvent_msg.ChatScreenState
import com.example.chatapp.e_messageChatId.stateAndEvent_msg.MessageEvent
import com.example.chatapp.e_messageChatId.ui_.menu.MessageOwner
import com.example.chatapp.e_messageChatId.stateAndEvent_msg.SendState
import com.example.chatapp.e_messageChatId.ui_.menu.MessageMenuAction
import com.example.chatapp.utils.ChatUtilsUid
import com.example.chatapp.utils.buildChatItem
import com.example.chatapp.e_messageChatId.viewModel_msg.MessageViewModel
import com.example.chatapp.e_messageChatId.viewModel_msg.MessageViewModelFactory
import com.example.chatapp.utils.showIconMenu
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class MessageChat : Fragment() {

    private var _binding: FragmentMessageChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    //adapter
    private lateinit var messageAdapter: MessageAdapter

    private val args: MessageChatArgs by navArgs()
    private lateinit var chatId: String       // الغرفة او اسم المكان الذي سنخزن المحادثة فيها والي نجلب منها
    private lateinit var currentUserUid: String  // المستحدم الحالي
    private lateinit var otherUserId: String   // المستخدم الذي تم الضغط علا شاتة ودخول لها

    private var isAtBottom: Boolean = true


    // viewModels chatDocumentInfoViewModel
    private val chatDocumentViewModel: ChatDocumentInfoViewModel by viewModels {
        ChatDocumentViewModelFactory(
            repository = requireContext().appContainer.chatDocumentRepository,
            sessionManager = requireContext().appContainer.sessionManager
        )
    }

    // viewModel MessageViewModel
    private val msgViewModel: MessageViewModel by viewModels {
        MessageViewModelFactory(
            getAllMessageUseCase = requireContext().appContainer.getAllMessageUseCase,
            openChatUseCase = requireContext().appContainer.openChatUseCase,
            sendMessageUseCase = requireContext().appContainer.sendMsgUseCase,
            seenObserver = requireContext().appContainer.seenObserver,
            deletedMessageUseCase = requireContext().appContainer.deletedMessageUseCase,
            editedMessageUseCase = requireContext().appContainer.editedMessageUseCase,
            removeAllMessageUseCase = requireContext().appContainer.removeAllMessageUseCase
        )
    }


    // --------------------------- التنفيذ يبدا
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenuToolbar()


//
//            menuHost.addMenuProvider(
//                object : MenuProvider {
//                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                        menuInflater.inflate(
//                            R.menu.menu_chat_message , menu)
//                    }
//                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                        return  when(menuItem.itemId){
//
//                            R.id.info_menu_chatId ->{ showChatInfo()
//                                true }
//                            R.id.remove_menu_chatId ->{ showRemoveDialog()
//                                true }
//                            else -> false } }
//                }
//    , viewLifecycleOwner ,
//                Lifecycle.State.RESUMED
//            )

        initArguments()
        createChatDocument() //        انشا المحادثة عند الضغط عل المستخدم
        setupRecyclerView()
        setupToolbar()
        setupTyping()
        setupSendButton()
        observeChatState()
        observeSendState()
        observerEvent()
        msgViewModel.openChat(
            chatId = chatId, currentUserId = currentUserUid, otherUserId = otherUserId
        )
    }

    private fun setupMenuToolbar() {
        binding.toolbarMsgId.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.info_menu_chatId -> {
                    showChatInfo()
                    true
                }

                R.id.remove_menu_chatId -> {
                    showRemoveDialog()
                    true
                }

                else -> false
            }
        }
    }

    private fun showChatInfo() {
        val action =
            MessageChatDirections.actionMessageChatToInfoFragmentMenu(otherUserId = otherUserId)
        findNavController().navigate(action)
    }

    private fun showRemoveDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_removeAll_message))
            .setMessage(getString(R.string.removeAll_message))

            .setNegativeButton(
                getString(R.string.btn_cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }
//          .show()

//            dialog.getButton(AlertDialog.BUTTON_POSITIVE) .setTextColor(MaterialColors.getColor(dialog.context,com.google.android.material, androidx.appcompat.R.attr.colorError , Color.RED))
            .setPositiveButton(
                getString(R.string.btn_yes_delete_message)
            ) { _, _ ->
                msgViewModel.removeAllMessage(
                    chatId = chatId, currentUserId = currentUserUid
                )
            }
            .show()
    }


    private fun initArguments() {
        currentUserUid = requireContext().appContainer.sessionManager.currentUserId
            ?: return  // اذا لم يجد مستخدم لا تدخل الشات return
        otherUserId = args.receiverId  //  هو uid الشخص الذي تم الضغط عل محادثتة
        chatId =
            ChatUtilsUid.generateChatId(currentUserUid = currentUserUid, otherUserUid = otherUserId)
    }

    //انشا المحادثة عند الضغط عل المستخدم
    private fun createChatDocument() {
        chatDocumentViewModel.createChatDocumentIfNotExist(
            chatId = chatId,
            participants = listOf(currentUserUid, otherUserId)
        )
    }

    private fun setupSendButton() {
        binding.btnSendMsgId.setOnClickListener {
            when (inputMode) {
                InputMode.SEND -> sendMessage() // فحص حالة الحقل اذا كانت ارسال يتم الارسال فقط
                InputMode.EDIT -> editingMessage() // اذا حالة الحقل تعديل يتم التعديل
            }
        }
        binding.btnCancelEditMsgId.setOnClickListener {
            exitEditMode()
        }
    }

    private fun sendMessage() {
        val text = binding.etWrightMessageId.text.toString().trim()

        if (text.isBlank()) return

        msgViewModel.sendMessage(
            chatId = chatId,
            senderId = currentUserUid,
            receiverId = otherUserId,
            messageText = text,
        )
        binding.etWrightMessageId.text?.clear()
    }

    private fun observeChatState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                msgViewModel.chatState.collect { state ->
                    binding.progressMsgId.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE

                    renderChat(state) // adapter
                }
            }
        }
    }

    private fun observeSendState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                msgViewModel.sendState.collect { state ->
                    when (state) {
                        SendState.Idle -> Unit
                        SendState.Sending -> {
//                            binding.btnSendMessageId.isEnabled = false
                        }

                        SendState.Success -> {
//                            binding.btnSendMessageId.isEnabled = true
//                            binding.etWrightMessageId.text?.clear()
                            binding.etWrightMessageId.clearFocus()

                            chatDocumentViewModel.stopOnTyping(
                                chatId = chatId, currentUserUid
                            )
                        }
                    }
                }
            }
        }
    }

    private fun renderChat(state: ChatScreenState) {
        binding.toolbarMsgId.title = state.toolbarUser?.name
        // img
        // adapter and عند وصول رسالة يقفز تحت
        val item = buildChatItem(state.messages)
        messageAdapter.submitList(item) {
            if (isAtBottom && item.isNotEmpty()) {
                binding.rvMsgId.scrollToPosition(item.lastIndex)
            }
        }
    }

    private fun setupTyping() {
        binding.etWrightMessageId.addTextChangedListener {
            chatDocumentViewModel.onTyping(
                chatId = chatId, userId = currentUserUid
            )
        }
        chatDocumentViewModel.getAllChatDocument(chatId = chatId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatDocumentViewModel.typingText.collect {
                    if (it == null) {
                        binding.tvTypingMsgId.visibility = View.GONE
                    } else {
                        binding.tvTypingMsgId.visibility = View.VISIBLE
                        binding.tvTypingMsgId.text = it
                    }
                }
            }
        }
    }

    private fun setupToolbar() {

        binding.toolbarMsgId.setOnClickListener {

            val action =
                MessageChatDirections.actionMessageChatToInfoFragmentMenu(otherUserId = otherUserId)
            findNavController().navigate(action)
        }
        binding.toolbarMsgId.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // مراقبة الاحداث error ,  success
    private fun observerEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                msgViewModel.event.collect { event ->
                    when (event) {
                        is MessageEvent.ShowError -> {
                            showToast(message = event.messageError)
                        }

                        is MessageEvent.EditSuccess -> {
                            exitEditMode()
                            binding.root.showSnackBar(
                                getString(R.string.edit_success)
                            )
                        }

                        MessageEvent.DeleteSuccess -> {
                            binding.root.showSnackBar(
                                getString(R.string.delete_success)
                            )
                        }

                        is MessageEvent.CopyMessage -> {
                            Log.d("copyToClipboard", "copyToClipboardEvent:${event.copyText}")
                            copyToClipboard(event.copyText)
                        }

                        is MessageEvent.RemoveAllMessageSuccess -> {
                            binding.root.showSnackBar(getString(R.string.remove_success))
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(currentUserId = currentUserUid) { action ->
            // استقبال الرسالة التي تم الضغط عليها من اجل عمل لها تعديل او حذف
            when (action) {
                is ActionMessage.ShowMenu -> {
                    showMessageMenu(
                        message = action.message,
                        owner = action.owner,
                        anchorView = action.anchorView
                    )
                }
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvMsgId.apply {
            this.layoutManager = layoutManager
            adapter = messageAdapter
            setHasFixedSize(true)

            // نعمل مستمع للقائمة من اجل عند وصول رسالة تنزل القائمة تلقائي
            addOnScrollListener(
                object :
                    RecyclerView.OnScrollListener() {  // انشي مستمع او مراقب لتحرك recyclerView
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val lastVisible = layoutManager.findLastVisibleItemPosition()
                        isAtBottom = lastVisible >= messageAdapter.itemCount - 1
                    }
                }
            )
        }
    }

    // هنا عند الضغط عل رسالة تم عرض اخيارات من تصميم xml
    private fun showMessageMenu(
        message: MessageText, owner: MessageOwner, anchorView: View
    ) {
//        // عرض المنيو بجنب الرسالة
//        PopupWindowOption(
//            context = requireContext() ,
//            message = message ,
//            owner = owner,
//            anchorView = anchorView ,
//            onAction = ::onMessageMenuAction
//        ).showPopupWindow()
//

        // عرض منيو من الاسفل
//    BottomSheetMenuMsg.newInstance(message, owner)
//        .setOnAction(::onMessageMenuAction)
//        .show(parentFragmentManager, "MessageMenu")
//

//     عرض المنيو العادي
        val popupMenu = PopupMenu(
//            ContextThemeWrapper(
            requireContext(),
//                com.google.android.material.R.style.ThemeOverlay_Material3_DayNight_SideSheetDialog
//            ),
            anchorView,
        )

        when (owner) {
            MessageOwner.ME -> { // اذا كانت الرسالة رسالتي يتم عرض الخيارات الخاصة بي
                popupMenu.menuInflater.inflate(R.menu.menu_my_message, popupMenu.menu)
            }

            MessageOwner.OTHER -> { // اذا الرسالة لطرف الاخر يتم عرض الخيارات الخاصة بة
                popupMenu.menuInflater.inflate(R.menu.menu_other_message, popupMenu.menu)
            }
        }
        showIconMenu(menu = popupMenu.menu)


//     هنا عند الضغط عل احد الخيارات تحدد ماذا نعمل
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.action_edite_id -> {
                    onMessageMenuAction(
                        MessageMenuAction.Edit(message = message)
                    )
                    true
                }

                R.id.action_delete_id -> {

                    onMessageMenuAction(
                        MessageMenuAction.Delete(message = message)
                    )
                    true
                }

                R.id.action_copy_id -> {
                    onMessageMenuAction(
                        MessageMenuAction.Copy(text = message.messageText)
                    )
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    // معلجة الاحداث التي تم الضغط عليها
    private fun onMessageMenuAction(action: MessageMenuAction) {
        when (action) {
            is MessageMenuAction.Edit -> {
                enterEditMode(action.message)
            }

            is MessageMenuAction.Delete -> {
                dialogDeleteConfirm(action.message)
            }

            is MessageMenuAction.Copy -> {
                msgViewModel.copyMessage(action.text)
            }
        }
    }

    private fun dialogDeleteConfirm(message: MessageText) {

        MaterialAlertDialogBuilder(requireContext()) // كائن من مكتبة يعرض مربع حوار
            .setTitle(getString(R.string.title_delete_message))
            .setMessage(getString(R.string.delete_message))
            .setPositiveButton(getString(R.string.btn_yes_delete_message)) { // الزر الايجابي
                    _, _ ->
                msgViewModel.deleteMessage(message = message)
            }
            .setNegativeButton(getString(R.string.btn_cancel), null) // زر الالغاء
            .show()
    }

    enum class InputMode { SEND, EDIT }

    private var inputMode = InputMode.SEND
    private var editingMessage: MessageText? = null

    // تهيئة شكل حقل الكتابة بعد الضغط عل تعديل
    private fun enterEditMode(message: MessageText) {
        binding.btnCancelEditMsgId.isVisible = true
        inputMode = InputMode.EDIT

        editingMessage = message // الرسالة الي نريد تعديلها جت من adapter

        binding.etWrightMessageId.setText(message.messageText) // النص المراد تعديله في حقل الرسالة
        binding.etWrightMessageId.setSelection(binding.etWrightMessageId.text!!.length) // انقل الموشر الي اخر حرف بالكلمة
        binding.btnSendMsgId.setIconResource(R.drawable.ic_edit) // icon send
        binding.etWrightMessageId.requestFocus()
    }

    // التعديل الفعلي يتم هننا
    private fun editingMessage() {
        val message = editingMessage ?: return // جلب الرسالة المراد تعديلها الي هذه المتغير

        val newText = binding.etWrightMessageId.text.toString().trim()

        if (newText.isEmpty()) return
        msgViewModel.editMessage(message, newText)

        exitEditMode() // بعد الانتها من التعديل
    }

    // دلة ترجع شكل حقل الكتابة الي وضعة السابق قبل الضغط عل تعديل
    private fun exitEditMode() {
        inputMode = InputMode.SEND
        editingMessage = null

        binding.etWrightMessageId.text?.clear()
        binding.btnCancelEditMsgId.isVisible = false

        binding.btnSendMsgId.setIconResource(R.drawable.btn_send_message_chat)
    }

    // عملية النسخ تتم هنا
    private fun copyToClipboard(copyText: String) {

        val clipboard = requireContext()
            .getSystemService( // من خدمات اندرويد
                ClipboardManager::class.java // ننشي كانت المتحكم في النسخ
            )

        val clip = ClipData.newPlainText("message", copyText)

        clipboard.setPrimaryClip(clip) // هو الي يقوم بخذ النص الي الحافضة

        showToast(getString(R.string.copy_success))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun View.showSnackBar(message: String) {
        Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        chatDocumentViewModel.stopOnTyping(
            chatId = chatId, currentUserUid
        )
        msgViewModel.onClose()
        _binding = null
        super.onDestroyView()
    }
}


