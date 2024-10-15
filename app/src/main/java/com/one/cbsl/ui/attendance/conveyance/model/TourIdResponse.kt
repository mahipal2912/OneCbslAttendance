import com.google.gson.annotations.SerializedName

data class TourIdResponse(

    @SerializedName("TourId") val TourId: String,
    @SerializedName("TransportCharge") val TransportCharge: String,
    @SerializedName("LoadingCharge") val LoadingCharge: String="",
    @SerializedName("BoardingCharge") val BoardingCharge: String="",
    @SerializedName("OtherCharge") val OtherCharge: String="",
    @SerializedName("Pending") val Pending: String

) {
    override fun toString(): String {
        return TourId
    }
}