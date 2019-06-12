package com.emi.commentdemo

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(var name : String?="", var userEmail : String?="", var userUid: String?="", var photo : String?="")
