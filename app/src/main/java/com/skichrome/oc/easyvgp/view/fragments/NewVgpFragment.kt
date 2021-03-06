package com.skichrome.oc.easyvgp.view.fragments

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentNewVgpBinding
import com.skichrome.oc.easyvgp.model.local.database.ControlResult
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.ControlPointNewVgpAdapter
import com.skichrome.oc.easyvgp.viewmodel.VgpViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.VgpViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class NewVgpFragment : BaseBindingFragment<FragmentNewVgpBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: NewVgpFragmentArgs by navArgs()
    private val viewModel by viewModels<VgpViewModel> {
        VgpViewModelFactory((requireActivity().application as EasyVGPApplication).newVgpRepository)
    }

    private var adapter by AutoClearedValue<ControlPointNewVgpAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_new_vgp

    override fun configureFragment()
    {
        configureUI()
        configureViewModel()
        configureBinding()
        configureRecyclerView()
        configureBtn()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        if (args.reportDateToEdit != -1L)
            activity?.apply { toolbar?.title = getString(R.string.title_fragment_vgp_edit) }
    }

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.onClickCommentEvent.observe(viewLifecycleOwner, EventObserver { showCommentAlertDialog(it) })
        viewModel.onReportSaved.observe(viewLifecycleOwner, EventObserver { if (it) navigateToVgpList() })

        if (args.reportDateToEdit == -1L)
            viewModel.getMachineTypeWithControlPoints(args.machineTypeId)
        else
            viewModel.loadPreviouslyCreatedReport(args.reportDateToEdit)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = ControlPointNewVgpAdapter(viewModel)
        binding.fragVGPRecyclerView.setHasFixedSize(true)
        binding.fragVGPRecyclerView.adapter = adapter
    }

    private fun configureBtn()
    {
        binding.fragVGPFab.setOnClickListener {
            showControlResultAlertDialog()
        }
    }

    private fun showCommentAlertDialog(indexAndComment: Pair<Int, String?>)
    {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)

            val dialogView = View.inflate(context, R.layout.dialog_comment, null)
            val commentEditText = dialogView.findViewById<TextInputEditText>(R.id.dialogCommentCommentEditText)
            indexAndComment.second?.let { comment -> commentEditText.setText(comment) }

            builder.setView(dialogView)

            builder.apply {
                setTitle(R.string.frag_vgp_dialog_title)
                setPositiveButton(R.string.frag_vgp_dialog_ok) { _, _ ->
                    viewModel.setCommentToCtrlPointData(indexAndComment.first, commentEditText.text.toString())
                    adapter.notifyItemChanged(indexAndComment.first)
                }
                setNegativeButton(R.string.frag_vgp_dialog_cancel, null)
            }
        }
        dialog?.show()
    }

    private fun showControlResultAlertDialog()
    {
        activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(R.string.dialog_control_result_title)
                var selectedItem = 0

                val choicesArray = ControlResult.values()
                setSingleChoiceItems(
                    choicesArray.map { resultRes -> getString(resultRes.result) }.toTypedArray(),
                    selectedItem
                ) { _, position -> selectedItem = position }
                setPositiveButton(R.string.dialog_control_result_positive_btn) { _, _ ->
                    viewModel.updateControlResult(
                        extraId = args.vgpReportExtra,
                        machineId = args.machineId,
                        isUpdateMode = args.reportDateToEdit != -1L,
                        controlResult = choicesArray[selectedItem]
                    )
                }
                setNegativeButton(R.string.dialog_control_result_negative_btn, null)
            }
                .create()
                .show()
        }
    }

    private fun navigateToVgpList()
    {
        val opt = NewVgpFragmentDirections.actionNewVgpFragmentToVgpListFragment(
            machineId = args.machineId,
            machineTypeId = args.machineTypeId,
            customerId = args.customerId
        )
        findNavController().navigate(opt)
    }
}