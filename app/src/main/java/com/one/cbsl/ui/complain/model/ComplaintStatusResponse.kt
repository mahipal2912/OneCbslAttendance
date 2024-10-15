package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class ComplaintStatusResponse(

    @SerializedName("ComplaintStatusName") val ItemName: String? = null,
    @SerializedName("ComplaintStatusId") val ItemId: String? = null,
    @SerializedName("status") val Status: String? = null

) {
    override fun toString(): String {
        return ItemName.toString()
    }
}
