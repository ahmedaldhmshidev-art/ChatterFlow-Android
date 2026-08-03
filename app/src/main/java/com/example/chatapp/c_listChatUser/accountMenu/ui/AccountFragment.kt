package com.example.chatapp.c_listChatUser.accountMenu.ui

import android.os.Bundle
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
import com.example.chatapp.R
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.databinding.FragmentAccountBinding
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding :FragmentAccountBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val accountViewModel : AccountViewModel by viewModels {
        AccountViewModelFactory(
           getAccountCurrentUseCase = requireContext().appContainer.getAccountCurrentUseCase
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountViewModel.loadCurrentUser()
        observeState()

        binding.toolbarAccountId.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                accountViewModel.accountState.collect{ state->
                    if (state.isLoading){
                        binding.progressAccountId.isVisible = true
                        binding.nestedScrollViewAccountId.isVisible=false
                    }else{
                        binding.progressAccountId.isVisible = false
                        binding.nestedScrollViewAccountId.isVisible=true
                    }
                    state.user?.let {
                        user->
                        binding.tvNameAccountId.text = user.name
                        binding.tvEmailAccountId.text = user.email
                        binding.tvUidAccountId.text = user.uid
                        binding.tvBioAccountId.text = user.bio
                    }
                    state.error?.let {
                        showToast(state.error)
                    }
                }
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}