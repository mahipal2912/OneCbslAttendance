package com.one.cbsl.ui.attendance.conveyance

import MovementResponse
import SaveResponse
import android.R
import android.annotation.SuppressLint
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
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.one.cbsl.CbslMain
import com.one.cbsl.adapter.SelectImageAdapter
import com.one.cbsl.databinding.FragmentLocalClaimBinding
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LocalClaimFragment : Fragment(), OnItemSelectedListener, TextWatcher {

    private var _binding: FragmentLocalClaimBinding? = null
    private lateinit var conveyanceViewModel: ConveyanceViewModel
    private var photoUri: Uri? = null

    var tourid: String = ""
    private var bankid: String = "0"
    private var projectId: String = "0"
    private var transportId: String = "0"
    private var movementId: String = "0"
    var date: String = "0"
    private var headConveyance: String? = null
    private val compositeDisposable = CompositeDisposable()
    private val binding get() = _binding!!
    var imagesPathList: ArrayList<Uri> = arrayListOf()
    private var pdfName = ""
    private var imagebase = ""

  /*  private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { galleryUri ->
            try {
                imagesPathList.clear()
                pdfName = System.currentTimeMillis().toString() + ".pdf"
                galleryUri?.let {
                    for (uri in galleryUri) {
                        val imageUri: Uri = uri
                        imagesPathList.add(imageUri)
                    }
                    binding.rvImage.adapter = SelectImageAdapter(imagesPathList)
                    imagebase = Base64.encodeToString(
                        Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                        Base64.DEFAULT
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
*/
  // Google Play–compliant photo picker
  private val galleryLauncher =
      registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->

          try {
              imagesPathList.clear()
              pdfName = "${System.currentTimeMillis()}.pdf"

              uris?.let {
                  imagesPathList.addAll(it)
                  binding.rvImage.adapter = SelectImageAdapter(imagesPathList)

                  imagebase = Base64.encodeToString(
                      Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                      Base64.DEFAULT
                  )
              }

          } catch (e: Exception) {
              e.printStackTrace()
          }
      }
    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                photoUri?.let { uri ->
                    imagesPathList.add(uri)
                    binding.rvImage.adapter = SelectImageAdapter(imagesPathList)

                    imagebase = Base64.encodeToString(
                        Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                        Base64.DEFAULT
                    )
                }
            }
        }


    //TODO capture the image using camera and display it
  /*  private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {

                if (it.resultCode === RESULT_OK) {

                    imagesPathList.add(photoUri!!)
                    binding.rvImage.adapter = SelectImageAdapter(imagesPathList)
                    imagebase = Base64.encodeToString(
                        Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                        Base64.DEFAULT
                    )
                }
            }
        )
*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        conveyanceViewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[ConveyanceViewModel::class.java]

        _binding = FragmentLocalClaimBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMovement()
        getTransportModel()
        getProjectDetail("ALL")
        getCompanyDetail("1")
        binding.spinMovement.onItemSelectedListener = this
        binding.spinProject.onItemSelectedListener = this
        binding.spinTransport.onItemSelectedListener = this
        binding.spinBank.onItemSelectedListener = this
        binding.spinHead.onItemSelectedListener = this
        binding.etFare.addTextChangedListener(this)

        binding.etFoodCharge.addTextChangedListener(this)
        binding.tvSave.setOnClickListener {

            if (binding.etClientPlace.text.toString().trim()
                    .isEmpty()
            ) {
                DialogUtils.showFailedDialog(
                    requireActivity(),
                    "Enter Client Place Name "
                )

            } else if (projectId == "0" && bankid == "0") {
                DialogUtils.showFailedDialog(requireActivity(), "Select Project or Client")

            } else {
                if (imagesPathList.size > 0) {
                    saveLocalConveyance(
                      imagebase
                    )
                } else {
                    saveLocalConveyance("")
                }
            }
        }

        binding.ivImage.setOnClickListener {

            val dialog = Dialog(requireContext())
            val customDialogLayoutBinding: RawImageSelectionDialogBinding =
                RawImageSelectionDialogBinding.inflate(layoutInflater)
            dialog.setContentView(customDialogLayoutBinding.root)
            dialog.show()
            customDialogLayoutBinding.tvCamera.visibility=View.GONE
            customDialogLayoutBinding.tvCamera.setOnClickListener {
                openCamera()
                dialog.dismiss()
            }
            customDialogLayoutBinding.tvGallery.setOnClickListener {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )

                dialog.dismiss()
            }
            customDialogLayoutBinding.tvCancel.setOnClickListener {
                dialog.dismiss()
            }
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
    private fun saveLocalConveyance(pdfBase: String) {
        DialogUtils.showProgressDialog(requireActivity(), "Processing..")
        AndroidNetworking.initialize(requireContext())

        AndroidNetworking.post("https://hrisapi.cbslgroup.in/webmethods/apiwebservice.asmx/ConveyanceSaveNew")
            .addBodyParameter(
                "userId",
                SessionManager.getInstance().getString(Constants.UserId)
            )
            .addBodyParameter("movementId", movementId)
            .addBodyParameter("conveyanceDate", binding.etDate.text.toString())
            .addBodyParameter("fromLocation", binding.etFromLocation.text.toString())
            .addBodyParameter("toLocation", binding.etToLocation.text.toString())
            .addBodyParameter("transportModeId", transportId)
            .addBodyParameter("fare", binding.etFare.text.toString())
            .addBodyParameter("remarks", binding.etRemark.text.toString())
            .addBodyParameter("imageName", pdfName)
            .addBodyParameter("imageLocation", pdfBase)
            .addBodyParameter("conveyncehead", headConveyance)
            .addBodyParameter("bankid", bankid)
            .addBodyParameter("projectid", projectId)
            .addBodyParameter("foodExpense", binding.etFoodCharge.text.toString())
            .addBodyParameter("clientPlace", binding.etClientPlace.text.toString())
            .addBodyParameter("complaintno", binding.etComplaintNo.text.toString())
            .addBodyParameter("machineno", binding.etMachineNo.text.toString())
            .addBodyParameter("AuthHeader", Constants.AUTH_HEADER)
            .build()
            .getAsObjectList(SaveResponse::class.java, object :
                ParsedRequestListener<List<SaveResponse>> {
                override fun onResponse(response: List<SaveResponse>?) {

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

    private fun getTransportModel() {
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

    }

    private fun loadMovement() {
        conveyanceViewModel.getPendingMovement().observe(viewLifecycleOwner, Observer { resources ->
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
                        requireActivity(), resources.message.toString()
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
                            requireActivity(), resources.message.toString()
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
                            requireActivity(), resources.message.toString()
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
                        requireActivity(), resources.message.toString()
                    )
                }
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        when (view?.parent) {
            binding.spinBank -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as BankResponse
                    bankid = selectedItem.bankid!!
                    if ( SessionManager.getInstance().getString(Constants.COMPANY) == "CBM"
                        || SessionManager.getInstance().getString(Constants.COMPANY) == "CBMPL"
                        || SessionManager.getInstance().getString(Constants.COMPANY) == "BMD"
                    ) {
                             getCompanyDetail(bankid)
                      }
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
                    movementId = movementResponse.movementId
                    binding.etFoodCharge.setText("0")
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


                    //setVisibility(movementResponse.taskname)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d("", "Nothing Selected")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("", "Nothing Changed")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, position: Int, p3: Int) {
        if (!TextUtils.isEmpty(binding.etFare.text.toString())
            || !TextUtils.isEmpty(binding.etFoodCharge.text.toString())
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
            val total: Int = firstValue + secondValue
            binding.etTotal.text = Editable.Factory.getInstance().newEditable(total.toString())
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        Log.d("", "text changed")
    }


}