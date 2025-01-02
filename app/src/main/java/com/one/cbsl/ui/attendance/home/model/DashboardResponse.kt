package com.one.cbsl.ui.attendance.home.model

data class DashboardResponse(
    var TotalAttendance: String? = null,
    var LatePunch: String? = null,
    var TotalMovement: String? = null,
    var TotalConveyance: String? = null,
    var LeavePlan: String? = null,
    var TotalVoucher: String? = null,
    var Schedule: String? = null,
    var Productiviy: String? = null,
    var AttendanceHod: Int? = null,
    var ConveyanceHead: Int? = null,
    var Pending: String? = null,
    var InstallPending: String? = null,
    var closeComplaint: String? = null,
    var pmr: String? = null,
    var status: String? = null,
    var CRMUsersId: String? = null,
    var GroupTypeId: String? = null,
    var NickName: String? = null,
    var onTour: String? = null,
    var isAccepted: Int? = 0,
    var IsPassUpdated: Int? = 0,
    var faceData: String? = "0",
    var IsProfileUpdated: String? = "0",
    var faceEnabled: String? = "0"
) {
    override fun toString(): String {
        return NickName.toString()
    }
}