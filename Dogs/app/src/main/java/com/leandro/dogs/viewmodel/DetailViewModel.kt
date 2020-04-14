package com.leandro.dogs.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.leandro.dogs.model.DogBreed
import com.leandro.dogs.model.DogDatabase
import kotlinx.coroutines.launch
import java.util.*

class DetailViewModel(application: Application): BaseViewModel(application ) {
    val dogLiveData = MutableLiveData<DogBreed>()

    fun fetch(uuid: Int){
        Log.d("XXX","id no view model "+uuid)
        launch {
            val dog =  DogDatabase(getApplication()).dogDao().getDog(uuid)
            dogLiveData.value = dog

        }

    }
}