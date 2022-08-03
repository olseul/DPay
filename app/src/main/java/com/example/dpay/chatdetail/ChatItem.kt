package com.example.dpay.chatdetail

data class ChatItem (
    val senderId: String,
    val message: String
        ) {
    constructor(): this("","")
}