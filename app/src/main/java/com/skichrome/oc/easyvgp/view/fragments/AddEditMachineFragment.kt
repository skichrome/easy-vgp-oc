package com.skichrome.oc.easyvgp.view.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAddEditMachineBinding
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.MachineViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class AddEditMachineFragment : BaseBindingFragment<FragmentAddEditMachineBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args by navArgs<AddEditMachineFragmentArgs>()
    private val viewModel by viewModels<MachineViewModel> {
        MachineViewModelFactory((requireActivity().application as EasyVGPApplication).machineRepository)
    }

    private lateinit var inputList: List<TextInputEditText>
    private var machinePhotoPath: String? = null
    private var machineRemotePhotoPath: Uri? = null
    private var machineType: Long? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_add_edit_machine

    override fun configureFragment()
    {
        configureUI()
        configureViewModel()
        configureBtn()
        configureImg()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == RC_IMAGE_CAPTURE_INTENT && resultCode == RESULT_OK)
        {
            machinePhotoPath?.let {
                val machinePicture = File(it).transformBitmapFile()
                binding.addEditMachineFragmentImg.loadPhotoWithGlide(machinePicture)
                binding.addEditMachineFragmentImg.visibility = View.VISIBLE
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(FRAGMENT_STATE_PICTURE_LOCATION, machinePhotoPath)
        outState.putString(FRAGMENT_STATE_REMOTE_PICTURE_LOCATION, machineRemotePhotoPath?.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        machinePhotoPath = savedInstanceState?.getString(FRAGMENT_STATE_PICTURE_LOCATION)
        machineRemotePhotoPath = savedInstanceState?.getString(FRAGMENT_STATE_REMOTE_PICTURE_LOCATION)?.let { Uri.parse(it) }

        machineRemotePhotoPath?.let {
            binding.addEditMachineFragmentImg.loadPhotoWithGlide(it)
            binding.addEditMachineFragmentImg.visibility = View.VISIBLE
        }
            ?: machinePhotoPath?.let {
                binding.addEditMachineFragmentImg.loadPhotoWithGlide(it)
                binding.addEditMachineFragmentImg.visibility = View.VISIBLE
            }
            ?: binding.addEditMachineFragmentImg.apply { visibility = View.GONE }
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        inputList = listOf(
            binding.addEditMachineFragmentNameEditText,
            binding.addEditMachineFragmentBrandEditText,
            binding.addEditMachineFragmentModelEditText,
            binding.addEditMachineFragmentSerialEditText,
            binding.addEditMachineFragmentManufacturingYearEditText
        )

        binding.viewModel = viewModel

        if (args.machineId != -1L)
        {
            activity?.apply { toolbar?.title = getString(R.string.title_fragment_edit_machine) }
            viewModel.loadMachineToEdit(args.machineId)
        }
    }

    private fun configureViewModel() = viewModel.apply {

        machineSaved.observe(viewLifecycleOwner, EventObserver { findNavController().navigateUp() })
        errorMessage.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        machineTypes.observe(viewLifecycleOwner, Observer { machineTypes ->
            machineTypes?.let {
                configureOrUpdateSpinner(machineTypes)

                if (args.machineId != -1L)
                {
                    machine.observe(viewLifecycleOwner, Observer { machine ->

                        machine.remotePhotoRef?.let {
                            binding.addEditMachineFragmentImg.loadPhotoWithGlide(it)
                            binding.addEditMachineFragmentImg.visibility = View.VISIBLE
                        }
                            ?: machine.localPhotoRef?.let {
                                binding.addEditMachineFragmentImg.loadPhotoWithGlide(it)
                                binding.addEditMachineFragmentImg.visibility = View.VISIBLE
                            }
                            ?: binding.addEditMachineFragmentImg.apply { visibility = View.GONE }

                        machine?.let { machineNotNull ->
                            val type = it.firstOrNull { it.id == machineNotNull.type }
                            type?.let { typeExist -> binding.addEditMachineFragmentMachineTypeSpinner.setSelection((it.indexOf(typeExist))) }
                        }
                    })
                }
            }
        })
    }

    private fun configureOrUpdateSpinner(machines: List<MachineType>)
    {
        val machinesTypeArray = machines.map { it.name }.toTypedArray()
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, machinesTypeArray)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        binding.addEditMachineFragmentMachineTypeSpinner.adapter = arrayAdapter
        binding.addEditMachineFragmentMachineTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(adapter: AdapterView<*>?) = Unit
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, itemId: Long)
            {
                machineType = machines[position].id
            }
        }
    }

    private fun configureBtn()
    {
        binding.addEditCustomerFragFab.setOnClickListener { getUserEnteredValues() }
    }

    private fun configureImg()
    {
        binding.addEditMachineFragmentImgCard.setOnClickListener { launchCamera() }
    }

    private fun getUserEnteredValues()
    {
        var canRegisterCustomer = true

        inputList.forEach { textView ->

            if (textView.text.toString() == "")
            {
                canRegisterCustomer = false
                textView.error = getString(R.string.frag_add_edit_customer_error_input)
                view?.snackBar(getString(R.string.frag_add_edit_customer_error_input_snack_bar_msg))
                return@forEach
            }
        }

        if (machineType == null || machinePhotoPath == null)
            canRegisterCustomer = false

        if (canRegisterCustomer)
        {
            val machine = Machine(
                machineId = if (args.machineId != -1L) args.machineId else 0,
                type = machineType!!,
                serial = binding.addEditMachineFragmentSerialEditText.text.toString(),
                customer = args.customerId,
                brand = binding.addEditMachineFragmentBrandEditText.text.toString(),
                name = binding.addEditMachineFragmentNameEditText.text.toString(),
                model = binding.addEditMachineFragmentModelEditText.text.toString(),
                manufacturingYear = binding.addEditMachineFragmentManufacturingYearEditText.text.toString().toInt(),
                localPhotoRef = machinePhotoPath,
                remotePhotoRef = machineRemotePhotoPath
            )

            if (args.machineId != -1L)
                viewModel.updateMachine(machine)
            else
                viewModel.saveMachine(machine)
        }
    }

    private fun launchCamera()
    {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile = try
                {
                    if (canWriteExternalStorage())
                    {
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            ?.createOrGetJpegFile(PICTURES_FOLDER_NAME, "machine")
                    }
                    else null

                }
                catch (e: IOException)
                {
                    Log.e("AddEditMachineFrag", "Error when getting photo file : ${e.message}", e)
                    null
                }

                photoFile?.also { file ->
                    machinePhotoPath = file.absolutePath
                    val uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        requireActivity().getString(R.string.file_provider_authority),
                        file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE_INTENT)
                }
            }
        }
    }
}