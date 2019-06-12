package com.emi.commentdemo

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.gms.common.SignInButton
import com.squareup.picasso.Picasso

object BindingMethod {
    @BindingAdapter("android:onClick")
    @JvmStatic fun bindSignInCLick(button: SignInButton, method: () -> Unit) {
        button.setOnClickListener { method.invoke() }
    }


    @BindingAdapter("bind:srcCompat")
    @JvmStatic
    fun loadImage(view: ImageView, photo: String?) {
         Picasso.with(view.context).load(photo)
                .resize(32, 32)
                .centerCrop().into(view)

    }
}