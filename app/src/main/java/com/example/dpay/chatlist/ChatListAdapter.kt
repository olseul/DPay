package com.example.dpay.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dpay.databinding.ItemChatListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit): ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil) {

    private lateinit var userDB: DatabaseReference

    inner class ViewHolder(private val binding: ItemChatListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatListItem: ChatListItem) {
            // 채팅 목록(전체)을 눌렀을 때
            binding.root.setOnClickListener {
                onItemClicked(chatListItem)
            }
            userDB = Firebase.database.reference.child("Users")
            val currentUserDB = userDB.child(chatListItem.writerId)
            currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.chatRoomWriterIdTextView.text = snapshot.child("name").value.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object  : DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }

        }
    }


}