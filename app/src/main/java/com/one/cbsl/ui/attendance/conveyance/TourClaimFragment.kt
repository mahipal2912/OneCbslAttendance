package com.one.cbsl.ui.attendance.conveyance

import MovementResponse
import SaveResponse
import TourDateResponse
import TourIdResponse
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.one.cbsl.CbslMain
import com.one.cbsl.adapter.SelectImageAdapter
import com.one.cbsl.databinding.FragmentTourConveyanceBinding
import com.one.cbsl.databinding.RawImageSelectionDialogBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.model.BankResponse
import com.one.cbsl.ui.attendance.conveyance.model.CompanyResponse
import com.one.cbsl.ui.attendance.conveyance.model.ProjectResponse
import com.one.cbsl.ui.attendance.conveyance.model.TransportModeResponse
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.utils.*
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class TourClaimFragment : Fragment(), TextWatcher, OnItemSelectedListener {

    private var _binding: FragmentTourConveyanceBinding? = null
    private lateinit var conveyanceViewModel: ConveyanceViewModel
    var tourid: String = ""
    var bankid: String = "0"
    var projectId: String = "0"
    var movementId: String = "0"
    var movementCode: String = "0"
    private var transportId: String = "0"

    var date: String = "0"
    var headConveyance: String? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var imagesPathList: ArrayList<Uri> = arrayListOf()
    var lodgingImageList: ArrayList<Uri> = arrayListOf()
    var boardingImageList: ArrayList<Uri> = arrayListOf()
    var otherImageList: ArrayList<Uri> = arrayListOf()

    var transportPdf: String = ""
    var lodgingPdf: String = ""
    var boardingPdf: String = ""
    var otherPdf: String = ""

    private var pdfName = ""
    private var imageType = "0"
    private var photoUri: Uri? = null

    /*private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { galleryUri ->
            try {
                galleryUri?.let {
                    when (imageType) {
                        "0" -> {
                            setImage(galleryUri, imagesPathList, binding.rvImage)
                            transportPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                                Base64.DEFAULT
                            )
                        }
                        "1" -> {
                            setImage(galleryUri, lodgingImageList, binding.rvLodingImage)
                            lodgingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(lodgingImageList)),
                                Base64.DEFAULT
                            )
                        }
                        "2" -> {
                            setImage(galleryUri, boardingImageList, binding.rvBoardingImage)
                            boardingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(boardingImageList)),
                                Base64.DEFAULT
                            )
                        }
                        "3" -> {
                            setImage(galleryUri, otherImageList, binding.rvOtherImage)
                            otherPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(otherImageList)),
                                Base64.DEFAULT
                            )
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
*/
    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->

            try {
                uris?.let {

                    when (imageType) {

                        "0" -> {
                            setImage(it, imagesPathList, binding.rvImage)
                            transportPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(imagesPathList)
                                ),
                                Base64.DEFAULT
                            )
                        }

                        "1" -> {
                            setImage(it, lodgingImageList, binding.rvLodingImage)
                            lodgingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(lodgingImageList)
                                ),
                                Base64.DEFAULT
                            )
                        }

                        "2" -> {
                            setImage(it, boardingImageList, binding.rvBoardingImage)
                            boardingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(boardingImageList)
                                ),
                                Base64.DEFAULT
                            )
                        }

                        "3" -> {
                            setImage(it, otherImageList, binding.rvOtherImage)
                            otherPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(otherImageList)
                                ),
                                Base64.DEFAULT
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    //TODO capture the image using camera and display it
   /* private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
                if (it.resultCode === RESULT_OK) {
                    when (imageType) {
                        "0" -> {
                            setSingleImage(photoUri!!, imagesPathList, binding.rvImage)
                            transportPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                                Base64.DEFAULT
                            )
                        }
                        "1" -> {
                            setSingleImage(photoUri!!, lodgingImageList, binding.rvLodingImage)
                            lodgingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(lodgingImageList)),
                                Base64.DEFAULT
                            )
                        }
                        "2" -> {
                            setSingleImage(photoUri!!, boardingImageList, binding.rvBoardingImage)
                            boardingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(boardingImageList)),
                                Base64.DEFAULT
                            )
                        }
                        "3" -> {
                            setSingleImage(photoUri!!, otherImageList, binding.rvOtherImage)
                            otherPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(otherImageList)),
                                Base64.DEFAULT
                            )
                        }
                    }


                }
            })
*/
    private val cameraActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                photoUri?.let { uri ->

                    when (imageType) {

                        "0" -> {
                            setSingleImage(uri, imagesPathList, binding.rvImage)
                            transportPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                                Base64.DEFAULT
                            )
                        }

                        "1" -> {
                            setSingleImage(uri, lodgingImageList, binding.rvLodingImage)
                            lodgingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(lodgingImageList)),
                                Base64.DEFAULT
                            )
                        }

                        "2" -> {
                            setSingleImage(uri, boardingImageList, binding.rvBoardingImage)
                            boardingPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(boardingImageList)),
                                Base64.DEFAULT
                            )
                        }

                        "3" -> {
                            setSingleImage(uri, otherImageList, binding.rvOtherImage)
                            otherPdf = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(otherImageList)),
                                Base64.DEFAULT
                            )
                        }
                    }
                }
            }
        }

    private fun setImage(
        galleryUri: List<Uri>,
        imageList: ArrayList<Uri>,
        recyclerView: RecyclerView
    ) {
        for (uri in galleryUri) {
            val imageUri: Uri = uri
            imageList.add(imageUri)
        }
        recyclerView.adapter = SelectImageAdapter(imageList)
    }

    private fun setSingleImage(
        imageUri: Uri,
        imageList: ArrayList<Uri>,
        recyclerView: RecyclerView
    ) {
        imageList.add(imageUri)
        recyclerView.adapter = SelectImageAdapter(imageList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        conveyanceViewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[ConveyanceViewModel::class.java]

        _binding = FragmentTourConveyanceBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTourTask()
        getProjectDetail("ALL")

        binding.etFare.addTextChangedListener(this)
        binding.etLodgingCharge.addTextChangedListener(this)
        binding.etOther.addTextChangedListener(this)
        binding.etFoodCharge.addTextChangedListener(this)

        binding.spinMovement.onItemSelectedListener = this
        binding.spinProject.onItemSelectedListener = this
        binding.spinMovementDate.onItemSelectedListener = this
        binding.spinTour.onItemSelectedListener = this
        binding.spinTransport.onItemSelectedListener = this
        binding.spinBank.onItemSelectedListener = this
        binding.spinHead.onItemSelectedListener = this
        binding.ivImage.setOnClickListener {
            imageType = "0"
            openDialogBox()
        }
        binding.ivLodging.setOnClickListener {
            imageType = "1"
            openDialogBox()
        }
        binding.ivBoarding.setOnClickListener {
            imageType = "2"
            openDialogBox()
        }

        binding.ivOther.setOnClickListener {
            imageType = "3"
            openDialogBox()
        }
        binding.tvSave.setOnClickListener {
            if (binding.etClientPlace.text.toString()
                    .isEmpty()
            ) {
                DialogUtils.showFailedDialog(
                    requireActivity(),
                    "Enter Client Place Name "
                )

            } else if (projectId == "0" && bankid == "0") {
                DialogUtils.showFailedDialog(requireActivity(), "Select Project or Client")

            } else {
                saveTourConveyance()
            }
        }
    }

    private fun openDialogBox() {
        val dialog = Dialog(requireContext())
        val customDialogLayoutBinding: RawImageSelectionDialogBinding =
            RawImageSelectionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(customDialogLayoutBinding.root)
        dialog.show()
        customDialogLayoutBinding.tvCamera.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }
        customDialogLayoutBinding.tvGallery.setOnClickListener {
            dialog.dismiss()
          //  galleryLauncher.launch("image/*")
            galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

        }
        customDialogLayoutBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun openCamera() {
        photoUri = null
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        }
        photoUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        cameraActivityResultLauncher.launch(cameraIntent)

    }

    @SuppressLint("CheckResult")
    private fun saveTourConveyance() {
        DialogUtils.showProgressDialog(requireActivity(), "Processing..")
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post("https://hrisapi.cbslgroup.in/webmethods/apiwebservice.asmx/TourConveyanceSave")
            .addBodyParameter("tourid", tourid)
            .addBodyParameter("movementId", movementId)
            .addBodyParameter("movementCode", movementCode)
            .addBodyParameter(
                "userId",
                SessionManager.getInstance().getString(Constants.UserId)
            )
            .addBodyParameter("conveyanceDate", binding.etDate.text.toString())
            .addBodyParameter("fromLocation", binding.etFromLocation.text.toString())
            .addBodyParameter("toLocation", binding.etToLocation.text.toString())
            .addBodyParameter("transportModeId", transportId)
            .addBodyParameter("fare", binding.etFare.text.toString())
            .addBodyParameter("remarks", binding.etRemark.text.toString())
            .addBodyParameter("imageName", System.currentTimeMillis().toString() + ".pdf")
            .addBodyParameter("imageLocation", transportPdf)
            .addBodyParameter("conveyncehead", headConveyance)
            .addBodyParameter("bankid", bankid)
            .addBodyParameter("projectid", projectId)

            .addBodyParameter("lodgingexpense", binding.etLodgingCharge.text.toString())
            .addBodyParameter("lodgingimage", lodgingPdf)
            .addBodyParameter("boardingCharge", binding.etFoodCharge.text.toString())
            .addBodyParameter("boardingimage", boardingPdf)
            .addBodyParameter("otherCharge", binding.etOther.text.toString())
            .addBodyParameter("otherimage", otherPdf)
            .addBodyParameter("clientPlace", binding.etClientPlace.text.toString())
            .addBodyParameter("complaintno", binding.etComplaintNo.text.toString())
            .addBodyParameter("machineno", binding.etMachineNo.text.toString())
            .addBodyParameter("AuthHeader", Constants.AUTH_HEADER)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObjectList(SaveResponse::class.java, object :
                ParsedRequestListener<List<SaveResponse>> {
                override fun onResponse(response: List<SaveResponse>?) {
                    DialogUtils.dismissDialog()
                    DialogUtils.showSuccessDialog(
                        requireActivity(),
                        response?.get(0)?.status.toString(),
                        CbslMain::class.java
                    )
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        requireActivity(), anError?.message, Toast.LENGTH_SHORT
                    ).show()
                }
            });

    }

    private fun disableEditText() {
        binding.etDate.isEnabled = false
        binding.etToLocation.isEnabled = false
        binding.etToLocation.isFocusable = false
        binding.etToLocation.isFocusableInTouchMode = false
        binding.etFromLocation.isEnabled = false
        binding.etFromLocation.isFocusable = false
        binding.etFromLocation.isFocusableInTouchMode = false
    }

    private fun getTourTask() {
        conveyanceViewModel.getTransportMode().observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.spinTransport.adapter = ArrayAdapter(
                                requireContext(), R.layout.simple_spinner_item, response
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                }
                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
        conveyanceViewModel.getTourId("", "", "TourId")
            .observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            try {
                                binding.spinTour.adapter = ArrayAdapter(
                                    requireContext(), R.layout.simple_spinner_item, response
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                    }
                    is Resource.Error -> {
                        //Handle Error
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                    }
                }
            })

    }

    private fun loadMovement(date: String) {
        conveyanceViewModel.getTourMovement(date, tourid, "")
            .observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            try {
                                binding.spinMovement.adapter = ArrayAdapter(
                                    requireContext(), R.layout.simple_spinner_item, response
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                    }
                    is Resource.Error -> {
                        //Handle Error
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                    }
                }
            })

    }

    private fun getTourDate(tourId: String) {
        conveyanceViewModel.getTourDate("", tourId, "date")
            .observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            try {
                                binding.spinMovementDate.adapter = ArrayAdapter(
                                    requireContext(), R.layout.simple_spinner_item, response
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                    }
                    is Resource.Error -> {
                        //Handle Error
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                    }
                }
            })
    }

    private fun getBankDetail(projectId: String) {
        conveyanceViewModel.getBankName(projectId)
            .observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            try {
                                binding.spinBank.adapter = ArrayAdapter(
                                    requireContext(), R.layout.simple_spinner_item, response
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                    }
                    is Resource.Error -> {
                        //Handle Error
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                    }
                }
            })

    }

    private fun getCompanyDetail(bankId: String) {
        conveyanceViewModel.getCompanyName(bankId)
            .observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            try {
                                binding.spinHead.adapter = ArrayAdapter(
                                    requireContext(), R.layout.simple_spinner_item, response
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                    }
                    is Resource.Error -> {
                        //Handle Error
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                    }
                }
            })

    }

    private fun getProjectDetail(type: String) {
        conveyanceViewModel.getProjectName(type).observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.spinProject.adapter = ArrayAdapter(
                                requireContext(), R.layout.simple_spinner_item, response
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Wait.. ")
                }
                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(
                        requireActivity(),
                        resources.message.toString()
                    )
                }
            }
        })

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("", "Process")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (!TextUtils.isEmpty(binding.etFare.text.toString())
            || !TextUtils.isEmpty(binding.etFoodCharge.text.toString())
            || !TextUtils.isEmpty(binding.etLodgingCharge.text.toString())
            || !TextUtils.isEmpty(binding.etOther.text.toString())
        ) {

            val firstValue =
                if (TextUtils.isEmpty(
                        binding.etFare.text.toString().trim()
                    )
                ) 0 else binding.etFare.text
                    .toString().trim().toInt()
            val secondValue =
                if (TextUtils.isEmpty(binding.etFoodCharge.text.toString().trim()))
                    0
                else binding.etFoodCharge.text
                    .toString().trim().toInt()


            val thirdValue =
                if (TextUtils.isEmpty(
                        binding.etLodgingCharge.text.toString().trim()
                    )
                ) 0 else binding.etLodgingCharge.text
                    .toString().trim().toInt()
            val fifthValue =
                if (TextUtils.isEmpty(
                        binding.etOther.text.toString().trim()
                    )
                ) 0 else binding.etOther.text
                    .toString().trim().toInt()


            val total: Int = firstValue + secondValue + thirdValue + fifthValue
            binding.etTotal.text = Editable.Factory.getInstance().newEditable(total.toString())
        }


    }

    override fun afterTextChanged(p0: Editable?) {
        Log.d("", "Done")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        when (view?.parent) {
            binding.spinBank -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as BankResponse
                    bankid = selectedItem.bankid!!
                    getCompanyDetail(bankid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.spinHead -> {
                try {
                    //headConveyance = parent.getItemAtPosition(position).toString()
                    val selectedItem = parent?.getItemAtPosition(position) as CompanyResponse
                    headConveyance = selectedItem.Headname.toString()
                    // getProjectDetail(headConveyance!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.spinProject -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as ProjectResponse
                    projectId = selectedItem.ProjectId!!
                    getBankDetail(projectId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.spinMovement -> {
                try {
                    val movementResponse = parent?.getItemAtPosition(position) as MovementResponse

                    movementId = movementResponse.movementId
                    movementCode = movementResponse.movementCode

                    if (movementResponse.movementCode.contains("Movement")) {
                        disableEditText()
                        //enableEditText()
                        binding.etFare.isEnabled = false
                        binding.etRemark.isEnabled = false
                        binding.tvSave.isEnabled = false
                    } else {
                        binding.tvSave.isEnabled = true
                        disableEditText()
                        binding.etFare.isEnabled = true
                        binding.etFare.setText("0")

                        binding.etRemark.isEnabled = true
                    }
                    binding.etFoodCharge.setText("0")
                    binding.etLodgingCharge.setText("0")
                    binding.etOther.setText("0")
                    binding.etDate.text = movementResponse.movementDate
                    binding.etRemark.setText(movementResponse.remark)
                    binding.etToLocation.setText(movementResponse.toLocation)
                    binding.etFromLocation.setText(movementResponse.fromLocation)
                    binding.etPurpose.text = movementResponse.taskname
                    binding.etMachineNo.text =
                        Editable.Factory.getInstance().newEditable(movementResponse.MachineNumber)
                    binding.etComplaintNo.text =
                        Editable.Factory.getInstance().newEditable(movementResponse.ComplaintNumber)
                    binding.etClientPlace.text =
                        Editable.Factory.getInstance().newEditable(movementResponse.ClientPlaceName)


                    if (movementResponse.LB_Clain == "0") {
                        binding.llTourMain.visibility = View.VISIBLE
                    } else {
                        binding.llTourMain.visibility = View.GONE
                    }
                    //setVisibility(movementResponse.taskname)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.spinTour -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as TourIdResponse
                    if (position > 0) {
                        tourid = selectedItem.TourId
                        getTourDate(selectedItem.TourId)

                        binding.etLodgingCharge.text =
                            Editable.Factory.getInstance().newEditable(selectedItem.LoadingCharge)
                        binding.etFoodCharge.text =
                            Editable.Factory.getInstance().newEditable(selectedItem.BoardingCharge)
                        binding.etOther.text =
                            Editable.Factory.getInstance().newEditable(selectedItem.OtherCharge)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
            binding.spinTransport -> {
                try {
                    //headConveyance = parent.getItemAtPosition(position).toString()
                    val selectedItem = parent?.getItemAtPosition(position) as TransportModeResponse
                    transportId = selectedItem.TransportModeId.toString()

                    // getProjectDetail(headConveyance!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.spinMovementDate -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as TourDateResponse

                    if (position > 0) {
                        date = selectedItem.date
                        loadMovement(selectedItem.date)
                    } else {
                        loadMovement("01/01/2001")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d("", "Nothing Select")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}