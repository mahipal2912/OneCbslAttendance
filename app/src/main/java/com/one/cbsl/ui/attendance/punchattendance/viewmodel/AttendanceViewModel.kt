package com.one.cbsl.ui.attendance.punchattendance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import kotlinx.coroutines.Dispatchers

class AttendanceViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun getMyAttendanceList(date: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getMyAttendance(date)
            if (response.isSuccessful) {
                emit(Resource.Success(data = response.body()))
            } else {
                emit(Resource.Error(message = "Error: ${response.message()}"))
            }
        } catch (exception: Exception) {
            emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAttendanceType() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getAttendanceType()
            if (response.isSuccessful) {
                emit(Resource.Success(data = response.body()))
            } else {
                emit(Resource.Error(message = "Error: ${response.message()}"))
            }
        } catch (exception: Exception) {
            emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
        }
    }

    fun punchoutAttendancebyId(
        logOutLocation: String,
        typeId: String,
        latitude: String?,
        longitude: String,
        purpose: String,
        client: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.punchoutAttendancebyId(
                logOutLocation, typeId, latitude, longitude, purpose, client
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

    fun syncOfflineAttendance(
        attendanceTypeId: String,
        punchDate: String,
        punchIn: String,
        locationAddress: String,
        latitude: String,
        longitude: String,
        createdOn: String,
        punchOutDate: String,
        logoutLocation: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.syncOfflineAttendance(
                SessionManager.getInstance().getString(Constants.UserId),
                attendanceTypeId,
                punchDate,
                punchIn,
                locationAddress,
                latitude,
                longitude,
                createdOn,
                punchOutDate,
                logoutLocation
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

    fun punchInAttendance(
        locationAddress: String?,
        latitude: String?,
        longitude: String?,
        attendanceTypeId: String?,
        purpose: String?,
        clientName: String?
    ) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.punchInAttendance(
                locationAddress, latitude, longitude, attendanceTypeId, purpose, clientName
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

}
