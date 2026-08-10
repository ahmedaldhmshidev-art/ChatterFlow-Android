package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.databinding.FragmentSettingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class SettingFragment : Fragment() {
    private lateinit var preferencesManager: PreferencesManager


    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!


    private fun showToastMsg(selected: String) {
        Toast.makeText(requireContext(), selected, Toast.LENGTH_SHORT).show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // يربط العناصر بلقائمة المنسدلة
        setupLanguageDropdown()
        setupThemeDropdown()

        preferencesManager = requireContext().appContainer.preferencesManager

        // هذا يراقب من اجل وضغ الافتراضي فقط
        observerLanguage()
        observerTheme()

        binding.toolbarSettingsId.setNavigationOnClickListener {
            findNavController().navigateUp()

        }
    }


    //    observerTheme
    private fun observerTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                preferencesManager.theme.collect { theme ->
                    binding.actThemeSettingId.setText(
                        theme,
                        false
                    )
                }
            }
        }
    }

    private fun setupThemeDropdown() {

        val theme = arrayOf(
            getString(R.string.theme_system),
            getString(R.string.theme_light),
            getString(R.string.theme_dark)
        )

        val adapterTheme =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, theme)

        binding.actThemeSettingId.setAdapter(adapterTheme)

        // الاستماع لاختيار عنصر من القائة المنسدلة ينفذ حدث
        binding.actThemeSettingId.setOnItemClickListener { _, _, position, _ ->
            val selectedTheme = theme[position] // وضع الذي اختارة المستخدم في متغير

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    // حفض الثيم المختارة في الشردبرفرنسزز
                    preferencesManager.saveTheme(selectedTheme)

                    AppSetting.applyTheme(selectedTheme)
                }
            }
            showToastMsg(selectedTheme)
        }
    }


    // observerLanguage
    private fun observerLanguage() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                preferencesManager.language.collect { language ->
                    binding.actLanguageSettingId.setText( // وضع اللغة التلقائي نفس الغة الذي اختارها المستخدم وتم حفضها في شيردبرفرنسزز
                        language,
                        false
                    )
                }
            }
        }
    }

    private fun setupLanguageDropdown() {
        val languages = arrayOf(
            getString(R.string.language_english), getString(R.string.language_arabic)
        )
        val adapterArray =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line, languages
            )

        binding.actLanguageSettingId.setAdapter(adapterArray)

        binding.actLanguageSettingId.setOnItemClickListener { _, _, position, _ ->

            val selectedLanguage = languages[position]

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    preferencesManager.saveLanguage(selectedLanguage)
                    // رسالة تاكيد قبل تغيير اللغة
                    showDialogChangeLanguage(selectedLanguage)
                }
            }
            showToastMsg(selectedLanguage)
        }
    }

    // تاكيد تغيير اللغة
    private fun showDialogChangeLanguage(language: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_change_language))
            .setMessage(getString(R.string.message_change_language))

            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_change_language)) { _, _ ->
                AppSetting.applyLanguage(language) // logout
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}