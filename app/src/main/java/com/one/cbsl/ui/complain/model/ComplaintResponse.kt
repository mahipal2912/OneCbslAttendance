package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class
ComplaintResponse(

    @SerializedName("ComplaintTypeId") val id: String? = null,
    @SerializedName("ComplaintTypeName") val ComplaintTypeName: String? = null,
    @SerializedName("status") val Status: String? = null


) {
    override fun toString(): String {
        return ComplaintTypeName.toString()
    }
    
}