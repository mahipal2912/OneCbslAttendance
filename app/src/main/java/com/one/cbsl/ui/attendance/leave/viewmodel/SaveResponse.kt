import com.google.gson.annotations.SerializedName

data class SaveResponse (

	@SerializedName("status") val status : String,
	@SerializedName("SaveId") val SaveId : String
)