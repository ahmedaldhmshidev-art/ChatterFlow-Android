package com.example.chatapp.c_listChatUser.ui_List

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentListChatsBinding
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthEvent
import com.example.chatapp.c_listChatUser.stateAndEventList.ListChatEvent
import com.example.chatapp.c_listChatUser.stateAndEventList.ListChatState
import com.example.chatapp.c_listChatUser.adapterList.AdapterListChat
import com.example.chatapp.a_authentication.viewModelAuth.AuthViewModel
import com.example.chatapp.a_authentication.viewModelAuth.AuthViewModelFactory
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.c_listChatUser.viewModelList.ListChatViewModel
import com.example.chatapp.c_listChatUser.viewModelList.ListChatViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ListChats : Fragment() {

    private var _binding :FragmentListChatsBinding ?=null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            loginUseCase = requireContext().appContainer.loginUseCase ,
            registerUseCase = requireContext().appContainer.registerUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase
        ) // من اجل logout
    }

    private val chatViewModel: ListChatViewModel by viewModels {
        ListChatViewModelFactory(
           chatRepository = requireContext().appContainer.listChatRepository ,
           userRepository = requireContext().appContainer.userRepository ,
            sessionManager = requireContext().appContainer.sessionManager
        )
    }

    private lateinit var currentUid :String
    private lateinit var adapterListChat: AdapterListChat

    private fun showProgressLoading(isLoading: Boolean) {
        binding.progressIdListChat.isVisible = isLoading
        binding.progressIdListChat.isEnabled = isLoading
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListChatsBinding.inflate(inflater,container,false)
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUid = FirebaseAuth.getInstance().currentUser?.uid?:return

        setupMenuListChat()
        setupButtonListener()

        chatViewModel.getAllChat()

        setupAdapterAndRecyclerView()

        observeAuthEvent() // logout
        observeListChatState()
        observeChatMessageEvent()
    }

    private fun setupMenuListChat() {
        binding.toolbarIdListChat.inflateMenu(
            R.menu.menu_list_chat
        )
        binding.toolbarIdListChat.setOnMenuItemClickListener {
            item ->
            when(item.itemId){
                R.id.account_menu_listChat_Id ->{
                    showAccount()
                    true
                }
                R.id.setting_menu_listChat_Id->{
//                    settingChat()
                    true
                }
                R.id.logout_menu_listChat_Id->{
//                    logoutUser
                    showDialogLogout()
                    true
                }
            else -> false
            }
        }
    }

    private fun showAccount() {
        findNavController().navigate(R.id.action_listChats_to_accountFragment)
    }

    private fun showDialogLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_menu_listChat))
            .setMessage(getString(R.string.logout_dialog_message))

            .setNegativeButton(getString(R.string.btn_cancel_delete_message)){
                dialog , _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout_menu_listChat)){
                _ , _ ->
                authViewModel.logoutUser() // logout
            }
            .show()
    }

    private fun setupButtonListener() {
        binding.fabBtnAddChatIdListChat.setOnClickListener{
            findNavController().navigate(R.id.action_listChats_to_userList)
        }
    }

    private fun observeListChatState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                chatViewModel.listChatState.collect{ state->
                    when(state){

                        ListChatState.Loading->{
                            showProgressLoading(true)
                            binding.rvListChatId.isVisible = false
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = false
                        }
                          is ListChatState.Success->{
                              showProgressLoading(false)
                              binding.rvListChatId.isVisible = true
                              binding.layoutEmptyListChat.layoutEmpty.isVisible = false

                              adapterListChat.submitList(state.users)
                              }
                        is ListChatState.Empty ->{
                            showProgressLoading(false)
                            binding.rvListChatId.isVisible = false
                            binding.layoutEmptyListChat.layoutEmpty.isVisible = true
                        }
                        is ListChatState.Error->{
                            showProgressLoading(false)
                                Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                                }
                        else ->Unit
                    }
                }
            }
        }
    }

    private fun observeChatMessageEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                chatViewModel.chatMessageEvent.collect{
                        event ->
                    when(event){
                        is ListChatEvent.NavigationToMessageListChat ->{
                            val action=ListChatsDirections.actionListChatsToMessageChat(
                                receiverId = event.userUid
                            )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun setupAdapterAndRecyclerView() {
        adapterListChat = AdapterListChat(currentId = currentUid){
            user ->
            chatViewModel.onUserClick(user = user)
        }
        binding.rvListChatId.adapter = adapterListChat
        binding.rvListChatId.layoutManager= LinearLayoutManager(requireContext())
    }

    private fun observeAuthEvent() { // logout
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                authViewModel.authEvent.collect{
                    event->
                    when(event){
                        AuthEvent.NavigationToLogin ->{
                            findNavController().navigate(R.id.authentication_nav_host,
                                null,NavOptions.Builder().
                                setPopUpTo(R.id.list_chats_nav_graph,true).build())
                        }
                        AuthEvent.NavigationToHomeListChat->{}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}