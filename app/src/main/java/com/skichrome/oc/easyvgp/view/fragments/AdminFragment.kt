package com.skichrome.oc.easyvgp.view.fragments

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAdminBinding
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.local.util.MachineTypeCtrlPtMultiChoiceItems
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.ControlPointAdapter
import com.skichrome.oc.easyvgp.view.fragments.adapters.MachineTypeAdapter
import com.skichrome.oc.easyvgp.viewmodel.AdminViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.AdminViewModelFactory

class AdminFragment : BaseBindingFragment<FragmentAdminBinding>()
{
    // =================================
    //              Fields
    // =================================

    private var machineTypeAdapter by AutoClearedValue<MachineTypeAdapter>()
    private var ctrlPointAdapter by AutoClearedValue<ControlPointAdapter>()

    private val viewModel by viewModels<AdminViewModel> {
        AdminViewModelFactory((requireActivity().application as EasyVGPApplication).adminRepository)
    }

    private lateinit var secondaryFabOpen: Animation
    private lateinit var secondaryFabClose: Animation
    private lateinit var fabCounterClockwiseAnim: Animation
    private lateinit var fabClockwiseAnim: Animation
    private var isFabOpen = false

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_admin

    override fun configureFragment()
    {
        configureViewModel()
        configureBtn()
        configureRecyclerView()
        configureUI()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {
        message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        controlPointsFromMachineType.observe(viewLifecycleOwner, Observer {
            it?.let { ctrlPoints ->
                showMachineTypeCtrlPointAlertDialog(ctrlPoints)
            }
        })
        onClickControlPoint.observe(
            viewLifecycleOwner,
            EventObserver { showControlPointAlertDialog(R.string.admin_fragment_dialog_layout_title_update_ctrl_point, it) })
        onLongClickMachineType.observe(
            viewLifecycleOwner,
            EventObserver { showNewMachineTypeAlertDialog(R.string.admin_fragment_dialog_layout_title_update_machine_type, it) })
    }

    private fun configureBtn()
    {
        fabClockwiseAnim = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_cw)
        fabCounterClockwiseAnim = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_ccw)
        secondaryFabOpen = AnimationUtils.loadAnimation(context, R.anim.secondary_fab_open)
        secondaryFabClose = AnimationUtils.loadAnimation(context, R.anim.secondary_fab_close)

        binding.adminFragmentFabNewMachineType.setOnClickListener { showNewMachineTypeAlertDialog(R.string.admin_fragment_dialog_layout_title_new_machine_type) }
        binding.adminFragmentFabNewCtrlPoint.setOnClickListener { showControlPointAlertDialog(R.string.admin_fragment_dialog_layout_title_new_ctrl_point) }

        binding.adminFragmentFabMain.setOnClickListener {
            isFabOpen = !isFabOpen
            changeFabState(isFabOpen)
        }
    }

    private fun configureRecyclerView()
    {
        machineTypeAdapter = MachineTypeAdapter(viewModel)
        ctrlPointAdapter = ControlPointAdapter(viewModel)

        binding.adminFragmentMachineTypeRecyclerView.apply {
            adapter = machineTypeAdapter
            addItemDecorationAndLinearLayoutManager()
        }
        binding.adminFragmentControlPointsRecyclerView.apply {
            adapter = ctrlPointAdapter
            addItemDecorationAndLinearLayoutManager()
        }
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    private fun showNewMachineTypeAlertDialog(msgRef: Int, machineType: MachineType? = null)
    {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val dialogView = View.inflate(context, R.layout.dialog_machine_type, null)
                val legalNameEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogMachineTypeLegalName)
                val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogMachineTypeName)

                machineType?.let { type -> legalNameEditText.setText(type.legalName) }
                machineType?.let { type -> nameEditText.setText(type.name) }
                setView(dialogView)

                setTitle(msgRef)
                setPositiveButton(R.string.admin_fragment_dialog_ok) { _, _ ->
                    val newName = nameEditText.text
                    val newLegalName = legalNameEditText.text

                    if (newLegalName != null && newLegalName.toString() != "" && newName != null && newName.toString() != "")
                    {
                        val newMachine =
                            MachineType(
                                id = machineType?.id ?: System.currentTimeMillis(),
                                legalName = newLegalName.toString(),
                                name = newName.toString()
                            )
                        machineType?.let { viewModel.updateMachineType(newMachine) } ?: viewModel.insertMachineType(newMachine)

                        if (isFabOpen)
                        {
                            isFabOpen = !isFabOpen
                            changeFabState(isFabOpen)
                        }
                    }
                    else
                        toast(getString(R.string.admin_fragment_dialog_error_required_fields))
                }
                setNegativeButton(R.string.admin_fragment_dialog_cancel, null)
            }
        }
        dialog?.show()
    }

    private fun showControlPointAlertDialog(msgRef: Int, ctrlPoint: ControlPoint? = null)
    {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val dialogView = View.inflate(context, R.layout.dialog_ctrl_point, null)
                val codeEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogCtrlPointCode)
                val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogCtrlPointName)

                ctrlPoint?.let { ctrlP -> codeEditText.setText(ctrlP.code) }
                ctrlPoint?.let { ctrlP -> nameEditText.setText(ctrlP.name) }
                setView(dialogView)

                setTitle(msgRef)
                setPositiveButton(R.string.admin_fragment_dialog_ok) { _, _ ->
                    val newName = nameEditText.text
                    val newCode = codeEditText.text

                    if (newCode != null && newCode.toString() != "" && newName != null && newName.toString() != "")
                    {
                        val newControlPoint =
                            ControlPoint(
                                id = ctrlPoint?.id ?: System.currentTimeMillis(),
                                code = newCode.toString(),
                                name = newName.toString()
                            )
                        ctrlPoint?.let { viewModel.updateControlPoint(newControlPoint) } ?: viewModel.insertControlPoint(newControlPoint)

                        if (isFabOpen)
                        {
                            isFabOpen = !isFabOpen
                            changeFabState(isFabOpen)
                        }
                    }
                    else
                        toast(getString(R.string.admin_fragment_dialog_error_required_fields))
                }
                setNegativeButton(R.string.admin_fragment_dialog_cancel, null)
            }
        }
        dialog?.show()
    }

    private fun showMachineTypeCtrlPointAlertDialog(controlPoints: List<MachineTypeCtrlPtMultiChoiceItems>) = controlPoints.let {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {

                setMultiChoiceItems(
                    controlPoints.map { controlPoint -> controlPoint.ctrlPoint.name }.toTypedArray(),
                    controlPoints.map { ctrlPoint -> ctrlPoint.isChecked }.toBooleanArray()
                ) { _, which, isChecked ->
                    val itemClicked = controlPoints[which]
                    toast("Item checked : $isChecked, $which, ${itemClicked.ctrlPoint.name}")
                    controlPoints[which].isChecked = isChecked
                }

                setTitle(R.string.admin_fragment_dialog_layout_title_machine_type_ctrl_point_list)
                setPositiveButton(R.string.admin_fragment_dialog_ok) { _, _ ->
                    val ctrlPointList = mutableListOf<ControlPoint>()
                    controlPoints.forEach { machTypeCtrlPtItem ->
                        if (machTypeCtrlPtItem.isChecked)
                            ctrlPointList.add(machTypeCtrlPtItem.ctrlPoint)
                    }
                    if (controlPoints.isNotEmpty())
                        viewModel.insertOrUpdateMachineTypeWithControlPoints(
                            MachineTypeWithControlPoints(
                                machineType = controlPoints.first().machineType,
                                controlPoints = ctrlPointList
                            )
                        )
                }
                setNegativeButton(R.string.admin_fragment_dialog_cancel, null)
            }
        }
        dialog?.show()
    }

    private fun changeFabState(openFab: Boolean)
    {
        binding.adminFragmentFabNewMachineType.isClickable = openFab
        binding.adminFragmentFabNewCtrlPoint.isClickable = openFab

        when (openFab)
        {
            true ->
            {
                binding.adminFragmentFabNewMachineTypeTextCard.visibility = View.VISIBLE
                binding.adminFragmentFabNewCtrlPointTextCard.visibility = View.VISIBLE
                binding.adminFragmentFabMain.startAnimation(fabClockwiseAnim)
                binding.adminFragmentFabNewMachineType.startAnimation(secondaryFabOpen)
                binding.adminFragmentFabNewCtrlPoint.startAnimation(secondaryFabOpen)
                binding.adminFragmentFabNewMachineType.show()
                binding.adminFragmentFabNewCtrlPoint.show()
            }
            false ->
            {
                binding.adminFragmentFabNewMachineTypeTextCard.visibility = View.INVISIBLE
                binding.adminFragmentFabNewCtrlPointTextCard.visibility = View.INVISIBLE
                binding.adminFragmentFabMain.startAnimation(fabCounterClockwiseAnim)
                binding.adminFragmentFabNewMachineType.startAnimation(secondaryFabClose)
                binding.adminFragmentFabNewCtrlPoint.startAnimation(secondaryFabClose)
                binding.adminFragmentFabNewMachineType.hide()
                binding.adminFragmentFabNewCtrlPoint.hide()
            }
        }
    }
}