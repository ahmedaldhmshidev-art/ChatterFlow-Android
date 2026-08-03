//package com.example.chatapp.e_messageChatId.ui_.menu
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.DrawableRes
//import androidx.annotation.StringRes
//import androidx.core.view.isVisible
//import com.example.chatapp.R
//import com.example.chatapp.databinding.BottomSheetMessageMenuBinding
//import com.example.chatapp.databinding.ItemMessageOptionMenuBinding
//import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
//import com.example.chatapp.utils.formatFullDate
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//
//class BottomSheetMenuMsg : BottomSheetDialogFragment() {
//
//
//    private var _binding: BottomSheetMessageMenuBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var message:MessageText
//    private lateinit var owner: MessageOwner
//    private lateinit var argument: Bundle
//
//    companion object {
//        private const val ARG_MESSAGE = "arg_message"
//        private const val ARG_OWNER   = "arg_owner"
//
//        fun newInstance(
//            messageText: MessageText ,
//            ownerEnum: MessageOwner
//        ):BottomSheetMenuMsg{
//
//            return BottomSheetMenuMsg().apply {
//                 argument = Bundle().apply {
//                    putParcelable(ARG_MESSAGE , messageText)
//                    putString(ARG_OWNER , ownerEnum.name)
//                }
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        argument?.let {
//            message = it.getParcelable(ARG_MESSAGE)!!
//            owner   = MessageOwner.valueOf(it.getString(ARG_OWNER)!!)
//        }
//    }
//
//    private var onAction:((MessageMenuAction)->Unit )?=null
//
//    fun setOnAction(
//        listener:(MessageMenuAction) ->Unit
//    ):BottomSheetMenuMsg{
//        onAction = listener
//        return this
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? {
//        _binding =BottomSheetMessageMenuBinding .inflate(inflater , container , false)
//        return binding.root
//    }
//
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupViews()
//        setupClicks()
//    }
//
//    private fun setupClicks() {
//       setupChick(binding.itemEditIdBottomSheet.root , MessageMenuAction.Edit(message))
//        setupChick(binding.itemCopyBottomSheet.root , MessageMenuAction.Copy(message.messageText))
//        setupChick(binding.itemDeleteIdBottomSheet.root , MessageMenuAction.Delete(message))
//    }
//    private fun  setupChick(
//        view:View , action:MessageMenuAction
//    ){
//        view.setOnClickListener {
//            onAction?.invoke(action)
//            dismiss()
//        }
//    }
//
//    private fun setupViews() {
//        showMessageDate() // عرض التاريخ
//        setupMenuItems() // تنسيق الايقونات
////        updateVisibleItems() // اخفا واضهار العناصر بنائن عن ال owner
//    }
////    private fun updateVisibleItems(){
////        if (owner == MessageOwner.OTHER){
////            binding.itemEditIdBottomSheet.root.isVisible = false
////            binding.itemDeleteIdBottomSheet.root.isVisible = false
////        }
////        if (message.deleted){
////            binding.itemEditIdBottomSheet.root.isVisible = false
////            binding.itemCopyBottomSheet.root.isVisible = false
////            binding.itemDeleteIdBottomSheet.root.isVisible = false
////        }
////    }
//
//
//    private fun setupMenuItems() {
//        setupItem(binding.itemEditIdBottomSheet   , R.drawable.ic_edit_menu   ,R.string.edit_menu)
//        setupItem(binding.itemCopyBottomSheet     , R.drawable.ic_copy_menu   , R.string.copy_menu)
//        setupItem(binding.itemDeleteIdBottomSheet , R.drawable.ic_delete_menu , R.string.delete_menu)
//    }
//
//    private fun setupItem(
//        item:ItemMessageOptionMenuBinding , @DrawableRes icon :Int , @StringRes text :Int
//    ){
//        item.imgOptionMenuId.setImageResource(icon)
//        item.tvNameItemOptionMenuId.setText(text)
//    }
//
//
//    private fun showMessageDate() {
//        binding.tvDateMenuBottomSheet.text= formatFullDate(message.timestamp)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}