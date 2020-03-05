package com.skichrome.oc.easyvgp.view.fragments

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAdminBinding
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.ControlPointAdapter
import com.skichrome.oc.easyvgp.view.fragments.adapters.MachineTypeAdapter
import com.skichrome.oc.easyvgp.viewmodel.AdminViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.AdminViewModelFactory
import kotlinx.android.synthetic.main.fragment_admin.*

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

        adminFragmentFabNewMachineType.setOnClickListener { showNewMachineTypeAlertDialog(R.string.admin_fragment_dialog_layout_title_new_machine_type) }
        adminFragmentFabNewCtrlPoint.setOnClickListener { showControlPointAlertDialog(R.string.admin_fragment_dialog_layout_title_new_ctrl_point) }

        adminFragmentFabMain.setOnClickListener {
            isFabOpen = !isFabOpen
            changeFabState(isFabOpen)
        }
    }

    private fun configureRecyclerView()
    {
        machineTypeAdapter = MachineTypeAdapter(viewModel)
        ctrlPointAdapter = ControlPointAdapter(viewModel)
        val spanCount = if (resources.getBoolean(R.bool.isTablet)) 4 else 3

        binding.adminFragmentMachineTypeRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        binding.adminFragmentMachineTypeRecyclerView.adapter = machineTypeAdapter

        binding.adminFragmentControlPointsRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        binding.adminFragmentControlPointsRecyclerView.adapter = ctrlPointAdapter
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
                    } else
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
                    } else
                        toast(getString(R.string.admin_fragment_dialog_error_required_fields))
                }
                setNegativeButton(R.string.admin_fragment_dialog_cancel, null)
            }
        }
        dialog?.show()
    }

    private fun showMachineTypeCtrlPointAlertDialog(controlPoints: LinkedHashMap<ControlPoint, Boolean>) = controlPoints.let {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {

                val controlPointsList = controlPoints.keys.map { controlPoint -> controlPoint.name }

                setMultiChoiceItems(
                    controlPointsList.toTypedArray(),
                    controlPoints.values.toBooleanArray()
                ) { _, which, isChecked ->
                    val itemClicked = controlPointsList[which]
                    toast("Item checked : $isChecked, $which, $itemClicked")
                }

                setTitle("Select all points linked to this machine type")
                setPositiveButton(R.string.admin_fragment_dialog_ok) { _, _ ->
                }
                setNegativeButton(R.string.admin_fragment_dialog_cancel, null)
            }
        }
        dialog?.show()
    }

    private fun changeFabState(openFab: Boolean)
    {
        when (openFab)
        {
            true ->
            {
                adminFragmentFabNewMachineTypeText.visibility = View.VISIBLE
                adminFragmentFabNewCtrlPointText.visibility = View.VISIBLE
                adminFragmentFabMain.startAnimation(fabClockwiseAnim)
                adminFragmentFabNewMachineType.startAnimation(secondaryFabOpen)
                adminFragmentFabNewCtrlPoint.startAnimation(secondaryFabOpen)
                adminFragmentFabNewMachineType.show()
                adminFragmentFabNewCtrlPoint.show()
            }
            false ->
            {
                adminFragmentFabNewMachineTypeText.visibility = View.INVISIBLE
                adminFragmentFabNewCtrlPointText.visibility = View.INVISIBLE
                adminFragmentFabMain.startAnimation(fabCounterClockwiseAnim)
                adminFragmentFabNewMachineType.startAnimation(secondaryFabClose)
                adminFragmentFabNewCtrlPoint.startAnimation(secondaryFabClose)
                adminFragmentFabNewMachineType.hide()
                adminFragmentFabNewCtrlPoint.hide()
            }
        }
    }
}