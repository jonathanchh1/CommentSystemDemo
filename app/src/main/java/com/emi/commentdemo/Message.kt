package com.emi.commentdemo

import android.net.Uri
import androidx.databinding.ObservableField
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.sql.Timestamp


data  class Message(
    var uuid : String="",
    var name : String= "",
    var comment: String="",
    var photo : String="",
    var timestamp: String=""
)




