package com.one.cbsl.networkcall

import androidx.lifecycle.liveData
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import kotlinx.coroutines.Dispatchers

class MainRepository constructor(private val apiHelper: NetworkApiHelper) {

    suspend fun getLoginResponse(employeeCode: String, password: String, deviceId: String) =
        apiHelper.loginWithDeviceId(employeeCode, password, deviceId)

    suspend fun getTodayAttendance() =
        apiHelper.getTodayAttendance()

    suspend fun checkDevice() =
        apiHelper.checkDevice(
            SessionManager.getInstance().getString(Constants.UserId),
            SessionManager.getInstance().getString(Constants.DEVICE_ID)
        )

    suspend fun getDashboardData() =
        apiHelper.getDashboardData()

    //Attendance
    suspend fun getMyAttendance(date: String) =
        apiHelper.getMyAttendance(date)

    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String) =
        apiHelper.changePassword(userId, currentPassword, newPassword)
 suspend fun markAttendance(userId: String, hodId: String, date: String, status: String) =
        apiHelper.markAttendance(userId, hodId,date, status)

    suspend fun getAttendanceType() =
        apiHelper.getAttendanceType()

    suspend fun punchInAttendance(
        locationAddress: String?,
        latitude: String?,
        longitude: String?,
        attendanceTypeId: String?, purpose: String?,
        clientName: String?
    ) =
        apiHelper.punchInAttendance(
            locationAddress,
            latitude,
            longitude,
            attendanceTypeId, purpose,
            clientName
        )

    suspend fun punchoutAttendancebyId(
        logoutLocation: String,
        typeId: String,
        latitude: String?,
        longitude: String?,
        purpose: String,
        clientName: String
    ) =
        apiHelper.punchoutAttendancebyId(
            logoutLocation, typeId, latitude,
            longitude, purpose, clientName
        )

    suspend fun syncOfflineAttendance(
        userid: String,
        attendanceTypeId: String,
        PunchDate: String,
        PunchIn: String,
        locationAddress: String,
        latitude : String,
        longitude: String,
        CreatedOn: String,
        PunchoutDate: String,
        logoutlocation: String
    ) =
        apiHelper.syncOfflineAttendance(
            userid,
            attendanceTypeId,
            PunchDate,
            PunchIn,
            locationAddress,
            latitude,
            longitude,
            CreatedOn,
            PunchoutDate, logoutlocation
        )

    //movement
    suspend fun getMyMovementData(date: String) =
        apiHelper.getMyMovementData(date)

    suspend fun startMovement(
        locationAddress: String,
        latitude: String,
        longitude: String,
        type: String
    ) =
        apiHelper.startMovement(locationAddress, latitude, longitude, type)


    suspend fun checkInMovement(
        fromLocation: String,
        toLocation: String,
        taskId: String,
        reason: String,
        locationAddress: String,
        latitude: String,
        longitude: String,
        tourid: String
    ) =
        apiHelper.checkInMovement(
            fromLocation, toLocation,
            taskId, reason, locationAddress, latitude, longitude, tourid
        )

    suspend fun saveMovementDataNew(
        userId: String?, fromLocation: String?, toLocation: String?,
        taskId: String?, reason: String?, locationAddress: String?,
        latitude: String?, longitude: String?, tourId: String, complaintno: String,
        machineno: String,
        clientname: String,
        clientplacename: String,
        complainttype: String
    ) =
        apiHelper.saveMovementDataNew(
            userId,
            fromLocation,
            toLocation,
            taskId,
            reason,
            locationAddress,
            latitude,
            longitude, tourId, complaintno, machineno, clientname, clientplacename, complainttype
        )

    suspend fun movementCheckout(
        movementCode: String,
        toLocation: String,
        latitude: String,
        longitude: String
    ) =
        apiHelper.movementCheckout(
            movementCode, toLocation, latitude, longitude
        )

    suspend fun completeTour() =
        apiHelper.completeTour()

    suspend fun getTask() =
        apiHelper.getTask()

    suspend fun getLastLocation() =
        apiHelper.getLastLocation()

    suspend fun getMyConveyanceData(date: String) =
        apiHelper.getMyConveyanceData(date)

    suspend fun getVoucher(date: String) =
        apiHelper.getVoucher(date)

    suspend fun getPayHistory(date: String) =
        apiHelper.getPayHistory(date)

    suspend fun getMyTourConveyance(userId: String?, date: String?, tourid: String) =
        apiHelper.getMyTourConveyance(userId, date, tourid)


    suspend fun getMyTourLog(userId: String?, date: String?, tourid: String) =
        apiHelper.getMyTourLog(userId, date, tourid)

    suspend fun getTourId(userid: String, date: String, tourId: String, type: String) =
        apiHelper.getTourId(userid, date, tourId, type)

    suspend fun getTourDate(userid: String, date: String, tourId: String, type: String) =
        apiHelper.getTourDate(userid, date, tourId, type)

    suspend fun getTourMovement(userid: String, date: String, tourId: String, type: String) =
        apiHelper.getTourMovement(userid, date, tourId, type)


    suspend fun getTransportMode() = apiHelper.getTransportMode()
    suspend fun getPendingMovement() = apiHelper.getPendingMovement()


    suspend fun getProjectName(type: String) = apiHelper.getprojectName(type)


    suspend fun getBankName(type: String) = apiHelper.getBankName(type)


    suspend fun getCompanyName(type: String) = apiHelper.getCompanyName(type)


    suspend fun getMyLeavePlan(userId: String?, type: Int) = apiHelper.getMyLeavePlan(userId, type)


    suspend fun saveLeavePlan(
        userId: String?,
        fromDate: String?,
        toDate: String?,
        reason: String?,
        leavetype: String?
    ) = apiHelper.saveLeavePlan(
        userId,
        fromDate,
        toDate,
        reason,
        leavetype
    )

    suspend fun LeavePlanUpdate(userid: String, id: String, leave_userid: String, status: String) =
        apiHelper.LeavePlanUpdate(
            userid,
            id, leave_userid, status
        )

    suspend fun getHodFacilityWiseConveyance(fromDate: String, toDate: String) =
        apiHelper.getHodFacilityWiseConveyance(fromDate, toDate)

    suspend fun getHodFacilityEmployeeList(
        facilityId: String,
        status: String,
        fromDate: String,
        todate: String
    ) = apiHelper.getHodFacilityEmployeeList(
        facilityId, status, fromDate,
        todate
    )
    suspend fun getHodFacilityWiseAttendance(fromDate: String, toDate: String) =
        apiHelper.getHodFacilityWiseAttendance(fromDate, toDate)

    suspend fun getHodFacilityEmployeeListAttendance(
        facilityId: String,
        status: String,
        fromDate: String,
        todate: String
    ) = apiHelper.getHodFacilityEmployeeListAttendance(
        facilityId, status, fromDate,
        todate
    )

    suspend fun getfinalHodEmpConveyanceDetail(
        userid: String,
        status: String,
        fromDate: String,
        todate: String
    ) = apiHelper.getfinalHodEmpConveyanceDetail(
        userid, status, fromDate,
        todate
    )

    suspend fun updatefinalHodEmpConveyance(
        conId: String,
        status: String,
        amt: String,
        remarks: String
    ) = apiHelper.updatefinalHodEmpConveyance(
        conId, status, amt,
        remarks
    )

    suspend fun getHeadFacilityWiseConveyance(fromDate: String, toDate: String) =
        apiHelper.getHeadFacilityWiseConveyance(fromDate, toDate)

    suspend fun getHeadFacilityEmployeeList(
        facilityId: String,
        status: String,
        fromDate: String,
        todate: String
    ) = apiHelper.getHeadFacilityEmployeeList(
        facilityId, status, fromDate,
        todate
    )

    suspend fun getFinalHeadEmpConveyanceDetail(
        userid: String,
        status: String,
        fromDate: String,
        todate: String
    ) = apiHelper.getfinalHeadEmpConveyanceDetail(
        userid, status, fromDate,
        todate
    )

    suspend fun updateFinalHeadEmpConveyance(
        conId: String,
        status: String,
        amt: String,
        remarks: String
    ) = apiHelper.updatefinalHeadEmpConveyance(
        conId, status, amt,
        remarks
    )


//Complaint Module


    suspend fun getComplaintDashboardData() =
        apiHelper.getComplaintDashboardData()


    suspend fun getComplainDetails(
        types: String,
        machineNo: String,
        clientId: String,
    ) = apiHelper.getComplainDetails(
        SessionManager.getInstance().getString(Constants.COMPLAINT_USERID),
        types,
        machineNo,
        clientId
    )

    suspend fun getClientDetails(
        types: String,
        machineNo: String,
        clientId: String,
    ) = apiHelper.getClientDetails(
        types,
        machineNo,
        clientId
    )

    suspend fun getCustomerName(
        userid: String,
        cityId: String
    ) = apiHelper.getCustomerName(
        userid,
        cityId
    )


    suspend fun getRegisterComplainDetail(
        cid: String
    ) = apiHelper.getRegisterComplainDetail(
        cid
    )

    suspend fun getMachineItem(
        machine_no: String
    ) = apiHelper.getMachineItem(
        machine_no
    )

    suspend fun getEngineer(
        poId: String
    ) = apiHelper.getEngineer(
        poId
    )

    suspend fun getComplaintStatus(
        type: String
    ) = apiHelper.getComplaintStatus(
        type
    )

    suspend fun getPendingReason(
        type: String
    ) = apiHelper.getPendingReason(
        type
    )

    suspend fun getPendingOwner(
        type: String
    ) = apiHelper.getPendingOwner(
        type
    )

    suspend fun getComplaintLog(complaintID: String) =
        apiHelper.getComplaintLog(complaintID)

    suspend fun getComplainType(
    ) = apiHelper.getComplainType(

    )

    suspend fun getPendingInstallation(
        thid: String,
        seid: String,
        clientId: String,
        parentId: String,
    ) = apiHelper.getPendingInstallation(
        thid,
        seid,
        clientId,
        parentId
    )

    suspend fun assignResolveComplaint(
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
    ) = apiHelper.assignResolveComplaint(
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


    suspend fun addComplain(
        poId: String?,
        complainTypeId: String?,
        detail: String,
        cby: String,
        advance: String,
        itemId: String?,
        assignto: String?,
        assigndate: String?
    ) = apiHelper.addComplain(
        poId, complainTypeId, detail, cby, advance, itemId, assignto, assigndate
    )

}

