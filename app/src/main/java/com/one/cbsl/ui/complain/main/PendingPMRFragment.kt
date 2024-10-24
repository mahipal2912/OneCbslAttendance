package com.one.cbsl.ui.complain.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
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
import com.one.cbsl.databinding.FragmentLeaveBinding
import com.one.cbsl.databinding.FragmentPendingComplaintBinding
import com.one.cbsl.databinding.FragmentPendingPmrBinding
import com.one.cbsl.databinding.RawImageSelectionDialogBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.complain.adapter.AssignComplainAdapter
import com.one.cbsl.ui.attendance.leave.adapter.MyLeaveAdapter
import com.one.cbsl.ui.attendance.leave.viewmodel.LeaveViewModel
import com.one.cbsl.ui.attendance.movement.viewmodel.MovementViewModel
import com.one.cbsl.ui.complain.adapter.ComplaintLogAdapter
import com.one.cbsl.ui.complain.model.*
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.*
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.text.SimpleDateFormat
import java.util.*

class PendingPMRFragment : Fragment(), AssignComplainAdapter.OptionListener,
    AdapterView.OnItemSelectedListener {

    private var _binding: FragmentPendingPmrBinding? = null
    private var viewModel: ComplaintViewModel? = null
    var complaint_Id: String? = null
    var statusId: Int? = 0
    var assignUserId: String? = null
    var check: String? = null


    var itemId: String = "0"
    var complainChangeStatus: String = "0"
    var emptyValue: String = ""
    var complainTypeId: String? = "0"
    private val binding get() = _binding!!
    private var clientId = "0"
    var imagesPathList: ArrayList<Uri> = arrayListOf()
    var imagesBeforePmr: ArrayList<Uri> = arrayListOf()
    var imagesAfterPmr: ArrayList<Uri> = arrayListOf()
    private var pdfName = ""
    private var certificatePath = ""
    private var beforePmrPath = ""
    private var afterPmrPath = ""
    private var imageType = "0"

    private var photoUri: Uri? = null
    val currentDateTime = Calendar.getInstance()

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { galleryUri ->
            try {
                galleryUri?.let {
                    when (imageType) {
                        "0" -> {
                            setImage(galleryUri, imagesPathList, binding.complainDetails.rvImage)
                            certificatePath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                                Base64.DEFAULT
                            )
                        }

                        "1" -> {
                            setImage(galleryUri, imagesAfterPmr, binding.complainDetails.rvImageNew)
                            afterPmrPath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesAfterPmr)),
                                Base64.DEFAULT
                            )
                        }

                        "2" -> {
                            setImage(
                                galleryUri,
                                imagesBeforePmr,
                                binding.complainDetails.rvImageOld
                            )
                            beforePmrPath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesBeforePmr)),
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
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
                if (it.resultCode === Activity.RESULT_OK) {
                    when (imageType) {
                        "0" -> {
                            setSingleImage(
                                photoUri!!,
                                imagesPathList,
                                binding.complainDetails.rvImage
                            )
                            certificatePath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesPathList)),
                                Base64.DEFAULT
                            )
                        }

                        "1" -> {
                            setSingleImage(
                                photoUri!!,
                                imagesAfterPmr,
                                binding.complainDetails.rvImageNew
                            )
                            afterPmrPath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesAfterPmr)),
                                Base64.DEFAULT
                            )
                        }

                        "2" -> {
                            setSingleImage(
                                photoUri!!,
                                imagesBeforePmr,
                                binding.complainDetails.rvImageOld
                            )
                            beforePmrPath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesBeforePmr)),
                                Base64.DEFAULT
                            )
                        }

                    }


                }
            })


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(Cbsl.getInstance());

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bindView()
        _binding = FragmentPendingPmrBinding.inflate(inflater, container, false)


        return binding.root
    }

    @SuppressLint("HardwareIds")
    private fun bindView() {
        viewModel = if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            ViewModelProvider(
                requireActivity(),
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.solarApiService))
            )[ComplaintViewModel::class.java]
        } else {

            ViewModelProvider(
                requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.bmdApiService))
            )[ComplaintViewModel::class.java]
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCustomerName()
        getPendingPMR()
        getComplaintStatus("PMR_STATUS")
        binding.searchOption.spinClientName.onItemSelectedListener = this
        binding.complainDetails.spinStatus.onItemSelectedListener = this
        binding.complainDetails.spinAssignto.onItemSelectedListener = this
        binding.complainDetails.spinComplainType.onItemSelectedListener = this
        binding.complainDetails.spinComplainItem.onItemSelectedListener = this
        binding.complainDetails.checkType.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.complainDetails.llComplainItemType.visibility = View.VISIBLE
                complainChangeStatus = "1"
            } else {
                complainChangeStatus = "0"
                itemId = "0"
                binding.complainDetails.llComplainItemType.visibility = View.GONE
            }
        }

        binding.complainDetails.tvAssingDate.text = Constants.getDateWithTime()
        binding.complainDetails.tvAssingDate.setOnClickListener {
            pickDateTime()
        }
        binding.btnSearch.setOnClickListener {
            getPendingPMR()
        }
        binding.complainDetails.btnDocumentUpload.setOnClickListener {
            imageType = "0"
            openDialogBox()
        }
        binding.complainDetails.btnDocumentUploadNew.setOnClickListener {
            imageType = "1"
            openDialogBox()
        }
        binding.complainDetails.btnDocumentUploadOld.setOnClickListener {
            imageType = "2"
            openDialogBox()
        }


        binding.logDialog.icCloseLogDialog.setOnClickListener {
            binding.logDialog.rlComplainLogDialog.visibility = View.GONE
            binding.llMainHeader.visibility = View.VISIBLE
        }
        binding.complainDetails.icCloseDialog.setOnClickListener {
            binding.complainDetails.rlComplainDialog.visibility = View.GONE
            binding.llMainHeader.visibility = View.VISIBLE
        }
        binding.complainDetails.btnAssign.setOnClickListener {
            if (check == "R") {
                if (imagesPathList.size > 0 && imagesBeforePmr.size > 0 && imagesAfterPmr.size > 0) {
                    closeComplaintNew()
                } else {
                    DialogUtils.showFailedDialog(requireActivity(), "Attach all document")

                }
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
            galleryLauncher.launch("image/*")
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
    private fun closeComplaintNew() {

        var url =
            "http://crmapi.cbslprojects.com/webmethods/apiwebservice.asmx/closeRejectComplaintNew"
        if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            url =
                "https://dms.crconline.in/solarappnew/webmethods/apiwebservice.asmx/closeRejectComplaintNew"
        }
        DialogUtils.showProgressDialog(requireActivity(), "Processing...")
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post(url)
            .addBodyParameter("complainID", complaint_Id)
            .addBodyParameter("ComplaintTypeId", "4")
            .addBodyParameter("remarks", binding.complainDetails.etComplainDetail.text.toString())
            .addBodyParameter("imageLocation", certificatePath)
            .addBodyParameter("ComplaintChangeStatus", complainChangeStatus)
            .addBodyParameter(
                "cby",
                SessionManager.getInstance().getString(Constants.COMPLAINT_USERID)
            )
            .addBodyParameter("check", "R")
            .addBodyParameter("resolveDate", binding.complainDetails.tvAssingDate.text.toString())
            .addBodyParameter("beforPmr", beforePmrPath)
            .addBodyParameter("afterPmr", afterPmrPath)
            .build()
            .getAsObjectList(ProjectResponse::class.java, object :
                ParsedRequestListener<List<ProjectResponse>> {
                override fun onResponse(response: List<ProjectResponse>?) {
                    DialogUtils.dismissDialog()
                    DialogUtils.showSuccessDialog(
                        requireActivity(),
                        response?.get(0)?.Status.toString(),
                        CbslMain::class.java
                    )
                }

                override fun onError(anError: ANError?) {
                    DialogUtils.showFailedDialog(requireActivity(), anError?.message.toString())
                }
            })
    }


    private fun loadCustomerName() {
        viewModel?.getCustomerName(
            "999", "0"
        )?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Leave")
                }

                is Resource.Success -> {
                    try {
                        binding.searchOption.spinClientName.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            resource.data!!
                        )
                        clientId = resource.data[0].ClientId.toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })

    }

    private fun getPendingPMR() {
        viewModel?.getComplainDetails(
            clientId,
            "PMR_GET",
            binding.searchOption.etBranch.text.toString()
        )
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                    }

                    is Resource.Success -> {
                        try {
                            if (resource.data?.get(0)?.Status == null) {
                                binding.llNoData.visibility = View.GONE
                                binding.rvMachineList.visibility = View.VISIBLE
                                binding.rvMachineList.adapter =
                                    AssignComplainAdapter(resource.data!!, this)
                            } else {
                                binding.rvMachineList.visibility = View.GONE
                                binding.llNoData.visibility = View.VISIBLE
                            }
                            DialogUtils.dismissDialog()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                    }
                }
            })
    }

    private fun getRegisterComplainDetail(complainId: String) {
        viewModel?.getRegisterComplainDetail(
            complainId
        )?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.complainDetails.rlComplainDialog.visibility = View.VISIBLE

                            binding.complainDetails.apply {
                                binding.complainDetails.tvComplainType.text =
                                    response[0].ComplaintType_Name
                                binding.complainDetails.tvComplainNo.text =
                                    response[0].Complain_tNumber
                                binding.complainDetails.tvMachineNo.text =
                                    response[0].Machine_Number
                                binding.complainDetails.tvItemName.text = response[0].ClientName

                            }
                            binding.llMainHeader.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }

                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
    }

    private fun loadHardwareItem(machine_no: String) {
        viewModel?.getMachineItem(
            machine_no
        )?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resource.data?.let { response ->
                        try {
                            if (response[0].ItemName != null) {
                                binding.btnSearch.visibility = View.VISIBLE
                                binding.complainDetails.spinComplainItem.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    response
                                )
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    private fun getComplainType() {
        viewModel?.getComplainType()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resource.data?.let { response ->
                        try {
                            if (response[0].ComplaintTypeName != null) {
                                binding.complainDetails.spinComplainType.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    response
                                )
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    private fun getEngineer(workOrderId: String) {
        viewModel?.getEngineer(
            workOrderId
        )?.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Engineer")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resource.data?.let { response ->
                        try {
                            binding.complainDetails.spinAssignto.adapter = ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_item,
                                response
                            )

                        } catch (e: Exception) {

                        }
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    private fun getComplaintStatus(type: String) {
        viewModel?.getComplaintStatus(type)?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Status")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.complainDetails.spinStatus.adapter = ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                response
                            )

                        } catch (e: Exception) {

                        }
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })

    }


    override fun onItemClick(ComplaintId: String, workorderId: String) {
        complaint_Id = ComplaintId
        getRegisterComplainDetail(ComplaintId)
        getEngineer(workorderId)
        getComplainType()
        imagesAfterPmr.clear()
        imagesPathList.clear()
        imagesBeforePmr.clear()
        binding.complainDetails.rvImage.adapter = SelectImageAdapter(imagesPathList)
        binding.complainDetails.rvImageNew.adapter = SelectImageAdapter(imagesAfterPmr)
        binding.complainDetails.rvImageOld.adapter = SelectImageAdapter(imagesBeforePmr)
    }


    override fun showComplaintLog(ComplaintId: String) {
        viewModel?.getComplaintLog(ComplaintId)?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    try {
                        resources.data?.let { response ->
                            binding.logDialog.rlComplainLogDialog.visibility = View.VISIBLE
                            binding.logDialog.rvComplaintLog.adapter = ComplaintLogAdapter(response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Getting Log")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })

    }


    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        p3: Long
    ) {

        when (view?.parent) {
            binding.searchOption.spinClientName -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as ComplainClientResponse
                    clientId = selectedItem.ClientId!!
                    //loadBranchName(cityId!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinStatus -> {
                try {
                    val selectedItem =
                        parent?.getItemAtPosition(position) as ComplaintStatusResponse
                    statusId = selectedItem.ItemId!!.toInt()
                    if (statusId == 0 && statusId == 2 && statusId == 3) {
                        check = "L"
                        binding.complainDetails.llDocumentLayout.visibility = View.GONE
                    } else {
                        check = "R"
                        binding.complainDetails.llDocumentLayout.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinAssignto -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as SE_UserResponse
                    assignUserId = selectedItem.CRMUsersId!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinComplainType -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as ComplaintResponse
                    complainTypeId = selectedItem.id
                    if (selectedItem.ComplaintTypeName == "hardware") {
                        loadHardwareItem(binding.complainDetails.tvMachineNo.text.toString())
                        binding.complainDetails.llComplainItemType.visibility = View.VISIBLE
                    } else {
                        binding.complainDetails.llComplainItemType.visibility = View.GONE
                        itemId = "0"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinComplainItem -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as HardwareResponse
                    itemId = selectedItem.ItemId!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun pickDateTime() {
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        // Format the pickedDateTime
                        val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(pickedDateTime.time)

                        binding.complainDetails.tvAssingDate.text = formattedDateTime
                        //  doSomethingWith(pickedDateTime)
                    },
                    startHour,
                    startMinute,
                    true
                ).show()
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}