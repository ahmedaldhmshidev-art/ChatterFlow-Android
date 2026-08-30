package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError.AccountError
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError.AccountErrorToString.toStringRes
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.AccountEvent
import com.example.chatapp.databinding.FragmentAccountBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private fun showTextError(
        textView: TextView?,
        message: String
    ) {
        textView?.text = message
        textView?.isVisible = true
    }

    private fun hideTextError(
        textView: TextView?
    ) {
        textView?.text = null
        textView?.isVisible = false
    }

    private fun showGeneralError(
        card: MaterialCardView?,
        textVew: TextView?,
        messageRes: String
    ) {
        textVew?.text = messageRes
        card?.isVisible = true
    }

    private fun hideGeneralError(
        card: MaterialCardView?,
        textVew: TextView?
    ) {
        card?.isVisible = false
        textVew?.text = null
    }

    private fun renderFieldError(error: AccountError.Field) {
        when (error) {
            AccountError.Field.NameBlank -> {
                showTextError(
                    tvNameErrorProfile,
                    getString(error.toStringRes())
                )
            }
//            AccountError.Field.NameMustBeDifferent -> {
//                showTextError(
//                    tvNameErrorProfile,
//                    getString(error.toStringRes())
//                )
//            }

            AccountError.Field.CurrentPasswordBlank -> {
                showTextError(
                    tvCurrentChangePasswordError,
                    getString(error.toStringRes())
                )
            }

            AccountError.Field.NewPasswordBlank -> {
                showTextError(
                    tvNewChangePasswordError,
                    getString(error.toStringRes())
                )
            }

            AccountError.Field.NewPasswordTooShort -> {
                showTextError(
                    tvNewChangePasswordError,
                    getString(error.toStringRes())
                )
            }

            AccountError.Field.NewPasswordMustBeDifferent -> {
                showTextError(
                    tvNewChangePasswordError,
                    getString(error.toStringRes())
                )
            }

            AccountError.Field.ConfirmPasswordBlank -> {
                showTextError(
                    tvConfirmChangePasswordError,
                    getString(error.toStringRes())
                )
            }

            AccountError.Field.ConfirmPasswordDoesNotMatch -> {
                showTextError(
                    tvConfirmChangePasswordError,
                    getString(error.toStringRes())
                )
            }
        }
    }

    private fun cleanAllFieldErrors() {
        hideTextError(tvNameErrorProfile)
        hideGeneralError(
            cardGeneralErrorEditProfile, tvGeneralErrorEditProfile
        )

        hideTextError(tvCurrentChangePasswordError)
        hideTextError(tvNewChangePasswordError)
        hideTextError(tvConfirmChangePasswordError)
        hideGeneralError(
            cardGeneralChangePasswordError, tvGeneralChangePasswordError
        )
    }

    private fun renderUser(user: User?) {
        user ?: return
        binding.tvNameAccountId.text = user.name
        binding.tvEmailAccountId.text = user.email
        binding.tvUidAccountId.text = user.uid
        binding.tvBioAccountId.text = user.bio
    }

    private fun renderLoading(loading: Boolean) {
        binding.progressAccountId.isVisible = loading
        binding.nestedScrollViewAccountId.isVisible = !loading
    }


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
            changePasswordUseCase = requireContext().appContainer.changePasswordUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClick()
        observeState()
        observeEvents()
        accountViewModel.loadCurrentUser()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountViewModel.event.collect { event ->
                    when (event) {
                        AccountEvent.ProfileUpdated -> {
                            showToast(getString(R.string.success_edited_profile))
                            editProfileDialog?.dismiss()
                        }

                        AccountEvent.PasswordChanged -> {
                            showToast(getString(R.string.success_change_password_account))
                            changePasswordDialog?.dismiss()
                        }

                        AccountEvent.NoChangedProfile -> {
                            showToast(getString(R.string.error_no_changed_profile))
                            editProfileDialog?.dismiss()
                        }

                        AccountEvent.NavigationToLogin -> {
                            findNavController()
                                .navigate(
                                    R.id.authentication_nav_host, null,
                                    NavOptions.Builder()
                                        .setPopUpTo(R.id.list_chats_nav_graph, true)
                                        .build()
                                )
                        }
                    }
                }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountViewModel.accountState.collect { state ->
                    renderLoading(state.isLoading)
                    renderUser(state.user)
                    renderError(state.error)
                }
            }
        }
    }

    private fun renderError(error: AccountError?) {
        cleanAllFieldErrors()
        error ?: return
        when (error) {
            is AccountError.Field -> {
                renderFieldError(error)
            }

            is AccountError.General -> {
                renderGeneralError(error)
            }
        }
    }

    private var activeDialog: AccountDialog? = null

    enum class AccountDialog {
        EDIT_PROFILE,
        CHANGE_PASSWORD
    }

    private fun renderGeneralError(error: AccountError.General) {
        val messageRes = getString(error.toStringRes())
        when (activeDialog) {
            AccountDialog.EDIT_PROFILE -> {
                showGeneralError(
                    card = cardGeneralErrorEditProfile,
                    textVew = tvGeneralErrorEditProfile,
                    messageRes = messageRes
                )
            }

            AccountDialog.CHANGE_PASSWORD -> {
                showGeneralError(
                    card = cardGeneralChangePasswordError,
                    textVew = tvGeneralChangePasswordError,
                    messageRes = messageRes
                )
            }

            null -> Unit
        }
    }


    private fun setupClick() {
        binding.toolbarAccountId.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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
    private var tvCurrentChangePasswordError: TextView? = null
    private var tvNewChangePasswordError: TextView? = null
    private var tvConfirmChangePasswordError: TextView? = null
    private var cardGeneralChangePasswordError: MaterialCardView? = null
    private var imgGeneralChangePasswordError: ImageView? = null
    private var tvGeneralChangePasswordError: TextView? = null

    private fun showChangePasswordDialog() {

        activeDialog = AccountDialog.CHANGE_PASSWORD
        accountViewModel.clearError()

        val dialogShow = layoutInflater.inflate(
            R.layout.dialog_change_password, null
        )
        cardGeneralChangePasswordError =
            dialogShow.findViewById(R.id.cardGeneralChangePasswordErrorId)
        imgGeneralChangePasswordError =
            dialogShow.findViewById(R.id.imgGeneralChangePasswordErrorId)
        tvGeneralChangePasswordError = dialogShow.findViewById(R.id.tvGeneralChangePasswordErrorId)
        tvCurrentChangePasswordError = dialogShow.findViewById(R.id.tvCurrentPasswordErrorId)
        tvNewChangePasswordError = dialogShow.findViewById(R.id.tvNewPasswordErrorId)
        tvConfirmChangePasswordError = dialogShow.findViewById(R.id.tvConfirmPasswordErrorId)
        val currentPassword = dialogShow.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val newPassword = dialogShow.findViewById<TextInputEditText>(R.id.etNewPassword)
        val confirmPassword = dialogShow.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        imgGeneralChangePasswordError?.setOnClickListener {
            hideGeneralError(cardGeneralChangePasswordError, tvGeneralChangePasswordError)
        }

        changePasswordDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_dialog_change_password_account)
                .setView(dialogShow)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.save, null)
                .create()

        changePasswordDialog?.setOnDismissListener {
            activeDialog = null
            tvCurrentChangePasswordError = null
            tvNewChangePasswordError = null
            tvConfirmChangePasswordError = null
            cardGeneralChangePasswordError = null
            tvGeneralChangePasswordError = null

            accountViewModel.clearError()
        }
        changePasswordDialog?.show()
        changePasswordDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setOnClickListener {
                accountViewModel.changePassword(
                    currentPassword = currentPassword.text.toString(),
                    newPassword = newPassword.text.toString(),
                    confirmPassword = confirmPassword.text.toString()
                )
            }
    }

    private var tvNameErrorProfile: TextView? = null
    private var editProfileDialog: AlertDialog? = null
    private var cardGeneralErrorEditProfile: MaterialCardView? = null
    private var tvGeneralErrorEditProfile: TextView? = null
    private var imgGeneralErrorEditProfile: ShapeableImageView? = null

    private fun showEditAccountDialog() {

        activeDialog = AccountDialog.EDIT_PROFILE
        accountViewModel.clearError()

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_edit_account, null
        )
        cardGeneralErrorEditProfile = dialogView.findViewById(R.id.cardGeneralErrorEditProfileId)
        imgGeneralErrorEditProfile = dialogView.findViewById(R.id.imgGeneralErrorEditProfileId)
        tvGeneralErrorEditProfile = dialogView.findViewById(R.id.tvGeneralErrorEditProfileId)
        tvNameErrorProfile = dialogView.findViewById(R.id.tvNameAccountErrorId)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etNameDialogEditAccountId)
        val etBio = dialogView.findViewById<TextInputEditText>(R.id.etBioDialogEditAccountId)
        imgGeneralErrorEditProfile?.setOnClickListener {
            hideGeneralError(cardGeneralErrorEditProfile, tvGeneralErrorEditProfile)
        }
        val user = accountViewModel.accountState.value.user

        etName.setText(user?.name.orEmpty())
        etBio.setText(user?.bio.orEmpty())

        editProfileDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.title_dialog_edit_account))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .setPositiveButton(getString(R.string.save), null)
                .create()

        editProfileDialog?.setOnDismissListener {
            activeDialog = null
            tvNameErrorProfile = null
            cardGeneralErrorEditProfile = null
            tvGeneralErrorEditProfile = null

            accountViewModel.clearError()
        }

        editProfileDialog?.show()
        editProfileDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setOnClickListener {
                accountViewModel.updateProfileAccount(
                    oldName = user?.name.orEmpty(),
                    newName = etName.text.toString(),
                    newBio = etBio.text.toString(),
                    oldBio = user?.bio.orEmpty()
                )
            }
    }


    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}