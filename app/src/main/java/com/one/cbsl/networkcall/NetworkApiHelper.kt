package com.one.cbsl.networkcall

import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager

class NetworkApiHelper(private val apiService: RetrofitService) {

    suspend fun loginWithDeviceId(
        Str_UserName: String?, Str_Password: String?, Str_Deviceid: String?
    ) = apiService.loginWithDeviceId(
        Str_UserName, Str_Password, Str_Deviceid, Constants.AUTH_HEADER
    )

    suspend fun getTodayAttendance(

    ) = apiService.getTodayAttendance(
        SessionManager.getInstance().getString(Constants.UserId), Constants.AUTH_HEADER
    )

    suspend fun checkDevice(
        userid: String, deviceId: String
    ) = apiService.checkDevice(
        SessionManager.getInstance().getString(Constants.UserId), deviceId, Constants.AUTH_HEADER
    )

    suspend fun getDashboardData(

    ) = apiService.getDashboardData(
        SessionManager.getInstance().getString(Constants.UserId), Constants.AUTH_HEADER
    )

    suspend fun getMyAttendance(date: String) = apiService.getMyAttendance(
        SessionManager.getInstance().getString(Constants.UserId), date, Constants.AUTH_HEADER
    )

    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String) =
        apiService.changePassword(
            SessionManager.getInstance().getString(Constants.UserId),
            currentPassword,
            newPassword,
            Constants.AUTH_HEADER
        )


    suspend fun getAttendanceType() = apiService.getAttendanceType(Constants.AUTH_HEADER)

    suspend fun punchInAttendance(
        locationAddress: String?,
        latitude: String?,
        longitude: String?,
        attendanceTypeId: String?,
        purpose: String?,
        clientName: String?
    ) = apiService.punchInAttendance(
        SessionManager.getInstance().getString(Constants.UserId),
        locationAddress,
        latitude,
        longitude,
        attendanceTypeId,
        purpose,
        clientName,
        Constants.AUTH_HEADER
    )

    suspend fun punchoutAttendancebyId(
        logOutLocation: String,
        typeId: String,
        latitude: String?,
        longitude: String?,
        purpose: String,
        client: String
    ) = apiService.punchoutAttendancebyLocation(
        SessionManager.getInstance().getString(Constants.UserId),
        logOutLocation,
        typeId,
        latitude,
        longitude,
        purpose,
        client,
        Constants.AUTH_HEADER
    )

    suspend fun syncOfflineAttendance(
        userid: String,
        attendanceTypeId: String,
        PunchDate: String,
        PunchIn: String,
        locationAddress: String,
        latitude: String,
        longitude: String,
        CreatedOn: String,
        PunchoutDate: String,
        logoutlocation: String
    ) = apiService.syncOfflineAttendance(
        userid,
        attendanceTypeId,
        PunchDate,
        PunchIn,
        locationAddress,
        latitude,
        longitude, "", "", "", "",
        CreatedOn,
        PunchoutDate, logoutlocation,
        Constants.AUTH_HEADER
    )


    suspend fun getMyMovementData(date: String) = apiService.getMyMovementData(
        SessionManager.getInstance().getString(Constants.UserId), "", Constants.AUTH_HEADER
    )

    suspend fun startMovement(
        locationAddress: String, latitude: String, longitude: String, type: String
    ) = apiService.startMovement(
        SessionManager.getInstance().getString(Constants.UserId),
        locationAddress,
        latitude,
        longitude,
        type,
        Constants.AUTH_HEADER
    )

    suspend fun checkInMovement(
        fromLocation: String,
        toLocation: String,
        taskId: String,
        reason: String,
        locationAddress: String,
        latitude: String,
        longitude: String,
        tourid: String
    ) = apiService.checkInMovement(
        SessionManager.getInstance().getString(Constants.UserId),
        fromLocation,
        toLocation,
        taskId,
        reason,
        locationAddress,
        latitude,
        longitude,
        tourid,
        Constants.AUTH_HEADER
    )

    suspend fun saveMovementDataNew(
        userId: String?,
        fromLocation: String?,
        toLocation: String?,
        taskId: String?,
        reason: String?,
        locationAddress: String?,
        latitude: String?,
        longitude: String?,
        tourId: String,
        complaintno: String,
        machineno: String,
        clientname: String,
        clientplacename: String,
        complainttype: String
    ) =
        apiService.MovementCheckInNew(
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
        movementCode: String, toLocation: String, latitude: String, longitude: String
    ) = apiService.movementCheckout(
        SessionManager.getInstance().getString(
            Constants.UserId
        ), movementCode, toLocation, latitude, longitude, Constants.AUTH_HEADER
    )

    suspend fun completeTour() = apiService.completeTour(
        SessionManager.getInstance().getString(Constants.UserId), Constants.AUTH_HEADER
    )

    suspend fun getTask() = apiService.getTask(
        SessionManager.getInstance().getString(Constants.UserId),
        Constants.AUTH_HEADER
    )

    suspend fun getLastLocation() = apiService.getLastLocation(
        SessionManager.getInstance().getString(Constants.UserId), Constants.AUTH_HEADER
    )

    suspend fun getMyConveyanceData(date: String) = apiService.getMyConveyanceData(
        SessionManager.getInstance().getString(Constants.UserId), date, Constants.AUTH_HEADER
    )

    suspend fun getVoucher(date: String) = apiService.getVoucher(
        SessionManager.getInstance().getString(Constants.UserId), date, Constants.AUTH_HEADER
    )

    suspend fun getMyTourConveyance(userId: String?, date: String?, tourid: String) =
        apiService.getMyTourConveyance(userId, date, tourid, Constants.AUTH_HEADER)

    suspend fun getMyTourLog(userId: String?, date: String?, tourid: String) =
        apiService.getMyTourLog(userId, date, tourid, Constants.AUTH_HEADER)

    suspend fun getTourId(userid: String, date: String, tourId: String, type: String) =
        apiService.getTourId(
            userid, date, tourId, type, Constants.AUTH_HEADER
        )

    suspend fun getTourDate(userid: String, date: String, tourId: String, type: String) =
        apiService.getTourDate(userid, date, tourId, type, Constants.AUTH_HEADER)

    suspend fun getTourMovement(
        userid: String, date: String, tourId: String, type: String
    ) = apiService.getTourMovement(userid, date, tourId, type, Constants.AUTH_HEADER)

    suspend fun getTransportMode() = apiService.getTransportMode(Constants.AUTH_HEADER)

    suspend fun getprojectName(type: String) =
        apiService.getProjectName(
            type,
            SessionManager.getInstance().getString(Constants.COMPANY),
            SessionManager.getInstance().getString(Constants.UserId),
            Constants.AUTH_HEADER
        )

    suspend fun getPendingMovement() = apiService.getPendingMovement(
        SessionManager.getInstance().getString(Constants.UserId), Constants.AUTH_HEADER
    )

    suspend fun getBankName(type: String) =
        apiService.getBankName(
            "PWISE",
            type,
            SessionManager.getInstance().getString(Constants.UserId),
            Constants.AUTH_HEADER
        )

    suspend fun getCompanyName(type: String) =
        apiService.getCompanyName(
            "BWISE",
            type,
            SessionManager.getInstance().getString(Constants.UserId),
            Constants.AUTH_HEADER
        )

    suspend fun getMyLeavePlan(userId: String?, type: Int) =
        apiService.getMyLeavePlan(userId, type, Constants.AUTH_HEADER)

    suspend fun saveLeavePlan(
        userId: String?, fromDate: String?, toDate: String?, Reason: String?, leavetype: String?
    ) = apiService.saveLeavePlan(userId, fromDate, toDate, Reason, leavetype, Constants.AUTH_HEADER)


    suspend fun getHodFacilityWiseConveyance(fromDate: String, toDate: String) =
        apiService.getHodFacilityWiseConveyance(
            SessionManager.getInstance().getString(Constants.UserId),
            fromDate,
            toDate,
            Constants.AUTH_HEADER
        )

    suspend fun getHodFacilityEmployeeList(
        facilityId: String, status: String, fromDate: String, todate: String
    ) = apiService.getHodFacilityEmployeeList(
        SessionManager.getInstance().getString(Constants.UserId),
        facilityId,
        status,
        fromDate,
        todate,
        Constants.AUTH_HEADER
    )

    suspend fun getfinalHodEmpConveyanceDetail(
        userid: String?, status: String?, fromDate: String?, todate: String?
    ) = apiService.getfinalHodEmpConveyanceDetail(
        userid, status, fromDate, todate, Constants.AUTH_HEADER
    )

    suspend fun updatefinalHodEmpConveyance(
        conId: String, status: String, amt: String, remarks: String
    ) = apiService.updatefinalHodEmpConveyance(
        conId, status, amt, remarks, Constants.AUTH_HEADER
    )

    suspend fun getHeadFacilityWiseConveyance(fromDate: String, toDate: String) =
        apiService.getHeadFacilityWiseConveyance(
            SessionManager.getInstance().getString(Constants.UserId),
            fromDate,
            toDate,
            Constants.AUTH_HEADER
        )

    suspend fun getHeadFacilityEmployeeList(
        facilityId: String, status: String, fromDate: String, todate: String
    ) = apiService.getHeadFacilityEmployeeList(
        SessionManager.getInstance().getString(Constants.UserId),
        facilityId,
        status,
        fromDate,
        todate,
        Constants.AUTH_HEADER
    )

    suspend fun getfinalHeadEmpConveyanceDetail(
        userid: String?, status: String?, fromDate: String?, todate: String?
    ) = apiService.getfinalHeadEmpConveyanceDetail(
        userid, status, fromDate, todate, Constants.AUTH_HEADER
    )

    suspend fun updatefinalHeadEmpConveyance(
        conId: String, status: String, amt: String, remarks: String
    ) = apiService.updatefinalHeadEmpConveyance(
        conId, status, amt, remarks, Constants.AUTH_HEADER
    )

    //Complaint Module

    suspend fun getComplaintDashboardData(

    ) = apiService.getComplaintDashboardData(
        SessionManager.getInstance().getString(Constants.EmpCode),
        "UT"
    )

    suspend fun getClientDetails(
        types: String,
        machineNo: String,
        clientId: String,
    ) = apiService.getClientDetails(
        SessionManager.getInstance().getString(Constants.COMPLAINT_USERID),
        types, machineNo, clientId
    )

    suspend fun getComplainDetails(
        userid: String,
        types: String,
        machineNo: String,
        clientId: String,
    ) = apiService.getComplainDetails(
        userid, types, machineNo, clientId
    )

    suspend fun getComplainType(
    ) = apiService.getComplainType(
        "Complaint"
    )


    suspend fun getCustomerName(
        userid: String, cityId: String
    ) = apiService.getCustomerName(
        userid, cityId
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
    ) = apiService.assignResolveComplaint(
        complainID,
        complainType,
        assignTo,
        assignDate,
        ComplaintTypeId,
        ItemId,
        ComplaintDetails,
        ComplaintChangeStatus,
        cby,
        check
    )

    suspend fun addComplain(
        poId: String?,
        complainTypeId: String?,
        detail: String,
        cby: String,
        advance: String,
        itemId: String?,
        assignTo: String?,
        assignDate: String?
    ) = apiService.addComplain(
        poId, complainTypeId, detail, cby, advance, itemId, assignTo, assignDate
    )

    suspend fun getMachineItem(
        machine_no: String
    ) = apiService.getMachineItem(
        machine_no
    )

    suspend fun getEngineer(
        poid: String
    ) = apiService.getEngineer(
        SessionManager.getInstance().getString(Constants.COMPLAINT_USERID), poid
    )

    suspend fun getComplaintStatus(
        type: String
    ) = apiService.getComplaintStatus(
        type
    )

    suspend fun getPendingReason(
        type: String
    ) = apiService.getPendingReason(
        type
    )

    suspend fun getPendingOwner(
        type: String
    ) = apiService.getPendingOwner(
        type
    )

    suspend fun getMachineDetails(
        types: String, machineNo: String, clientId: String
    ) = apiService.getMachineDetails(
        SessionManager.getInstance().getString(Constants.COMPLAINT_USERID),
        types,
        machineNo,
        clientId
    )

    suspend fun getPendingInstallation(
        thid: String, seid: String, clientId: String, parentId: String
    ) = apiService.getPendingInstallation(
        thid, seid, parentId, clientId
    )

    suspend fun getRegisterComplainDetail(
        cid: String
    ) = apiService.getRegisterComplainDetail(
        cid
    )

    suspend fun getComplaintLog(complaintID: String) = apiService.getComplaintLog(complaintID)


}