package com.skichrome.oc.easyvgp.view.fragments

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAdminBinding
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.MachineTypeAdapter
import com.skichrome.oc.easyvgp.viewmodel.AdminViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.AdminViewModelFactory
import kotlinx.android.synthetic.main.fragment_admin.*

class AdminFragment : BaseBindingFragment<FragmentAdminBinding>()
{
    // =================================
    //              Fields
    // =================================

    private var adapter by AutoClearedValue<MachineTypeAdapter>()

    private val viewModel by viewModels<AdminViewModel> {
        AdminViewModelFactory((requireActivity().application as EasyVGPApplication).adminRepository)
    }

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
        message.observe(this@AdminFragment, EventObserver { binding.root.snackBar(getString(it)) })
        onClickMachineType.observe(this@AdminFragment, EventObserver {})
        onLongClickMachineType.observe(
            this@AdminFragment,
            EventObserver { showNewMachineTypeAlertDialog(R.string.admin_fragment_dialog_layout_title_update, it) })
    }

    private fun configureBtn()
    {
        adminFragmentFab.setOnClickListener { showNewMachineTypeAlertDialog(R.string.admin_fragment_dialog_layout_title_new) }
    }

    private fun configureRecyclerView()
    {
        adapter = MachineTypeAdapter(viewModel)
        val spanCount = if (resources.getBoolean(R.bool.isTablet)) 4 else 3

        binding.adminFragmentRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        binding.adminFragmentRecyclerView.adapter = adapter
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
                val dialogView = View.inflate(context, R.layout.dialog_input_text, null)
                val legalNameEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogInputLegalName)
                val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogInputName)

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
                                name = newName.toString(),
                                remoteId = ""
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
}