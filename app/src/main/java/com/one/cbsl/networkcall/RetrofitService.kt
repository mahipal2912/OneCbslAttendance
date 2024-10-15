package com.one.cbsl.networkcall

import MovementResponse
import MyLeaveResponse
import SaveResponse
import TourDateResponse
import TourIdResponse
import com.one.cbsl.ui.complain.model.*
import com.one.cbsl.ui.attendance.conveyance.model.*
import com.one.cbsl.ui.attendance.conveyance.model.ProjectResponse
import com.one.cbsl.ui.attendance.home.model.DashboardResponse
import com.one.cbsl.ui.attendance.movement.model.MovementListResponse
import com.one.cbsl.ui.attendance.movement.model.TaskResponse
import com.one.cbsl.ui.attendance.other.model.LoginModel
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceResponse
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceTypeResponse
import com.one.cbsl.ui.complain.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("LoginNew")
    suspend fun loginWithDeviceId(
        @Query("Str_UserName") Str_UserName: String?,
        @Query("Str_Password") Str_Password: String?,
        @Query("Str_DeviceId") Str_DeviceId: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<LoginModel>>

    @GET("GetAttendances")
    suspend fun getTodayAttendance(
        @Query("userId") userId: String?, @Query("AuthHeader") AuthHeader: String?
    ): Response<List<AttendanceResponse>>

    @GET("CheckDeviceId")
    suspend fun checkDevice(
        @Query("userid") userId: String?,
        @Query("Str_DeviceId") type: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<ProjectResponse>>

    @GET("LoadMyDashboard")
    suspend fun getDashboardData(
        @Query("userId") userId: String?, @Query("AuthHeader") AuthHeader: String?
    ): Response<List<DashboardResponse>>

    //Attendance
    @GET("UpdatePassword")
    suspend fun changePassword(
        @Query("User_ID") User_ID: String?,
        @Query("CurrPassword") CurrPassword: String?,
        @Query("NewPassword") NewPassword: String?,
        @Query("AuthHeader") header: String?
    ): Response<List<AttendanceResponse>>

    @GET("GetMyAttendanceListbydate")
    suspend fun getMyAttendance(
        @Query("userId") userId: String?,
        @Query("date") date: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<AttendanceResponse>>

    @GET("LoadAttendanceTypes")
    suspend fun getAttendanceType(
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<AttendanceTypeResponse>>

    @GET("punchoutAttendanceLocationWise")
    suspend fun punchoutAttendancebyLocation(
        @Query("userId") userId: String?,
        @Query("LogoutLocation") logoutLocation: String?,
        @Query("typeid") typeid: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        @Query("purpose") purpose: String?,
        @Query("clientName") clientName: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<AttendanceResponse>>

    @GET("syncOfflinAttendance")
    suspend fun syncOfflineAttendance(
        @Query("userId") userid: String,
        @Query("attendanceTypeId") attendanceTypeId: String,
        @Query("PunchDate") PunchDate: String,
        @Query("PunchIn") PunchIn: String,
        @Query("locationAddress") locationAddress: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("MovementStarted") MovementStarted: String,
        @Query("MovementStartLocationAddress") MovementStartLocationAddress: String,
        @Query("MovementStartLongitude") MovementStartLongitude: String,
        @Query("MovementStartLatitude") MovementStartLatitude: String,
        @Query("CreatedOn") CreatedOn: String,
        @Query("PunchoutDate") PunchoutDate: String,
        @Query("logoutlocation") logoutlocation: String,
        @Query("AuthHeader") header: String?


    ): Response<List<AttendanceResponse>>

    //@GET("PunchInAttendance")
    @GET("PunchInAttendanceLocationWise")
    suspend fun punchInAttendance(
        @Query("userId") userId: String?,
        @Query("locationAddress") locationAddress: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        @Query("attendanceTypeId") attendanceTypeId: String?,
        @Query("purpose") purpose: String?,
        @Query("clientname") clientname: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<AttendanceResponse>>

    @GET("GetMyMovementList")
    suspend fun getMyMovementData(
        @Query("userId") userId: String?,
        @Query("date") date: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MovementListResponse>>

    @GET("CompleteTour")
    suspend fun completeTour(
        @Query("userId") userId: String?, @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MovementListResponse>>

    @GET("LoadTask")
    suspend fun getTask(
        @Query("userid") userid: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TaskResponse>>

    @GET("GetLastLocationNew")
    suspend fun getLastLocation(
        @Query("userId") userId: String?, @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TaskResponse>>

    @GET("LoadMovementSpinner")
    suspend fun getPendingMovement(
        @Query("userId") userId: String?, @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MovementResponse>>

    @GET("IntitialMovementStart")
    suspend fun startMovement(
        @Query("userId") userId: String?,
        @Query("locationAddress") locationAddress: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        @Query("type") type: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MovementListResponse>>


    @GET("MovementCheckIn")
    suspend fun checkInMovement(
        @Query("userId") userId: String?,
        @Query("fromLocation") fromLocation: String?,
        @Query("toLocation") toLocation: String?,
        @Query("taskId") taskId: String?,
        @Query("reason") reason: String?,
        @Query("locationAddress") locationAddress: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        @Query("tourid") tourid: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TaskResponse>>

    @GET("MovementCheckInNew")
    suspend fun MovementCheckInNew(
        @Query("userId") userId: String?,
        @Query("fromLocation") fromLocation: String?,
        @Query("toLocation") toLocation: String?,
        @Query("taskId") taskId: String?,
        @Query("reason") reason: String?,
        @Query("locationAddress") locationAddress: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        @Query("tourid") tourid: String?,
        @Query("complaintno") complaintno: String?,
        @Query("machineno") machineno: String?,
        @Query("clientname") clientname: String?,
        @Query("clientplace") clientplace: String?,
        @Query("complainttype") complainttype: String?,
    ): Response<List<TaskResponse>>

    @GET("MovementCheckOut")
    suspend fun movementCheckout(
        @Query("userId") userId: String?,
        @Query("movementCode") movementCode: String?,
        @Query("locationAddress") locationAddress: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TaskResponse>>

    @GET("GetMyConveyanceList")
    suspend fun getMyConveyanceData(
        @Query("userId") userId: String?,
        @Query("date") date: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MyConveyanceResponse>>

    @GET("GetMyVoucherList")
    suspend fun getVoucher(
        @Query("userId") userId: String?,
        @Query("date") date: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MyConveyanceResponse>>


    @GET("LoadTransportMode")
    suspend fun getTransportMode(
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TransportModeResponse>>

    @GET("GetTourConveyance")
    suspend fun getMyTourConveyance(
        @Query("userid") userId: String?,
        @Query("date") date: String?,
        @Query("tourid") tourid: String?,
        @Query("AuthHeader") AuthHeader: String?

    ): Response<List<GetTourResponse>>

    @GET("GetTourConveyance")
    suspend fun getMyTourLog(
        @Query("userid") userId: String?,
        @Query("date") date: String?,
        @Query("tourid") tourid: String?,
        @Query("AuthHeader") AuthHeader: String?

    ): Response<List<MyConveyanceResponse>>

    @GET("LoadTourMovementSpinner")
    suspend fun getTourId(
        @Query("userid") userid: String,
        @Query("date") date: String,
        @Query("tourid") tourId: String,
        @Query("type") type: String,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TourIdResponse>>


    @GET("LoadTourMovementSpinner")
    suspend fun getTourDate(
        @Query("userid") userid: String,
        @Query("date") date: String,
        @Query("tourid") tourId: String,
        @Query("type") type: String,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<TourDateResponse>>

    @GET("LoadTourMovementSpinner")
    suspend fun getTourMovement(
        @Query("userid") userid: String,
        @Query("date") date: String,
        @Query("tourid") tourId: String,
        @Query("type") type: String,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MovementResponse>>

    @GET("GetMasterDetailNew")
    suspend fun getBankName(
        @Query("type") type: String?,
        @Query("id") id: String?,
        @Query("userId") userId: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<BankResponse>>

    @GET("GetMasterDetailNew")
    suspend fun getCompanyName(
        @Query("type") type: String?,
        @Query("id") id: String?,
        @Query("userId") userId: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<CompanyResponse>>

    @GET("GetMasterDetailNew")
    suspend fun getProjectName(
        @Query("type") type: String?,
        @Query("id") id: String?,
        @Query("userId") userId: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<ProjectResponse>>

    @GET("getFinalHodConveyanceFacilityDetail")
    suspend fun getHodFacilityWiseConveyance(
        @Query("userId") userId: String?,
        @Query("fromDate") from: String,
        @Query("toDate") to: String,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<HodConveyanceResponse>>

    @GET("getFinalHodConFacilityEmpDetail")
    suspend fun getHodFacilityEmployeeList(
        @Query("userId") userId: String?,
        @Query("facilityid") facilityId: String?,
        @Query("status") status: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") todate: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<HodConveyanceEmpResponse>>


    @GET("getFinalHodEmpConveyanceDetail")
    suspend fun getfinalHodEmpConveyanceDetail(
        @Query("userId") userId: String?,
        @Query("status") status: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") todate: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<HodConvEmpDetailResponse>>

    @GET("updatefinalHodEmpConveyance")
    suspend fun updatefinalHodEmpConveyance(
        @Query("con_id") conId: String?,
        @Query("status") status: String?,
        @Query("approvedamnt") amt: String?,
        @Query("remark") remarks: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<UpdateConveyanceEmpResponse>>

    @GET("getHodConveyanceFacilityDetail")
    suspend fun getHeadFacilityWiseConveyance(
        @Query("userId") userId: String?,
        @Query("fromDate") from: String,
        @Query("toDate") to: String,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<HodConveyanceResponse>>

    @GET("getHodConFacilityEmpDetail")
    suspend fun getHeadFacilityEmployeeList(
        @Query("userId") userId: String?,
        @Query("facilityid") facilityId: String?,
        @Query("status") status: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") todate: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<HodConveyanceEmpResponse>>


    @GET("getFinalHodEmpConveyanceDetail")
    suspend fun getfinalHeadEmpConveyanceDetail(
        @Query("userId") userId: String?,
        @Query("status") status: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") todate: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<HodConvEmpDetailResponse>>

    @GET("updateHodEmpConveyance")
    suspend fun updatefinalHeadEmpConveyance(
        @Query("con_id") conId: String?,
        @Query("status") status: String?,
        @Query("approvedamnt") amt: String?,
        @Query("remark") remarks: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<UpdateConveyanceEmpResponse>>


    @GET("GetLeavePlanList")
    suspend fun getMyLeavePlan(
        @Query("userId") userId: String?,
        @Query("type") typeId: Int,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<MyLeaveResponse>>

    @GET("LeavePlanSaveType")
    suspend fun saveLeavePlan(
        @Query("userId") userId: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") toDate: String?,
        @Query("reason") Reason: String?,
        @Query("leavetype") leavetype: String?,
        @Query("AuthHeader") AuthHeader: String?
    ): Response<List<SaveResponse>>


    //ComplaintModule

    @GET("getUserType")
    suspend fun getComplaintDashboardData(
        @Query("code") code: String?, @Query("type") type: String?
    ): Response<List<DashboardResponse>>

    @GET("getMachineDetail")
    suspend fun getClientDetails(
        @Query("userid") userid: String,
        @Query("types") types: String,
        @Query("machineNo") machineNo: String,
        @Query("clientId") clientId: String
    ): Response<List<AssignClientResponse>>

    @GET("getStatusDetails")
    suspend fun getComplaintStatus(@Query("types") types: String?): Response<List<ComplaintStatusResponse>>

    @GET("getComplaintLog")
    suspend fun getComplaintLog(@Query("complaintId") complaintid: String?): Response<List<ComplaintLogResponse>>

    @GET("getStatusDetails")
    suspend fun getPendingOwner(@Query("types") types: String?): Response<List<PendingOwnerResponse>>

    @GET("getStatusDetails")
    suspend fun getPendingReason(@Query("types") types: String?): Response<List<PendingReasonResponse>>

    @GET("assignComplaint")
    suspend fun assignResolveComplaint(
        @Query("complainID") complainID: String,
        @Query("complainstatusType") complainType: String,
        @Query("assignTo") assignTo: String,
        @Query("assignDate") assignDate: String,
        @Query("ComplaintTypeId") ComplaintTypeId: String,
        @Query("ItemId") ItemId: String,
        @Query("ComplaintDetails") ComplaintDetails: String,
        @Query("ComplaintChangeStatus") ComplaintChangeStatus: String,
        @Query("cby") cby: String,
        @Query("check") check: String
    ): Response<List<ProjectResponse>>

    @GET("getDetail")
    suspend fun getCustomerName(
        @Query("clientId") clientId: String, @Query("cityId") cityId: String
    ): Response<List<ComplainClientResponse>>

    @GET("getDetail")
    suspend fun getBranchDetails(
        @Query("clientId") clientId: String, @Query("cityId") cityId: String
    ): Response<List<BranchResponse>>

    @GET("getStateCity")
    suspend fun getStateDetails(
        @Query("types") types: String, @Query("stateId") stateId: String
    ): Response<List<StateResponse>>

    @GET("getStateCity")
    suspend fun getCityDetails(
        @Query("types") types: String, @Query("stateId") stateId: String
    ): Response<List<CityResponse>>

    @GET("getMachineItemDetail")
    suspend fun getMachineItem(
        @Query("machineNo") machine_no: String
    ): Response<List<HardwareResponse>>

    @GET("getUserType")
    suspend fun getEngineer(
        @Query("code") code: String, @Query("type") woid: String
    ): Response<List<SE_UserResponse>>

    @GET("getRegisterComplainDetail")
    suspend fun getRegisterComplainDetail(
        @Query("complainid") machine_no: String
    ): Response<List<GetComplainResponse>>


    @GET("getMachineDetail")
    suspend fun getMachineDetails(
        @Query("userid") userid: String,
        @Query("types") types: String,
        @Query("machineNo") machineNo: String,
        @Query("clientId") clientId: String
    ): Response<List<InitialComplainResponse>>

    @GET("getPendingInstallation")
    suspend fun getPendingInstallation(
        @Query("thid") thid: String,
        @Query("seid") seid: String,
        @Query("clientId") clientId: String,
        @Query("parentid") parentId: String
    ): Response<List<PendingInstallResponse>>


    @GET("getMachineDetail")
    suspend fun getComplainDetails(
        @Query("userid") userid: String,
        @Query("types") types: String,
        @Query("machineNo") machineNo: String,
        @Query("clientId") clientId: String
    ): Response<List<GetComplainResponse>>

    @GET("getComplaintType")
    suspend fun getComplainType(@Query("type") type: String): Response<List<ComplaintResponse>>

    @GET("saveComplaint")
    suspend fun addComplain(
        @Query("poid") poId: String?,
        @Query("ctid") complainTypeId: String?,
        @Query("ctdetails") detail: String,
        @Query("code") cby: String,
        @Query("advAmt") advance: String,
        @Query("itemid") itemId: String?,
        @Query("assingto") assingto: String?,
        @Query("assigndate") assigndate: String?
    ): Response<List<ComplaintResponse>>

}