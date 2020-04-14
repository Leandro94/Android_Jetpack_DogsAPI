package com.leandro.dogs.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leandro.dogs.R

val PERMISSION_SEND_SMS =  234
@SuppressLint("ResourceAsColor")
fun getProgressDrawble(context: Context): CircularProgressDrawable{
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        this.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN)
        //this.setColorFilter(0xFFFF0000.toInt(), android.graphics.PorterDuff.Mode.SRC_IN)
        start()
    }
}

fun ImageView.loadImage(uri: String?, progressDrawble: CircularProgressDrawable){
    val options = RequestOptions()
        .placeholder(progressDrawble)
        .error(R.mipmap.ic_dog_icon)
    Glide.with(context).setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}
@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?){
    view.loadImage(url, getProgressDrawble(view.context))
}