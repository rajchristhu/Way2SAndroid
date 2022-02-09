package com.christhu.way2s

class MainRepository constructor(private val retrofitService: RetrofitService) {

    fun getAllData() = retrofitService.getData()
}