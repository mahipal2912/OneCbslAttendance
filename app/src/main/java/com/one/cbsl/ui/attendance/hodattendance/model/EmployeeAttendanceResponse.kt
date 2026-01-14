package com.one.cbsl.ui.attendance.hodattendance.model


import com.google.gson.annotations.SerializedName

data class EmployeeAttendanceResponse(
    @SerializedName("FacilityId")
    var facilityId: String? = null,

    @SerializedName("Total")
    val total: String,

    @SerializedName("Present")
    val present: String,

    @SerializedName("Absent")
    val absent: String,

    @SerializedName("Leave")
    val leave: String,

    @SerializedName("UserId")
    val userId: String,

    @SerializedName("EmployeeName")
    val employeeName: String,

    @SerializedName("EmployeeCode")
    val employeeCode: String,

    @SerializedName("MobileNo")
    val mobileNo: String,

    @SerializedName("Facility")
    val facility: String,
    @SerializedName("CityName")
    val CityName: String,
    @SerializedName("faceData")
    val faceData: String,

    @SerializedName("Reporting")
    val reporting: String,

    @SerializedName("StatusByHod")
    val statusByHod: String,

    @SerializedName("Status")
    var status: String? = null,

    @SerializedName("status")
    val response: String? = null
)

