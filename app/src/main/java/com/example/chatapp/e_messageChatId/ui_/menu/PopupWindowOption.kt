//package com.example.chatapp.e_messageChatId.ui_.menu
//
//import android.content.Context
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.PopupWindow
//import androidx.annotation.DrawableRes
//import androidx.annotation.StringRes
//import androidx.core.view.isVisible
//import com.example.chatapp.R
//import com.example.chatapp.databinding.BottomSheetMessageMenuBinding
//import com.example.chatapp.databinding.ItemMessageOptionMenuBinding
//import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
//import com.example.chatapp.utils.styleTime
//
//class PopupWindowOption(
//    private val context: Context,
//    private val message:MessageText,
//    private val owner: MessageOwner,
//    private val anchorView: View,
//
//    private val onAction :(MessageMenuAction) ->Unit
//    ): PopupWindow(context) {
//
//        // inflate xml
//        private val binding = BottomSheetMessageMenuBinding.inflate(
//            LayoutInflater.from(context)
//        )
//
//    fun showPopupWindow(){
//
//
//        // نحسب مساحة ال menu
//       contentView.measure(
//           View.MeasureSpec.UNSPECIFIED , View.MeasureSpec.UNSPECIFIED
//       )
//        val popupWidth = contentView.measuredWidth
//        val popupHeight = contentView.measuredHeight
//
//        // نحسب مساحة الرسالة نفسها
//        val location = IntArray(2)
//        anchorView.getLocationOnScreen(location)
//        val anchorViewX = location[0]
//        val anchorViewY = location[1]
//
//        // نحسب مساحة الشاشة
//        val displayMetrics = context.resources.displayMetrics
//        val screenWidth=displayMetrics.widthPixels
//        val screenHeight = displayMetrics.heightPixels
//
//        // المكان الابتدائي للعرض يمين يسار
//        var x :Int
//        // المكان الابتدائي الرئسي  يبدا من اعلا الرسالة نفسها
//        var y = anchorViewY
//        if (owner == MessageOwner.OTHER){
//            val msgEnd = anchorViewX + anchorView.width
//
//            x = msgEnd - popupWidth - 8
//
//        }else{
//           x= anchorViewX + anchorView.width + 8
//        }
//
//
//        // لايخرج من اليسار
//        if (x < 16){
//            x = 16
//        }
//        // لا يخرج من اليمين
//        if (x + popupWidth > screenWidth - 16){
//            x = screenWidth - popupWidth - 16
//        }
//
//        // اذا خرج من اسفل الشاشة يظهر في اعل الرسالة
//        if (y + popupHeight > screenHeight -16){
//            y= anchorViewY - popupHeight
//        }
//        // اذا خرج من اعل الشاشة
//        if (y < 16) y = 16
//
//        elevation = 12f
//
//        showAtLocation(
//            anchorView ,
//            Gravity.NO_GRAVITY,
//            x,
//            y
//        )
//    }
//
//
//
//    init {
//        contentView = binding.root  // خزنا في ال popupWindow  صفحة المراد عرضها من xml
//        // الطول والعرض يكون بحجم المحتوي
//         width = ViewGroup.LayoutParams.WRAP_CONTENT
//        height = ViewGroup.LayoutParams.WRAP_CONTENT
//
//        isOutsideTouchable = true // اذا ضغط خارج القائمة تغلق
//        isFocusable = true        // تعطيل الفوكس  وزر الرحوع يغلقها
//
//        setupViews()
//        setupClicks()
//    }
//
//
//    private fun setupViews() {
//        showMessageDate() // عرض التاريخ
//        setupMenuItems() // تنسيق الايقونات
//        updateVisibleItems() // اخفا واضهار العناصر بنائن عن ال owner
//    }
//    private fun updateVisibleItems(){
//        if (owner == MessageOwner.OTHER){
//            binding.itemEditIdBottomSheet.root.isVisible = false
//            binding.itemDeleteIdBottomSheet.root.isVisible = false
//        }
//        if (message.deleted){
//            binding.itemEditIdBottomSheet.root.isVisible = false
//            binding.itemCopyBottomSheet.root.isVisible = false
//            binding.itemDeleteIdBottomSheet.root.isVisible = false
//        }
//    }
//
//
//    private fun setupMenuItems() {
//        setupItem(binding.itemEditIdBottomSheet   , R.drawable.ic_edit_menu   , R.string.edit_menu)
//        setupItem(binding.itemCopyBottomSheet     , R.drawable.ic_copy_menu   , R.string.copy_menu)
//        setupItem(binding.itemDeleteIdBottomSheet , R.drawable.ic_delete_menu , R.string.delete_menu)
//    }
//
//    private fun setupItem(
//        item: ItemMessageOptionMenuBinding, @DrawableRes icon :Int, @StringRes text :Int
//    ){
//        item.imgOptionMenuId.setImageResource(icon)
//        item.tvNameItemOptionMenuId.setText(text)
//    }
//
//    private fun showMessageDate() {
//        binding.tvDateMenuBottomSheet.text= styleTime(message.timestamp)
//    }
//
//
//    private fun setupClicks() {
//        setupChick(binding.itemEditIdBottomSheet.root , MessageMenuAction.Edit(message))
//        setupChick(binding.itemCopyBottomSheet.root , MessageMenuAction.Copy(message.messageText))
//        setupChick(binding.itemDeleteIdBottomSheet.root , MessageMenuAction.Delete(message))
//    }
//    private fun  setupChick(
//        view:View , action:MessageMenuAction
//    ){
//        view.setOnClickListener {
//            onAction.invoke(action)
//            dismiss()
//        }
//    }
//}