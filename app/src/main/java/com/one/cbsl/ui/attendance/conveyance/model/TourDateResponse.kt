import com.google.gson.annotations.SerializedName

data class TourDateResponse(


    @SerializedName("date") val date: String

) {
    override fun toString(): String {
        return date
    }
}