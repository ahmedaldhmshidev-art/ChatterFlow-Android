package com.example.chatapp.e_messageChatId.info.ui

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
import androidx.navigation.fragment.navArgs
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.databinding.FragmentInfoMenuBinding
import com.example.chatapp.e_messageChatId.info.uiState.UiStateInfo
import com.example.chatapp.e_messageChatId.ui_.MessageChatArgs
import kotlinx.coroutines.launch

class InfoFragmentMenu : Fragment() {
    private var _binding : FragmentInfoMenuBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentInfoMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    private val infoViewModel:ViewModelInfo by viewModels {
        InfoViewModelFactory(
            getOtherUserUseCase = requireContext().appContainer.getOtherUserUseCase
        )
    }
    private val args: InfoFragmentMenuArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val otherUserId = args.otherUserId

        binding.toolbarInfoId.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        infoViewModel.getOtherUser(otherUserId)
        observerUiSateInfo()
    }

    private fun observerUiSateInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED){
                infoViewModel.stateInfo.collect{
                    state ->
                    if (state.isLoading){
                        binding.progressInfoId.isVisible = true
                        binding.nestedScrollViewInfoId.isVisible = false
                    }
                    else {
                        binding.progressInfoId.isVisible = false
                        binding.nestedScrollViewInfoId.isVisible = true
                    }
                    state.user?.let { user->
                        binding.tvNameInfoId.text = user.name
                        binding.tvEmailInfoId.text = user.email
                        binding.tvUidInfoId.text = user.uid
                        binding.tvBioInfoId.text = user.bio
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