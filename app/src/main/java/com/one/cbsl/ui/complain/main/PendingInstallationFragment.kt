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
import android.provider.MediaStore
import android.text.Editable
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.adapter.SelectImageAdapter
import com.one.cbsl.databinding.FragmentComplaintHomeBinding
import com.one.cbsl.databinding.FragmentPendingInstallationBinding
import com.one.cbsl.databinding.RawImageSelectionDialogBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.complain.adapter.AssignComplainAdapter
import com.one.cbsl.ui.complain.adapter.ComplaintMainAdapter
import com.one.cbsl.ui.complain.adapter.PendingInstallationAdapter
import com.one.cbsl.ui.complain.model.ComplainClientResponse
import com.one.cbsl.ui.complain.model.PendingInstallResponse
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.*
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class PendingInstallationFragment : Fragment(),  AdapterView.OnItemSelectedListener,PendingInstallationAdapter.OpitionListener {

    private var _binding: FragmentPendingInstallationBinding? = null
    private var viewModel: ComplaintViewModel? = null
    var clientId: String = "0"
    private val binding get() = _binding!!
    var machineId: String = "0"

    var imagesInstallList: ArrayList<Uri> = arrayListOf()
    var imagesCertificateList: ArrayList<Uri> = arrayListOf()
    var imagesTrainingList: ArrayList<Uri> = arrayListOf()
    private var pdfName = ""
    private var certificatePath = ""
    private var installPath = ""
    private var trainingPath = ""
    private var imageType = "0"

    private var photoUri: Uri? = null
    val currentDateTime = Calendar.getInstance()

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { galleryUri ->
            try {
                galleryUri?.let {
                    when (imageType) {
                        "0" -> {
                            setImage(
                                galleryUri,
                                imagesInstallList,
                                binding.installDetails.rvInsallationReport
                            )
                            installPath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesInstallList)),
                                Base64.DEFAULT
                            )
                        }

                        "1" -> {
                            setImage(
                                galleryUri,
                                imagesTrainingList,
                                binding.installDetails.rvTrainingReport
                            )
                            trainingPath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(Constants.generatePdf(imagesTrainingList)),
                                Base64.DEFAULT
                            )
                        }

                        "2" -> {
                            setImage(
                                galleryUri,
                                imagesCertificateList,
                                binding.installDetails.rvCustomerCertificate
                            )
                            certificatePath = Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(
                                        imagesCertificateList
                                    )
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
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode === Activity.RESULT_OK) {
                when (imageType) {
                    "0" -> {
                        setSingleImage(
                            photoUri!!,
                            imagesInstallList,
                            binding.installDetails.rvInsallationReport
                        )
                        installPath = Base64.encodeToString(
                            Utils.convertPDFToByteArray(Constants.generatePdf(imagesInstallList)),
                            Base64.DEFAULT
                        )
                    }

                    "1" -> {
                        setSingleImage(
                            photoUri!!,
                            imagesTrainingList,
                            binding.installDetails.rvTrainingReport
                        )
                        trainingPath = Base64.encodeToString(
                            Utils.convertPDFToByteArray(Constants.generatePdf(imagesTrainingList)),
                            Base64.DEFAULT
                        )
                    }

                    "2" -> {
                        setSingleImage(
                            photoUri!!,
                            imagesCertificateList,
                            binding.installDetails.rvCustomerCertificate
                        )
                        certificatePath = Base64.encodeToString(
                            Utils.convertPDFToByteArray(Constants.generatePdf(imagesCertificateList)),
                            Base64.DEFAULT
                        )
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(Cbsl.getInstance());

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPendingInstallationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindView()
        binding.searchOption.spinClientName.onItemSelectedListener = this
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as CbslMain?)!!.setDrawerLocked(false)
        (activity as CbslMain?)!!.updateValues()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCustomerName()
        getPendingInstallation("0")

        binding.btnSearch.setOnClickListener {
            getPendingInstallation(binding.searchOption.etBranch.text.toString())
        }

        binding.installDetails.apply {
            tvInstallDate.text =
                Editable.Factory.getInstance().newEditable(Constants.getDateWithTime())
            icCloseDialog.setOnClickListener {
                binding.installDetails.rlInstallationDialog.visibility = View.GONE
            }
            btnInstall.setOnClickListener {
                installedMachineProcess()
            }
            tvInstallDate.setOnClickListener {
                pickDateTime()
            }
            btnInstallationReport.setOnClickListener {
                //takePic()
                imageType = "0"
                openDialogBox()
            }
            btnTrainingReport.setOnClickListener {
                //  takePic()
                imageType = "1"
                openDialogBox()
            }
            btnCustomerCertificate.setOnClickListener {
                //takePic()
                imageType = "2"
                openDialogBox()
            }
        }
        binding.installDetails.icCloseDialog.setOnClickListener {
            binding.installDetails.rlInstallationDialog.visibility = View.GONE
            binding.llMainHeader.visibility = View.VISIBLE
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


    private fun loadCustomerName() {
        viewModel?.getCustomerName(
            "999", "0"
        )?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Customer")
                }

                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()


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

    private fun getPendingInstallation(branchId: String) {
        viewModel?.getPendingInstallation(
            SessionManager.getInstance().getString(Constants.COMPLAINT_USERID),
            "0",
            clientId,
            branchId
        )
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                    }

                    is Resource.Success -> {
                        try {
                            if (resource.data?.get(0)?.status == null) {
                                binding.llNoData.visibility = View.GONE
                                binding.rvMachineList.visibility = View.VISIBLE
                                binding.rvMachineList.adapter =
                                    PendingInstallationAdapter(resource.data!!, this)
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


    @SuppressLint("CheckResult")
    private fun installedMachineProcess() {
        var url =
            "http://crmapi.cbslprojects.com/webmethods/apiwebservice.asmx"
        if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            url =
                "https://dms.crconline.in/solarappnew/webmethods/apiwebservice.asmx"
        }

        DialogUtils.showProgressDialog(requireActivity(), "Installing Machine")
        Rx2AndroidNetworking.post("$url/installedMachine")
            .addBodyParameter("clientId", clientId)
            .addBodyParameter("machineId", machineId)
            .addBodyParameter("installedDate", binding.installDetails.tvInstallDate.text.toString())
            .addBodyParameter("ipaddress", binding.installDetails.etIpAddress.text.toString())
            .addBodyParameter("simno", binding.installDetails.etSimno.text.toString())
            .addBodyParameter("installreportpath", installPath)
            .addBodyParameter("machinetrainingpath", trainingPath)
            .addBodyParameter("customercertificate", certificatePath)
            .addBodyParameter(
                "installedBy",
                SessionManager.getInstance().getString(Constants.COMPLAINT_USERID)
            )
            .setPriority(Priority.MEDIUM)
            .build()
            .getObjectListSingle(PendingInstallResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userList ->
                DialogUtils.showSuccessDialog(
                    requireActivity(),
                    userList[0].status.toString(),
                    CbslMain::class.java
                )
                requireActivity().onBackPressed()
            }, { throwable ->
                DialogUtils.showFailedDialog(requireActivity(), throwable.message.toString())
                Toast.makeText(
                    requireActivity(),
                    throwable.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            })
    }


    override fun onItemClick(
        machineNo: String,
        machineId: String,
        clientId: String,
        branchname: String
    ) {
        binding.installDetails.rlInstallationDialog.visibility = View.VISIBLE
        binding.installDetails.etMachineNo.text = machineNo
        binding.installDetails.tvBranch.text = branchname
        this.machineId = machineId
        this.clientId = clientId
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

                        binding.installDetails.tvInstallDate.text =
                            Editable.Factory.getInstance().newEditable(formattedDateTime)
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

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when (parent!!.id) {
            R.id.spin_client_name -> {
                try {
                    val selectedItem = parent.getItemAtPosition(position) as ComplainClientResponse
                    clientId = selectedItem.ClientId!!
                    //        loadBranchName(cityId!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d("Not yet implemented","")

    }

}