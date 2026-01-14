package com.one.cbsl.ui.attendance.conveyance.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import kotlinx.coroutines.Dispatchers

class ConveyanceViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun getMyConveyanceData(date: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getMyConveyanceData(date)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getVoucher(date: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getVoucher(date)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getPayHistory(date: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getPayHistory(date)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getMyTourConveyance(date: String, tourId: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getMyTourConveyance(
                    SessionManager.getInstance().getString(
                        Constants.UserId
                    ), date, tourId
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

    fun getMyTourLog(date: String, tourId: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getMyTourLog(
                    SessionManager.getInstance().getString(
                        Constants.UserId
                    ), date, tourId
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

    fun getTransportMode() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getTransportMode(
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

    fun getPendingMovement() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getPendingMovement(
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

    fun getTourMovement(date: String, tourId: String, type: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getTourMovement(
                SessionManager.getInstance().getString(
                    Constants.UserId
                ), date, tourId, type
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

    fun getTourId(date: String, tourId: String, type: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getTourId(
                SessionManager.getInstance().getString(
                    Constants.UserId
                ), date, tourId, type
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

    fun getTourDate(date: String, tourId: String, type: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getTourDate(
                SessionManager.getInstance().getString(
                    Constants.UserId
                ), date, tourId, type
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

    fun getBankName(type: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getBankName(
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

    fun getCompanyName(type: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getCompanyName(
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

    fun getProjectName(type: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getProjectName(
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

    fun getHodFacilityWiseConveyance(fromDate: String, to: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getHodFacilityWiseConveyance(
                fromDate, to
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

    fun getHodFacilityEmployeeList(
        facilityId: String,
        status: String,
        fromDate: String,
        todate: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getHodFacilityEmployeeList(
                facilityId, status, fromDate,
                todate
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

    fun getFinalHodEmpConveyanceDetail(
        userid: String,
        status: String,
        fromDate: String,
        todate: String
    ) = liveData(
        Dispatchers.IO
    ) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getfinalHodEmpConveyanceDetail(
                userid, status, fromDate,
                todate
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

    fun updateFinalHodEmpConveyance(conId: String, status: String, amt: String, remarks: String) =
        liveData(
            Dispatchers.IO
        ) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.updatefinalHodEmpConveyance(
                    conId, status, amt,
                    remarks
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

    fun getHeadFacilityWiseConveyance(fromDate: String, to: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getHeadFacilityWiseConveyance(
                fromDate, to
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

    fun getHeadFacilityEmployeeList(
        facilityId: String,
        status: String,
        fromDate: String,
        todate: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getHeadFacilityEmployeeList(
                facilityId, status, fromDate,
                todate
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

    fun getFinalHeadEmpConveyanceDetail(
        userid: String,
        status: String,
        fromDate: String,
        todate: String
    ) = liveData(
        Dispatchers.IO
    ) {
        emit(Resource.Loading())
        try {
            val response = mainRepository.getFinalHeadEmpConveyanceDetail(
                userid, status, fromDate,
                todate
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

    fun updateFinalHeadEmpConveyance(conId: String, status: String, amt: String, remarks: String) =
        liveData(
            Dispatchers.IO
        ) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.updateFinalHeadEmpConveyance(
                    conId, status, amt,
                    remarks
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