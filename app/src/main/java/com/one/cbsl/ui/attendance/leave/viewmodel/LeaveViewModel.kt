package com.one.cbsl.ui.attendance.leave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import kotlinx.coroutines.Dispatchers

class LeaveViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun getMyLeavePlan(type: Int) = liveData(
        Dispatchers.IO
    ) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getMyLeavePlan(
                SessionManager.getInstance().getString(
                    Constants.UserId
                ),
                type
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

    fun leavePlanSave(
        fromDate: String,
        toDate: String,
        reason: String,
        leavetype: String
    ) = liveData(
        Dispatchers.IO
    ) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.saveLeavePlan(
                SessionManager.getInstance().getString(
                    Constants.UserId
                ),
                fromDate, toDate, reason, leavetype
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

    fun LeavePlanUpdate(userid: String, id: String, leave_userid: String, status: String) =
        liveData(
            Dispatchers.IO
        ) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.LeavePlanUpdate(
                    userid,
                    id, leave_userid, status
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