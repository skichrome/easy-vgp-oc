package com.skichrome.oc.easyvgp.view.fragments

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAddEditMachineBinding
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.MachineViewModelFactory
import kotlinx.android.synthetic.main.fragment_add_edit_machine.*

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
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {

        machineSaved.observe(this@AddEditMachineFragment, EventObserver { findNavController().navigateUp() })
        errorMessage.observe(this@AddEditMachineFragment, EventObserver { binding.root.snackBar(getString(it)) })
        machineTypes.observe(this@AddEditMachineFragment, Observer { machineTypes ->
            machineTypes?.let {
                configureOrUpdateSpinner(machineTypes)

                if (args.machineId != -1L)
                {
                    machine.observe(this@AddEditMachineFragment, Observer { machine ->
                        machine?.let {
                            binding.addEditMachineFragmentMachineTypeSpinner.setSelection((machine.type - 1).toInt())
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
                machineType = (position + 1).toLong()
            }
        }
    }

    private fun configureUI()
    {
        inputList = listOf(
            addEditMachineFragmentName,
            addEditMachineFragmentBrand,
            addEditMachineFragmentSerial
        )

        binding.viewModel = viewModel

        if (args.machineId != -1L)
            viewModel.loadMachineToEdit(args.machineId)
    }

    private fun configureBtn()
    {
        binding.addEditCustomerFragFab.setOnClickListener { getUserEnteredValues() }
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

        if (machineType == null)
            canRegisterCustomer = false

        if (canRegisterCustomer)
        {
            val machine = Machine(
                machineId = if (args.machineId != -1L) args.machineId else 0,
                type = machineType!!,
                serial = addEditMachineFragmentSerial.text.toString(),
                customer = args.customerId,
                brand = addEditMachineFragmentBrand.text.toString(),
                name = addEditMachineFragmentName.text.toString()
            )

            if (args.machineId != -1L)
                viewModel.updateMachine(machine)
            else
                viewModel.saveMachine(machine)
        }
    }
}