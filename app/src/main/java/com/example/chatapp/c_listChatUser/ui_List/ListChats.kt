package com.example.chatapp.c_listChatUser.ui_List

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentListChatsBinding
import com.example.chatapp.c_listChatUser.utiles.stateAndEventList.ListChatEvent
import com.example.chatapp.c_listChatUser.utiles.stateAndEventList.ListChatState
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.c_listChatUser.utiles.listChatError.ListChatError
import com.example.chatapp.c_listChatUser.utiles.listChatError.ListChatErrorUi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ListChats : Fragment() {
    private lateinit var adapterListChat: AdapterListChat


    private var _binding: FragmentListChatsBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ListChatViewModel by viewModels {
        ListChatViewModelFactory(
            chatRepository = requireContext().appContainer.listChatRepository,
            userRepository = requireContext().appContainer.userRepository,
            sessionManager = requireContext().appContainer.sessionManager,
            deleteChatUseCase = requireContext().appContainer.deleteChatUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase
        )
    }

    private fun showProgressLoading(isLoading: Boolean) {
        binding.progressIdListChat.isVisible = isLoading
    }

    private fun showError(error: ListChatError) {
        Snackbar.make(binding.root, getString(ListChatErrorUi.map(error)), Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenuListChat()
        setupButtonListener()
        setupAdapterAndRecyclerView()

        setupSearchListener()
        observeListChatState()
        observeChatMessageEvent()

        chatViewModel.getAllChat()
    }

    private fun setupSearchListener() {
        binding.etSearchListChatsId.addTextChangedListener { text ->
            chatViewModel.searchChats(text?.toString().orEmpty())
        }
    }

    private fun setupMenuListChat() {
        binding.toolbarIdListChat.inflateMenu(R.menu.menu_list_chat)

        binding.toolbarIdListChat.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.account_menu_listChat_Id -> {
                    showAccount()
                    true
                }

                R.id.setting_menu_listChat_Id -> {
                    settingChat()
                    true
                }

                R.id.logout_menu_listChat_Id -> {
                    showDialogLogout()
                    true
                }

                R.id.about_menu_listChat_Id -> {
                    showAboutApp()
                    true
                }

                else -> false
            }
        }
    }

    private fun settingChat() {
        findNavController().navigate(R.id.action_listChats_to_settingFragment)
    }

    private fun showAboutApp() {
        findNavController().navigate(R.id.action_listChats_to_aboutAppFragment)
    }

    private fun showAccount() {
        findNavController().navigate(R.id.action_listChats_to_accountFragment)
    }

    private fun showDialogLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_menu_listChat))
            .setMessage(getString(R.string.logout_dialog_message))

            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout_menu_listChat)) { _, _ ->
                chatViewModel.logout() // logout
            }
            .show()
    }

    private fun setupButtonListener() {
        binding.fabBtnAddChatIdListChat.setOnClickListener {
            findNavController().navigate(R.id.action_listChats_to_userList)
        }
    }

    private fun observeListChatState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.listChatState.collect { state ->
                    when (state) {
                        ListChatState.Idle -> {
                            showProgressLoading(false)
                        }

                        ListChatState.Loading -> {
                            showProgressLoading(true)
                            binding.rvListChatId.isVisible = false
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = false
                        }

                        is ListChatState.Success -> {
                            showProgressLoading(false)
                            binding.rvListChatId.isVisible = true
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = false

                            adapterListChat.submitList(state.users)
                        }

                        is ListChatState.Empty -> {
                            showProgressLoading(false)
                            binding.rvListChatId.isVisible = false
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = true
                        }

                        is ListChatState.SearchEmpty -> {
                            showProgressLoading(false)
                            binding.rvListChatId.isVisible = false
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = true
                            binding.layoutEmptyListChat.textEmptyId.text =
                                getString(R.string.error_no_chat_found)
                        }

                        is ListChatState.Error -> {
                            showProgressLoading(false)
                            binding.rvListChatId.isVisible = false
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = true
                            binding.layoutEmptyListChat.textEmptyId.text =
                                getString(ListChatErrorUi.map(state.error))
                        }
                    }
                }
            }
        }
    }

    private fun observeChatMessageEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.chatMessageEvent.collect { event ->
                    when (event) {
                        is ListChatEvent.OpenChatMessage -> {
                            val action = ListChatsDirections.actionListChatsToMessageChat(
                                receiverId = event.userUid
                            )
                            findNavController().navigate(action)
                        }

                        is ListChatEvent.ShowError -> {
                            showError(event.error)
                        }

                        is ListChatEvent.DeleteChatSuccess -> {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.success_delete_chat),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        ListChatEvent.NavigationToLogin -> {
                            findNavController().navigate(
                                R.id.authentication_nav_host,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.list_chats_nav_graph, true)
                                    .build()
                            )
                        }
                    }
                }
            }
        }
    }

    private val sessionManager get() = requireContext().appContainer.sessionManager
    private fun setupAdapterAndRecyclerView() {
        val currentUid = sessionManager.currentUserId ?: return

        adapterListChat = AdapterListChat(currentId = currentUid,
            navigationToMessage = { user ->
                chatViewModel.onUserClick(user = user)
            },
            onDeleteChat = { chatId, view ->
                showPopupMenu(chatId, view)
            }
        )

        binding.rvListChatId.adapter = adapterListChat
        binding.rvListChatId.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showPopupMenu(chatId: String, view: View) {
        val popup = PopupMenu(requireContext(), view)

        popup.menuInflater.inflate(R.menu.menu_delete_chat, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete_chat_menu_id -> {
                    showDialogDeleteChat(chatId)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun showDialogDeleteChat(chatId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.menu_delete_chat))
            .setMessage(getString(R.string.menu_delete_chat_msg_dialog))
            .setNegativeButton(R.string.btn_cancel, null)
            .setPositiveButton(R.string.delete_menu) { _, _ ->
                chatViewModel.deleteChat(chatId)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}