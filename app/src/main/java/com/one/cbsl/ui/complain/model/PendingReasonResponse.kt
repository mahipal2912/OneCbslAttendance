package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class PendingReasonResponse(

    @SerializedName("ComplaintPendingReasonName") val name: String? = null,
    @SerializedName("ComplaintPendingReasonId") val reasonId: String? = null,
    @SerializedName("status") val Status: String? = null

) {
    override fun toString(): String {
        return name.toString()
    }
}
