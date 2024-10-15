package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class ComplainClientResponse(

        @SerializedName("ClientId") var ClientId: String? = null,
        @SerializedName("ClientName") var ClientName: String? = null,
        @SerializedName("ClientCode") val BranchCode: String? = null


) {
    override fun toString(): String {
        return ClientName.toString()
    }
    
}