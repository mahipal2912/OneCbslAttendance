package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class HodAttendanceTypeResponse (
    @SerializedName("Type") val Type: String,
    @SerializedName("status") val markstatus: String

)
{
    override fun toString(): String {
        return Type
    }
}