package com.one.cbsl.ui.attendance.punchattendance.model

import com.google.gson.annotations.SerializedName

data class AttendanceTypeResponse(
    @SerializedName("AttendanceTypeId") val AttendanceTypeId: String,
    @SerializedName("AttendanceType") val AttendanceType: String,
    @SerializedName("IsActive") val IsActive: String

) {
    override fun toString(): String {
        return AttendanceType
    }
}
