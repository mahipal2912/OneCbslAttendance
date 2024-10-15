package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class HodAttendanceResponse(
        @SerializedName("FacilityId") val FacilityId: String?,
        @SerializedName("Total") val Total: String,
        @SerializedName("Present") val Present: String,
        @SerializedName("Absent") val Absent: String,
        @SerializedName("Leave") val Leave: String,
        @SerializedName("UserId") val UserId: String,
        @SerializedName("EmployeeName") val EmployeeName: String,
        @SerializedName("EmployeeCode") val EmployeeCode: String,
        @SerializedName("PunchIn") val PunchIn: String,
        @SerializedName("PunchOut") val PunchOut: String,
        @SerializedName("LocationAddress") val LocationAddress: String,
        @SerializedName("LogoutLocation") val LogoutLocation: String,
        @SerializedName("MobileNo") val MobileNo: String,
        @SerializedName("Facility") val Facility: String,
        @SerializedName("Reporting") val Reporting: String,
        @SerializedName("StatusByHod") val StatusByHod: String,
        @SerializedName("Status") var Status: String?=null,
        @SerializedName("status") val response: String?=null

)
