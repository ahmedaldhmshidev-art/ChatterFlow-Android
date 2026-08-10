package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.AccountEvent
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.PasswordField
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.ProfileField
import com.example.chatapp.databinding.FragmentAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory(
            getAccountCurrentUseCase = requireContext().appContainer.getAccountCurrentUseCase,
            updateInfoAccountUseCase = requireContext().appContainer.updateInfoAccountUseCase,
            changePassword = requireContext().appContainer.changePasswordUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase

        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClick()
        observeState()
        observeEvents()

        accountViewModel.loadCurrentUser()

        binding.toolbarAccountId.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountViewModel.event.collect { event ->
                    when (event) {
                        AccountEvent.SaveSuccessEditProfile -> {
                            showToast(getString(R.string.save_success))
                            editProfileDialog?.dismiss()
                        }

                        is AccountEvent.Error -> {
                            showToast(event.message)
                        }

                        AccountEvent.NavigationToLogin -> {
                            findNavController().navigate(
                                R.id.authentication_nav_host,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.list_chats_nav_graph, true)
                                    .build()
                            )
                        }

                        AccountEvent.PasswordChangedSuccess -> {
                            showToast(getString(R.string.success_change_password_account))
                            changePasswordDialog?.dismiss()
                        }

                        is AccountEvent.ErrorProfile -> {
                            when (event.field) {
                                ProfileField.NAME_BLANK -> {
                                    tvNameErrorProfile?.text =
                                        getString(R.string.error_blank_name_profile)
                                    tvNameErrorProfile?.visibility = View.VISIBLE
                                }

                                ProfileField.NEW_NAME_MUST_BE_DIFFERENT -> {
                                    tvNameErrorProfile?.text =
                                        getString(R.string.error_new_name_must_be_different)
                                    tvNameErrorProfile?.visibility = View.VISIBLE
                                }
                            }
                        }

                        is AccountEvent.ErrorPassword -> {
                            when (event.field) {
                                PasswordField.CURRENT_PASSWORD_BLANK -> {
                                    tvCurrentPasswordError?.text =
                                        getString(R.string.error_blank_current_password)
                                    tvCurrentPasswordError?.visibility = View.VISIBLE
                                }

                                PasswordField.NEW_PASSWORD_BLANK -> {
                                    tvNewPasswordError?.text =
                                        getString(R.string.error_blank_new_password)
                                    tvNewPasswordError?.visibility = View.VISIBLE
                                }

                                PasswordField.CONFIRM_PASSWORD_BLANK -> {
                                    tvConfirmPasswordError?.text =
                                        getString(R.string.error_blank_confirm_password)
                                    tvConfirmPasswordError?.visibility = View.VISIBLE
                                }

                                PasswordField.NEW_PASSWORD_MUST_BE_DIFFERENT_CURRENT -> {
                                    tvNewPasswordError?.text =
                                        getString(R.string.error_confirm_password_different)
                                    tvNewPasswordError?.visibility = View.VISIBLE
                                }

                                PasswordField.NEW_PASSWORD_TOO_SHORT_6 -> {
                                    tvNewPasswordError?.text =
                                        getString(R.string.error_new_password_less_6)
                                    tvNewPasswordError?.visibility = View.VISIBLE
                                }

                                PasswordField.CONFIRM_PASSWORD_DOES_NOT_MATCH -> {
                                    tvConfirmPasswordError?.text =
                                        getString(R.string.error_confirm_password_does_not_match)
                                    tvConfirmPasswordError?.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupClick() {
        binding.btnEditProfileAccountId.setOnClickListener {
            showEditAccountDialog()
        }
        binding.btnChangePasswordAccountId.setOnClickListener {
            showChangePasswordDialog()
        }
        binding.btnLogoutAccountId.setOnClickListener {
            showDialogLogout()
        }
    }

    private fun showDialogLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_menu_listChat))
            .setMessage(getString(R.string.logout_dialog_message))

            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout_menu_listChat)) { _, _ ->
                accountViewModel.logout() // logout
            }
            .show()
    }

    private var changePasswordDialog: AlertDialog? = null
    private var tvCurrentPasswordError: TextView? = null
    private var tvNewPasswordError: TextView? = null
    private var tvConfirmPasswordError: TextView? = null

    private fun showChangePasswordDialog() {
        val dialogShow = layoutInflater.inflate(
            R.layout.dialog_change_password, null
        )
        tvCurrentPasswordError = dialogShow.findViewById(R.id.tvCurrentPasswordErrorId)
        tvNewPasswordError = dialogShow.findViewById(R.id.tvNewPasswordErrorId)
        tvConfirmPasswordError = dialogShow.findViewById(R.id.tvConfirmPasswordErrorId)
        val currentPassword = dialogShow.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val newPassword = dialogShow.findViewById<TextInputEditText>(R.id.etNewPassword)
        val confirmPassword = dialogShow.findViewById<TextInputEditText>(R.id.etConfirmPassword)


        changePasswordDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_dialog_change_password_account)
                .setView(dialogShow)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.save, null)
                .create()

        changePasswordDialog?.show()
        changePasswordDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setOnClickListener {
                accountViewModel.changePassword(
                    currentPassword = currentPassword.text.toString().trim(),
                    newPassword = newPassword.text.toString().trim(),
                    confirmPassword = confirmPassword.text.toString().trim()
                )
            }
    }


    private var tvNameErrorProfile: TextView? = null
    private var editProfileDialog: AlertDialog? = null

    private fun showEditAccountDialog() {
        val dialogView = layoutInflater.inflate(
            R.layout.dialog_edit_account, null
        )
        tvNameErrorProfile = dialogView.findViewById(R.id.tvNameAccountErrorId)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etNameDialogEditAccountId)
        val etBio = dialogView.findViewById<TextInputEditText>(R.id.etBioDialogEditAccountId)

        val user = accountViewModel.accountState.value.user

        etName.setText(user?.name ?: "")
        etBio.setText(user?.bio ?: "")

        editProfileDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.title_dialog_edit_account))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .setPositiveButton(getString(R.string.save), null)
                .create()
        editProfileDialog?.show()
        editProfileDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setOnClickListener {

                accountViewModel.updateProfileAccount(
                    oldName = user?.name ?: "",
                    newName = etName.text.toString(),
                    newBio = etBio.text.toString(),
                )
            }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountViewModel.accountState.collect { state ->
                    if (state.isLoading) {
                        binding.progressAccountId.isVisible = true
//                        binding.nestedScrollViewAccountId.isVisible = false
                    } else {
                        binding.progressAccountId.isVisible = false
//                        binding.nestedScrollViewAccountId.isVisible = true
                    }
                    state.user?.let { user ->
                        binding.tvNameAccountId.text = user.name
                        binding.tvEmailAccountId.text = user.email
                        binding.tvUidAccountId.text = user.uid
                        binding.tvBioAccountId.text = user.bio
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