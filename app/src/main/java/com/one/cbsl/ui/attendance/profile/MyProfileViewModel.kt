package com.one.cbsl.ui.attendance.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Resource
import kotlinx.coroutines.Dispatchers

class MyProfileViewModel(private val mainRepository: MainRepository) : ViewModel() {


    fun changePassword(userId: String, currentPassword: String, newPassword: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.changePassword(userId, currentPassword, newPassword)

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

