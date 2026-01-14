package com.one.cbsl.ui.complain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.utils.Resource
import kotlinx.coroutines.Dispatchers

class ComplaintViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {


    fun getComplaintDashboardData() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getComplaintDashboardData()
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getComplainDetails(
        types: String,
        machineNo: String,
        clientId: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getComplainDetails(
                    types,
                    machineNo,
                    clientId

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

    fun getClientDetails(
        types: String,
        machineNo: String,
        clientId: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getClientDetails(
                    types,
                    machineNo,
                    clientId

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

    fun assignResolveComplaint(
        complainID: String,
        complainType: String,
        assignTo: String,
        assignDate: String,
        cby: String,
        remarks: String,
        check: String,
        attachment: String,
        ComplaintTypeId: String,
        ItemId: String,
        ComplaintDetails: String,
        AdvanceAmount: String,
        ComplaintChangeStatus: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.assignResolveComplaint(
                    complainID,
                    complainType,
                    assignTo,
                    assignDate,
                    cby,
                    remarks,
                    check, attachment,
                    ComplaintTypeId,
                    ItemId,
                    ComplaintDetails,
                    AdvanceAmount,
                    ComplaintChangeStatus
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

    fun getCustomerName(
        userid: String,
        cityId: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getCustomerName(
                    userid,
                    cityId
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

    fun addComplain(
        poId: String?,
        complainTypeId: String?,
        detail: String,
        cby: String,
        advance: String,
        itemId: String?,
        assignto: String?,
        assigndate: String?
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.addComplain(
                    poId,
                    complainTypeId,
                    detail,
                    cby,
                    advance,
                    itemId,
                    assignto,
                    assigndate
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

    fun getComplainType(

    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getComplainType()
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getRegisterComplainDetail(
        cid: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getRegisterComplainDetail(
                    cid
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

    fun getMachineItem(
        machine_no: String
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getMachineItem(
                    machine_no
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

    fun getEngineer(
        poid: String
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getEngineer(
                    poid
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

    fun getComplaintStatus(
        type: String
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getComplaintStatus(
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

    fun getPendingReason(type: String) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getPendingReason(
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

    fun getPendingOwner(type: String) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getPendingOwner(
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

    fun getComplaintLog(complaintID: String) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getComplaintLog(complaintID)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.Error(message = "Error: ${response.message()}"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message ?: "Error Occurred!"))
            }
        }
    fun getPendingInstallation(
        thid: String,
        seid: String,
        clientId: String,
        parentId: String,
    ) =
        liveData(Dispatchers.IO)
        {
            emit(Resource.Loading())
            try {
                val response = mainRepository.getPendingInstallation(
                    thid,
                    seid,
                    clientId,
                    parentId
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