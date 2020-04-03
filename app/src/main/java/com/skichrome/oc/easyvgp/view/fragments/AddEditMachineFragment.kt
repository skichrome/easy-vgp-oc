package com.skichrome.oc.easyvgp.view.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAddEditMachineBinding
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.MachineViewModelFactory
import kotlinx.android.synthetic.main.fragment_add_edit_machine.*
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

    private lateinit var inputList: List<TextView>
    private var machineFilePath: String? = null
    private var machineType: Long? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_add_edit_machine

    override fun configureFragment()
    {
        configureViewModel()
        configureUI()
        configureBtn()
        configureImg()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && machineFilePath != null)
            Glide.with(this).load(File(machineFilePath!!)).centerCrop().into(addEditMachineFragmentImg)
        super.onActivityResult(requestCode, resultCode, data)
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {

        machineSaved.observe(viewLifecycleOwner, EventObserver { findNavController().navigateUp() })
        errorMessage.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        machineTypes.observe(viewLifecycleOwner, Observer { machineTypes ->
            machineTypes?.let {
                configureOrUpdateSpinner(machineTypes)

                if (args.machineId != -1L)
                {
                    machine.observe(viewLifecycleOwner, Observer { machine ->

                        machine.remotePhotoRef?.let { remotePhoto ->
                            Glide.with(this@AddEditMachineFragment).load(remotePhoto).centerCrop().into(addEditMachineFragmentImg)
                            Log.e("AddEditMachFrag", "Remote photo path save : $remotePhoto")
                        }
                            ?: machine.localPhotoRef?.let { localPhoto ->
                                machineFilePath = localPhoto
                                Glide.with(this@AddEditMachineFragment).load(File(localPhoto)).centerCrop().into(addEditMachineFragmentImg)
                                Log.e("AddEditMachFrag", "Local photo path save : $localPhoto")
                            }

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

    private fun configureUI()
    {
        inputList = listOf(
            addEditMachineFragmentName,
            addEditMachineFragmentBrand,
            addEditMachineFragmentModel,
            addEditMachineFragmentSerial,
            addEditMachineFragmentManufacturingYear
        )

        binding.viewModel = viewModel

        if (args.machineId != -1L)
            viewModel.loadMachineToEdit(args.machineId)
    }

    private fun configureBtn()
    {
        binding.addEditCustomerFragFab.setOnClickListener { getUserEnteredValues() }
    }

    private fun configureImg()
    {
        binding.addEditMachineFragmentImg.setOnClickListener { launchCamera() }
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

        if (machineType == null || machineFilePath == null)
            canRegisterCustomer = false

        if (canRegisterCustomer)
        {
            val machine = Machine(
                machineId = if (args.machineId != -1L) args.machineId else 0,
                type = machineType!!,
                serial = addEditMachineFragmentSerial.text.toString(),
                customer = args.customerId,
                brand = addEditMachineFragmentBrand.text.toString(),
                name = addEditMachineFragmentName.text.toString(),
                model = addEditMachineFragmentModel.text.toString(),
                manufacturingYear = addEditMachineFragmentManufacturingYear.text.toString().toInt(),
                localPhotoRef = machineFilePath
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
                    machineFilePath = file.absolutePath
                    val uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        AUTHORITY,
                        file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}