package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class CityResponse(

    @SerializedName("CityName") val CityName: String? = null,
    @SerializedName("CityId") val CityId: String? = null,
    @SerializedName("status") val Status: String? = null


) {
    override fun toString(): String {
        return CityName.toString()
    }
    
}