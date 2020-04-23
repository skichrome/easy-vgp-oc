package com.skichrome.oc.easyvgp.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
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
import java.text.SimpleDateFormat

class VgpListFragment : BaseBindingFragment<FragmentVgpListBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args by navArgs<VgpListFragmentArgs>()
    private val viewModel by viewModels<VgpListViewModel> {
        VgpListViewModelFactory((requireActivity().application as EasyVGPApplication).vgpListRepository)
    }

    private var vgpListAdapter by AutoClearedValue<VgpListFragmentAdapter>()

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
        pdfValidClickEvent.observe(viewLifecycleOwner, EventObserver { pdfClickedAlertDialog(it) })
        customerEmail.observe(viewLifecycleOwner, EventObserver { sendEmailIntent(it.first, it.second) })
        loadAllVgpFromMachine(args.machineId)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        vgpListAdapter = VgpListFragmentAdapter(viewModel)
        binding.vgpListFragmentRecyclerView.apply {
            adapter = vgpListAdapter
            addItemDecorationAndLinearLayoutManager()
        }
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

    private fun pdfClickedAlertDialog(report: VgpListItem)
    {
        activity?.let {
            AlertDialog.Builder(activity).apply {
                var selectedItem = 0

                setTitle(getString(R.string.share_report_dialog_title))
                setSingleChoiceItems(R.array.home_fragment_single_choice_alert_dialog, selectedItem) { _, which -> selectedItem = which }
                setPositiveButton(getString(R.string.share_report_dialog_positive_btn)) { _, _ ->
                    when (selectedItem)
                    {
                        1 -> viewModel.loadCustomerEmail(args.customerId, report)
                        else -> openPdfViewer(report)
                    }
                }
                setNegativeButton(getString(R.string.share_report_dialog_negative_btn)) { dialog, _ -> dialog.dismiss() }
            }
                .create()
                .show()
        }
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

    private fun sendEmailIntent(customerEmail: String, report: VgpListItem)
    {
        if (report.reportLocalPath == null)
        {
            binding.root.snackBar(getString(R.string.home_fragment_error_download_report_before))
            return
        }

        Intent(Intent.ACTION_SEND).apply {
            val subject = getString(R.string.share_report_email_subject)
            val userName = FirebaseAuth.getInstance().currentUser?.displayName
            val reportDate = SimpleDateFormat.getDateInstance().format(report.reportEndDate)
            val content = getString(R.string.share_report_email_content, reportDate, userName)

            if (canReadExternalStorage())
            {
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.createOrGetFile(PDF_FOLDER_NAME, report.reportLocalPath)
                    ?.let { file ->
                        if (file.exists())
                        {
                            val attachment = FileProvider.getUriForFile(requireContext(), getString(R.string.file_provider_authority), file)

                            putExtra(Intent.EXTRA_EMAIL, arrayOf(customerEmail))
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, content)
                            putExtra(Intent.EXTRA_STREAM, attachment)

                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            type = "message/rfc822"

                            resolveActivity(requireActivity().packageManager)?.let {
                                startActivity(Intent.createChooser(this, getString(R.string.share_report_intent_chooser)))
                            } ?: binding.root.snackBar(getString(R.string.share_report_no_mail_app_found))
                        }
                    }
            }

        }
    }
}