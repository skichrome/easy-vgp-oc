package com.skichrome.oc.easyvgp.view.fragments

import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.work.*
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentVgpListBinding
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.services.UploadReportWorker
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.VgpListFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.VgpListViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.VgpListViewModelFactory
import kotlinx.android.synthetic.main.fragment_vgp_list.*

class VgpListFragment : BaseBindingFragment<FragmentVgpListBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args by navArgs<VgpListFragmentArgs>()
    private val viewModel by viewModels<VgpListViewModel> {
        VgpListViewModelFactory((requireActivity().application as EasyVGPApplication).vgpListRepository)
    }

    private var adapter by AutoClearedValue<VgpListFragmentAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_vgp_list

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

    private fun configureViewModel() = viewModel.apply {
        message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        reportDateEvent.observe(viewLifecycleOwner, EventObserver { navigateToNewVgpSetupFragment(reportDateToEdit = it) })
        pdfClickEvent.observe(viewLifecycleOwner, EventObserver { generateReport(it) })
        pdfValidClickEvent.observe(viewLifecycleOwner, EventObserver { openPdfViewer(it) })
        loadAllVgpFromMachine(args.machineId)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = VgpListFragmentAdapter(viewModel)
        binding.vgpListFragmentRecyclerView.adapter = adapter
    }

    private fun configureBtn()
    {
        vgpListFragmentFab.setOnClickListener { navigateToNewVgpSetupFragment() }
    }

    private fun generateReport(report: VgpListItem)
    {
        val userId = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(CURRENT_LOCAL_PROFILE, -1L)

        val reportData = workDataOf(
            KEY_LOCAL_USER_ID to userId,
            KEY_LOCAL_CUSTOMER_ID to args.customerId,
            KEY_LOCAL_MACHINE_ID to args.machineId,
            KEY_LOCAL_MACHINE_TYPE_ID to args.machineTypeId,
            KEY_LOCAL_EXTRAS_REFERENCE to report.extrasReference,
            KEY_LOCAL_REPORT_DATE to report.reportDate
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = OneTimeWorkRequest.Builder(UploadReportWorker::class.java)
            .setConstraints(constraints)
            .setInputData(reportData)
            .build()
        WorkManager.getInstance(requireContext()).beginWith(work).enqueue()

        binding.root.snackBar(getString(R.string.fragment_vgp_list_work_enqueued_info))
    }

    private fun navigateToNewVgpSetupFragment(reportDateToEdit: Long = -1L)
    {
        val opt = VgpListFragmentDirections.actionVgpListFragmentToNewVgpSetupFragment(
            machineId = args.machineId,
            machineTypeId = args.machineTypeId,
            customerId = args.customerId,
            reportDateToEdit = reportDateToEdit
        )
        findNavController().navigate(opt)
    }

    private fun openPdfViewer(report: VgpListItem)
    {
        report.reportLocalPath?.let { fileName ->
            Intent(Intent.ACTION_VIEW).apply {
                if (canReadExternalStorage())
                {
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.createOrGetFile(PDF_FOLDER_NAME, fileName)
                        ?.let { file ->
                            if (file.exists())
                            {
                                val uri =
                                    FileProvider.getUriForFile(requireContext(), requireActivity().getString(R.string.file_provider_authority), file)
                                setDataAndType(uri, "application/pdf")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                                resolveActivity(requireActivity().packageManager)?.let {
                                    startActivity(this)
                                } ?: binding.root.snackBar(getString(R.string.fragment_vgp_list_no_pdf_viewer))
                            }
                            else
                                viewModel.downloadReport(report, file)
                        }
                }
            }
        }
    }
}