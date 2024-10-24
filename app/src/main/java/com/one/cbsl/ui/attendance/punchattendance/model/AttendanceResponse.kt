package com.one.cbsl.ui.attendance.punchattendance.model

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(
    @SerializedName("UserId") val UserId: String,
    @SerializedName("PunchDate") val PunchDate: String,
    @SerializedName("PunchIn") val PunchIn: String,
    @SerializedName("WorkingHours") val WorkingHours: String,
    @SerializedName("odpurpose") val odpurpose: String,
    @SerializedName("odclientname") val odclientname: String,
    @SerializedName("PunchOut") val PunchOut: String,
    @SerializedName("LocationAddress") val LocationAddress: String,
    @SerializedName("Latitude") val Latitude: String,
    @SerializedName("Longitude") val Longitude: String,
    @SerializedName("AttendanceMarkBy") val attendnceBy: String? = null,
    @SerializedName("Astatus") val Astatus: String? = null,
    @SerializedName("LogoutLocation") val LogoutLocation: String? = null,
    @SerializedName("logOutStatus") val logOutStatus: String? = null,
    @SerializedName("status") val MarkStatus: String? = null,
    @SerializedName("Approvedby") val approvedby: String? = "",
    @SerializedName("Attendancestatus") val Attendancestatus: String? = "",
    @SerializedName("MarksStatus") val MarksStatus: String? = "",
    @SerializedName("Approveddate") val approveddate: String? = "",
    @SerializedName("StatusByHod") val hodStatus: String? = "",
    @SerializedName("Approvedbyhead") val Approvedbyhead: String? = "",
    @SerializedName("headApproveddate") val headApproveddate: String? = "",
    @SerializedName("statusbyhead") val statusbyhead: String? = ""
)
