package com.one.cbsl.ui.attendance.other.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Resource
import kotlinx.coroutines.*

class LoginViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {


    fun getLoginResponse(employeeCode: String, password: String, deviceId: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getLoginResponse(employeeCode, password, deviceId)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

}