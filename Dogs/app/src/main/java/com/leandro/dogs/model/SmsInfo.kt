package com.leandro.dogs.model

data class SmsInfo(
    var to: String,
    var text: String,
    var imageUrl: String?
)