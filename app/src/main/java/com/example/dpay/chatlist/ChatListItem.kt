package com.example.dpay.chatlist

data class ChatListItem (
    val readerId: String,
    val writerId: String,
    val title: String,
    val key: Long
    ){

    constructor(): this("", "", "", 0)
}