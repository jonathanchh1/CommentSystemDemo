package com.emi.commentdemo

import androidx.lifecycle.ViewModel

class MessageViewModel constructor(var message : Message) : ViewModel(){

    fun name() = message.name
    fun photo() = message.photo
    fun uuid() = message.uuid
    fun comment() = message.comment
    fun timestamp() = message.timestamp
}