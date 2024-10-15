package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class ProjectResponse(

    @SerializedName("ProjectId") val ProjectId: String? = null,
    @SerializedName("ProjectName") val ProjectName: String? = null,
    @SerializedName("status") val Status: String? = null


) {
    override fun toString(): String {
        return ProjectName.toString()
    }
    
}