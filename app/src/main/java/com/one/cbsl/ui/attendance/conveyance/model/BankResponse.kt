package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

class BankResponse {
    @SerializedName("bankid")
    var bankid: String? = null
    @SerializedName("bank name")
    var bankName: String? = null
    override fun toString(): String {
        return bankName.toString()
    }
}