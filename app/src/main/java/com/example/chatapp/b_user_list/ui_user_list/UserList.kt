package com.example.chatapp.b_user_list.ui_user_list

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.databinding.FragmentUserListBinding
import com.example.chatapp.b_user_list.adabter_user_list.AdapterUserList
import com.example.chatapp.b_user_list.uiState_event.EventUserList
import com.example.chatapp.b_user_list.uiState_event.StateUserList
import com.example.chatapp.b_user_list.viewModel_user_list.UserListViewModelFactory
import com.example.chatapp.b_user_list.viewModel_user_list.ViewModelUserList
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UserList : Fragment() {
    private var _binding:FragmentUserListBinding?=null
    private val binding get() = _binding!!

    private val userListViewModel :ViewModelUserList by viewModels {
        UserListViewModelFactory(
            repository = requireContext().appContainer.userRepository,
            sessionManager = requireContext().appContainer.sessionManager
        )
    }
//    private fun showProgressLoading(isLoading: Boolean) {
//        binding.progressUserListId.isVisible = isLoading
//        binding.progressUserListId.isEnabled = isLoading
//    }

    private lateinit var  adapterUserList :AdapterUserList



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarDesign()
        setupAdapterAndRecyclerView()


        userListViewModel.gatAllUsers()
        observeState()
        observeEvent()
    }

    private fun observeEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                userListViewModel.eventUserList.collect{
                    event->
                  when(event){
                      is EventUserList.NavigationToMessageUserList->{
                          val action = UserListDirections.actionUserListToMessageChat(
                              receiverId = event.user.uid
                          )
                          findNavController().navigate(action)
                      }
                  }
                }
            }
        }
    }

    private fun toolbarDesign() {
        binding.toolbarUserListId.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                userListViewModel.stateUserList.collect{state ->
                    when(state){

                        StateUserList.Loading->{
                            binding.progressUserListId.isVisible = true
                            binding.rvUserListId.isVisible = false
                            binding.layoutEmptyUserList.layoutEmpty.isVisible=false
                        }
                        is StateUserList.Success->{
                            binding.progressUserListId.isVisible = false
                            binding.rvUserListId.isVisible = true
                            binding.layoutEmptyUserList.layoutEmpty.isVisible=false
                            adapterUserList.submitList(state.users)
                        }
                        is StateUserList.Empty->{
                            binding.progressUserListId.isVisible = false
                            binding.rvUserListId.isVisible = false
                            binding.layoutEmptyUserList.layoutEmpty.isVisible=true
                        }
                        is StateUserList.Error->{
                           binding.progressUserListId.isVisible=false
                            Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                        }

                       else -> Unit
                    }
                }
            }
        }
    }

    private fun setupAdapterAndRecyclerView() {
        adapterUserList=AdapterUserList{
            user ->
            userListViewModel.onUserClick(user)
        }
        binding.rvUserListId.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUserListId.adapter = adapterUserList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
