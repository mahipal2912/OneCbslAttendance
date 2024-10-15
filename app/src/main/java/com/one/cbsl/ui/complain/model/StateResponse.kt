package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class StateResponse(

    @SerializedName("StateName") val StateName: String? = null,
    @SerializedName("StateId") val StateProvinceId: String? = null


) {
    override fun toString(): String {
        return StateName!!
    }
    
}