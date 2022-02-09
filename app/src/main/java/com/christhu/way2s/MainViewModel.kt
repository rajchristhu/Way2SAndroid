package com.christhu.way2s

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel constructor(private val repository: MainRepository)  : ViewModel() {

    val dataList = MutableLiveData<List<ModelClass>>()
    val errorMessage = MutableLiveData<String>()

    fun getAllData() {

        val response = repository.getAllData()
        response.enqueue(object : Callback<List<ModelClass>> {
            override fun onResponse(call: Call<List<ModelClass>>, response: Response<List<ModelClass>>) {
                dataList.postValue(response.body())
            }

            override fun onFailure(call: Call<List<ModelClass>>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}