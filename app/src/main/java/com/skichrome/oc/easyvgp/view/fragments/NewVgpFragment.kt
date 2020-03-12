package com.skichrome.oc.easyvgp.view.fragments

import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentNewVgpBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.ControlPointNewVgpAdapter
import com.skichrome.oc.easyvgp.viewmodel.VgpViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.VgpViewModelFactory

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
        configureViewModel()
        configureBinding()
        configureRecyclerView()
        configureBtn()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.onClickCommentEvent.observe(viewLifecycleOwner, EventObserver { showCommentAlertDialog(it) })
        viewModel.getMachineTypeWithControlPoints(args.machineTypeId)
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
            viewModel.saveCtrlPointDataList(args.machineId)
        }
    }

    private fun showCommentAlertDialog(index: Int)
    {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)

            // Set up the input
            val input = EditText(it)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(input)

            builder.apply {
                setTitle(R.string.frag_vgp_dialog_title)
                setPositiveButton(R.string.frag_vgp_dialog_ok) { _, _ ->
                    binding.root.snackBar(input.text.toString())
                    viewModel.setCommentToCtrlPointData(index, input.text.toString())
                }
                setNegativeButton(R.string.frag_vgp_dialog_cancel, null)
            }
        }
        dialog?.show()
    }
}