package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

class CompanyResponse {
    @SerializedName("Headname")
    var Headname: String? = null
    override fun toString(): String {
        return Headname.toString()
    }
}