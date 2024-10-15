package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class ProjectResponse(
    @SerializedName("ProjectId") val ProjectId: String? = null,
    @SerializedName("ProjectName") val ProjectName: String? = null,
    @SerializedName("status") val Status: String? = null

) {
    override fun toString(): String {
        return ProjectName.toString()
    }
}