package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class HardwareResponse(


    @SerializedName("ItemName") val ItemName: String? = null,
    @SerializedName("ItemId") val ItemId: String? = null,
    @SerializedName("status") val Status: String? = null

) {
    override fun toString(): String {
        return ItemName.toString()
    }
}
