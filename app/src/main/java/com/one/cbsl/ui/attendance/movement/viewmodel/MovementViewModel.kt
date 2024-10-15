package com.one.cbsl.ui.attendance.movement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Resource
import kotlinx.coroutines.Dispatchers

class MovementViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun getMyMovementData(date: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getMyMovementData(date)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun startMovement(locationAddress: String, latitude: String, longitude: String, type: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.startMovement(locationAddress, latitude, longitude, type)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun checkInMovement(
        fromLocation: String,
        toLocation: String,
        taskId: String,
        reason: String,
        locationAddress: String,
        latitude: String,
        longitude: String,
        tourid: String
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.checkInMovement(
                        fromLocation, toLocation,
                        taskId, reason, locationAddress, latitude, longitude, tourid
                    )
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }
    fun checkInMovement(
        userId: String,
        fromLocation: String,
        toLocation: String,
        taskId: String,
        reason: String,
        locationAddress: String,
        latitude: String,
        longitude: String,
        tourid: String,
        complaintno: String,
        machineno: String,
        clientname: String,
        clientplacename: String,
        complainttype: String
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.saveMovementDataNew(
                        userId, fromLocation, toLocation,
                        taskId, reason, locationAddress, latitude, longitude,tourid,complaintno,machineno,clientname,clientplacename,complainttype
                    )
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun movementCheckout(
        movementCode: String,
        toLocation: String,
        latitude: String,
        longitude: String
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.movementCheckout(
                        movementCode, toLocation,
                        latitude, longitude
                    )
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun completeTour() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.completeTour()
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getTask() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.getTask()
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getLastLocation() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response =
                    mainRepository.getLastLocation()
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