package com.emi.commentdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class MessageViewModelFactory constructor(var message : Message) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MessageViewModel::class.java))
            return MessageViewModel(message) as T

        throw IllegalArgumentException("its not our model class")
    }
}