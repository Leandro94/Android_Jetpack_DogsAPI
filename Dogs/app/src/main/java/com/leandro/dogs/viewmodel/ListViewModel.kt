package com.leandro.dogs.viewmodel

import android.app.Application
import android.app.Notification
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.leandro.dogs.model.DogBreed
import com.leandro.dogs.model.DogDatabase
import com.leandro.dogs.model.DogsApiService
import com.leandro.dogs.util.NotificationsHelper
import com.leandro.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime =  5*60*1000*1000*1000L

    private val dogsService =  DogsApiService()
    private val disposable =  CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(){
        checkCacheDuration()
        val updateTime =  prefHelper.getUpdateTime()
        if(updateTime !=null && updateTime !=0L && System.nanoTime()-updateTime< refreshTime){
            fetchFromDatabse()
        }
        fetchFromRemote()
    }
    private fun checkCacheDuration(){
        val cachePreference = prefHelper.getCacheDuration()
        try{
            val cachePreferenceInt = cachePreference?.toInt()?:5*60
            refreshTime = cachePreferenceInt.times(1000*1000*1000L)
        }
        catch (e: NumberFormatException){
            e.printStackTrace()
        }
    }
    fun refreshBypassCache(){
        fetchFromRemote()
    }
    private fun fetchFromDatabse(){
        loading.value = true
        launch {
            val dogs =  DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show()

        }
    }
    private fun fetchFromRemote(){
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(), "Dogs retrieved from endpoint", Toast.LENGTH_SHORT).show()
                        NotificationsHelper(getApplication()).createNotification()
                    }
                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }
    private fun dogsRetrieved(dogList: List<DogBreed>){
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreed>){
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*list.toTypedArray())
            var i =0
            while(i<list.size){
                list[i].uuid=result[i].toInt()
                i++
            }
            dogsRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}