package com.one.cbsl.ui.complain.model
import com.google.gson.annotations.SerializedName

class PendingOwnerResponse(

    @SerializedName("PendingOwnerName") val PendingOwnerName: String? = null,
    @SerializedName("PendingOwnerId") val PendingOwnerId: Int? = 0,
    @SerializedName("status") val Status: String? = null

) {
    override fun toString(): String {
        return PendingOwnerName.toString()
    }
}
