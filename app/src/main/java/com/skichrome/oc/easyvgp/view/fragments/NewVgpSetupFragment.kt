package com.skichrome.oc.easyvgp.view.fragments

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentNewVgpSetupBinding
import com.skichrome.oc.easyvgp.model.local.ControlType
import com.skichrome.oc.easyvgp.model.local.database.ControlResult
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.ONE_YEAR_TIME
import com.skichrome.oc.easyvgp.util.SIX_MONTHS_TIME
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.NewVgpSetupViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.NewVgpSetupViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat

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
    private lateinit var nonNullableViewContentIfLoadEnabled: List<TextInputEditText>
    private lateinit var selectedControlType: ControlType
    private var currentExtraId = -1L
    private var currentReportDate = -1L
    private var userSelectedReportDate = System.currentTimeMillis()
    private var navigationInstructionFromFab = false

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_new_vgp_setup

    override fun configureFragment()
    {
        configureViewModel()
        configureBinding()
        configureUI()
        configureFab()
        configureSpinner()
    }

    override fun onPause()
    {
        if (!navigationInstructionFromFab && currentReportDate != -1L)
            viewModel.deleteVgpExtras(currentReportDate).also { navigationInstructionFromFab = false }
        super.onPause()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        val dateFormat = SimpleDateFormat.getDateInstance()
        when
        {
            args.reportDateToEdit == -1L ->
            {
                userSelectedReportDate = if (args.reportDateFromDatePicker != -1L) args.reportDateFromDatePicker else System.currentTimeMillis()

                binding.fragmentNewVgpSetupReportDate.setText(dateFormat.format(userSelectedReportDate))
                binding.fragmentNewVgpSetupReportDateLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                binding.fragmentNewVgpSetupReportDateSetBtn.isEnabled = true
                binding.fragmentNewVgpSetupReportDateSetBtn.setOnClickListener {
                    navigateToDatePickerFragment()
                }
            }
            args.reportDateToEdit != -1L ->
            {
                activity?.apply { toolbar?.title = getString(R.string.title_fragment_vgp_setup_edit) }
                binding.fragmentNewVgpSetupReportDate.setText(dateFormat.format(args.reportDateToEdit))
                binding.fragmentNewVgpSetupReportDateLayout.endIconMode = TextInputLayout.END_ICON_NONE
                binding.fragmentNewVgpSetupReportDateSetBtn.isEnabled = false
            }
        }

        binding.fragmentNewVgpSetupIsMachineControlWithLoad.setOnCheckedChangeListener { _, isChecked ->
            configureDisabledFieldsStatus(isChecked)
        }
        if (!binding.fragmentNewVgpSetupIsMachineControlWithLoad.isChecked)
        {
            configureDisabledFieldsStatus(false)
            nonNullableViewContentIfLoadEnabled.forEach { it.error = null }
        }
    }

    private fun configureViewModel()
    {
        if (args.reportDateToEdit != -1L)
            viewModel.loadPreviousNewVgpExtras(args.reportDateToEdit)

        if (args.reportDateToEdit == -1L && currentReportDate != -1L)
            viewModel.loadPreviousNewVgpExtras(currentReportDate)

        viewModel.success.observe(viewLifecycleOwner, EventObserver { navigateToNewVgpFragment(it) })
        viewModel.machineWithControlPointsDataExtras.observe(viewLifecycleOwner, Observer {
            it?.let { extra ->
                currentExtraId = extra.id
                selectedControlType = extra.controlType
                binding.fragmentNewVgpSetupMachineControlType.setSelection(selectedControlType.id)
            }
        })
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        nonNullableViewContent = listOf(
            binding.fragmentNewVgpSetupMachineHours,
            binding.fragmentNewVgpSetupInterventionPlace,
            binding.fragmentNewVgpSetupReportDate
        )
        nonNullableViewContentIfLoadEnabled = listOf(
            binding.fragmentNewVgpSetupControlLoadType,
            binding.fragmentNewVgpSetupControlLoadValue
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
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) = Unit
        }
    }

    private fun configureDisabledFieldsStatus(isEnabled: Boolean)
    {
        binding.fragmentNewVgpSetupIsMachineControlWithNominalLoad.isEnabled = isEnabled
        binding.fragmentNewVgpSetupIsMachineControlTriggeredSensors.isEnabled = isEnabled
        binding.fragmentNewVgpSetupControlLoadValue.isEnabled = isEnabled
        binding.fragmentNewVgpSetupControlLoadType.isEnabled = isEnabled

        nonNullableViewContentIfLoadEnabled.forEach { it.error = null }
    }

    private fun getUserData()
    {
        navigationInstructionFromFab = true
        var canSave = true
        nonNullableViewContent.forEach { editText ->

            if (editText.text == null || editText.text.toString() == "")
            {
                canSave = false
                editText.error = getString(R.string.frag_add_edit_customer_error_input)
                view?.snackBar(getString(R.string.frag_add_edit_customer_error_input_snack_bar_msg))
                return@forEach
            }
        }

        nonNullableViewContentIfLoadEnabled.forEach { editText ->
            if (binding.fragmentNewVgpSetupIsMachineControlWithLoad.isChecked && (editText.text == null || editText.text.toString() == ""))
            {
                canSave = false
                editText.error = getString(R.string.frag_add_edit_customer_error_input)
                view?.snackBar(getString(R.string.frag_add_edit_customer_error_input_snack_bar_msg))
                return@forEach
            }
        }

        if (args.reportDateToEdit != -1L && currentExtraId == -1L)
            canSave = false

        if (canSave)
        {
            val reportDateMillis = if (args.reportDateToEdit == -1L) userSelectedReportDate else args.reportDateToEdit
            val reportEndDate = when (binding.fragmentNewVgpSetupIsMachineLiftingEquiped.isChecked)
            {
                true -> reportDateMillis + SIX_MONTHS_TIME
                false -> reportDateMillis + ONE_YEAR_TIME
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
                reportEndDate = reportEndDate,
                isTestsWithLoad = binding.fragmentNewVgpSetupIsMachineControlWithLoad.isChecked,
                isTestsWithNominalLoad = if (binding.fragmentNewVgpSetupIsMachineControlWithNominalLoad.isEnabled) binding.fragmentNewVgpSetupIsMachineControlWithNominalLoad.isChecked else null,
                loadMass = binding.fragmentNewVgpSetupControlLoadValue.text.toString().toIntOrNull(),
                loadType = if (binding.fragmentNewVgpSetupControlLoadType.text?.toString() == "") null else binding.fragmentNewVgpSetupControlLoadType.text.toString(),
                testsHasTriggeredSensors = if (binding.fragmentNewVgpSetupIsMachineControlTriggeredSensors.isEnabled) binding.fragmentNewVgpSetupIsMachineControlTriggeredSensors.isChecked else null,
                controlGlobalResult = ControlResult.RESULT_OK
            )

            if (args.reportDateToEdit == -1L)
                viewModel.createNewVgpExtras(extras).also { currentReportDate = reportDateMillis }
            else
                viewModel.updateNewVgpExtras(extras)
        }
    }

    private fun navigateToDatePickerFragment()
    {
        val opt = NewVgpSetupFragmentDirections.actionNewVgpSetupFragmentToDateSelectorFragment(
            machineId = args.machineId,
            machineTypeId = args.machineTypeId,
            customerId = args.customerId,
            reportDateToEdit = args.reportDateToEdit
        )
        findNavController().navigate(opt)
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