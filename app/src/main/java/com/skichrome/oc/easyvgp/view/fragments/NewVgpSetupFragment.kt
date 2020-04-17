package com.skichrome.oc.easyvgp.view.fragments

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentNewVgpSetupBinding
import com.skichrome.oc.easyvgp.model.local.ControlType
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.NewVgpSetupViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.NewVgpSetupViewModelFactory

class NewVgpSetupFragment : BaseBindingFragment<FragmentNewVgpSetupBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: NewVgpSetupFragmentArgs by navArgs()

    private val viewModel: NewVgpSetupViewModel by viewModels {
        NewVgpSetupViewModelFactory((requireActivity().application as EasyVGPApplication).newVgpSetupRepository)
    }

    private lateinit var nonNullableViewContent: List<TextInputEditText>
    private lateinit var selectedControlType: ControlType
    private var currentExtraId = -1L

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_new_vgp_setup

    override fun configureFragment()
    {
        configureViewModel()
        configureBinding()
        configureFab()
        configureSpinner()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        if (args.reportDateToEdit != -1L)
            viewModel.loadPreviousNewVgpExtras(args.reportDateToEdit)

        viewModel.success.observe(viewLifecycleOwner, EventObserver { navigateToNewVgpFragment(it) })
        viewModel.machineWithControlPointsDataExtras.observe(viewLifecycleOwner, Observer {
            it?.let { extra ->
                currentExtraId = extra.id
            }
        })
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        nonNullableViewContent = listOf(
            binding.fragmentNewVgpSetupMachineHours,
            binding.fragmentNewVgpSetupInterventionPlace
        )
    }

    private fun configureFab()
    {
        binding.fragmentNewVgpSetupFab.setOnClickListener { getUserData() }
    }

    private fun configureSpinner()
    {
        val spinnerItems = ControlType.values().map { getString(it.type) }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerItems)

        binding.fragmentNewVgpSetupMachineControlType.adapter = adapter
        binding.fragmentNewVgpSetupMachineControlType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(adapterView: AdapterView<*>?, v: View?, position: Int, itemId: Long)
            {
                selectedControlType = ControlType.values()[position]
                Log.e("NewVgpSetupFrag", "Current selected item : ${adapter.getItem(position)}")
                Log.e("NewVgpSetupFrag", "Current selected item stored : ${getString(selectedControlType.type)}")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) = Unit
        }
    }

    private fun getUserData()
    {
        var canSave = nonNullableViewContent.none { it.text == null || it.text.toString() == "" }
        if (args.reportDateToEdit != -1L && currentExtraId == -1L)
            canSave = false

        if (canSave)
        {
            val reportDateMillis = if (args.reportDateToEdit == -1L) System.currentTimeMillis() else args.reportDateToEdit
            val reportEndDate = when (selectedControlType)
            {
                ControlType.PUT_INTO_SERVICE ->
                {
                    reportDateMillis + 7889399962  // + 3 months
                }
                ControlType.PUT_BACK_INTO_SERVICE ->
                {
                    reportDateMillis + 7889399962  // + 3 months
                }
                ControlType.VGP ->
                {
                    reportDateMillis + 7889399962  // + 3 months
                }
            }

            val extras = MachineControlPointDataExtra(
                id = if (args.reportDateToEdit == -1L) 0L else currentExtraId,
                reportDate = reportDateMillis,
                isMachineCE = binding.fragmentNewVgpSetupIsMachineMarkedCE.isChecked,
                isLiftingEquip = binding.fragmentNewVgpSetupIsMachineLiftingEquiped.isChecked,
                isMachineClean = binding.fragmentNewVgpSetupIsMachineClean.isChecked,
                machineNotice = binding.fragmentNewVgpSetupIsMachineNoticeAvailable.isChecked,
                isValid = false,
                controlType = selectedControlType,
                interventionPlace = binding.fragmentNewVgpSetupInterventionPlace.text.toString(),
                machineHours = binding.fragmentNewVgpSetupMachineHours.text.toString().toInt(),
                reportEndDate = reportEndDate
            )

            if (args.reportDateToEdit == -1L)
                viewModel.createNewVgpExtras(extras)
            else
                viewModel.updateNewVgpExtras(extras)
        }
        else
            binding.root.snackBar("Error, can't save")
    }

    private fun navigateToNewVgpFragment(vgpExtraId: Long)
    {
        val opt = NewVgpSetupFragmentDirections.actionNewVgpSetupFragmentToNewVgpFragment(
            machineId = args.machineId,
            customerId = args.customerId,
            machineTypeId = args.machineTypeId,
            reportDateToEdit = args.reportDateToEdit,
            vgpReportExtra = vgpExtraId
        )
        findNavController().navigate(opt)
    }
}