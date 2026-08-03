package com.example.chatapp.b_user_list.adabter_user_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.databinding.ItemListUserBinding

class AdapterUserList(private val onUserClick:(User)->Unit) :
    ListAdapter<User , AdapterUserList . HolderUserList>(UserListDiffUtil()) {

   class UserListDiffUtil : DiffUtil.ItemCallback<User>(){
       override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
           return oldItem.uid == newItem.uid
       }
       override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
          return oldItem == newItem
       }
   }
    class HolderUserList(private val binding: ItemListUserBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun build(user:User){
            binding.tvNameItemUserList.text = user.name
            binding.tvBioItemListUserId.text = user.bio
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserList {
        val inflate = ItemListUserBinding .inflate(LayoutInflater.from(parent.context)
            ,parent,false)
        return HolderUserList(inflate)
    }
    override fun onBindViewHolder(holder: HolderUserList, position: Int) {
        val users = getItem(position)
        holder.build(users)
        holder.itemView.setOnClickListener {
            onUserClick(users)
        }
    }
}