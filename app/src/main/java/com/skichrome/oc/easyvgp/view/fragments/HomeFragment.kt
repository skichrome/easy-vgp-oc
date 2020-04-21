package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentHomeBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.HomeReportFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.HomeViewModelFactory

class HomeFragment : BaseBindingFragment<FragmentHomeBinding>()
{
    // =================================
    //              Fields
    // =================================

    private var adapter: HomeReportFragmentAdapter by AutoClearedValue()

    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory((requireActivity().application as EasyVGPApplication).homeRepository)
    }

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_home
    override fun configureFragment()
    {
        configureBtn()
        configureViewModel()
        configureRecyclerView()
        configureUI()
        configureChart()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureBtn()
    {
        binding.homeFragmentFab.setOnClickListener { navigateToCustomersFragment() }
    }

    private fun configureViewModel() = viewModel.apply {
        currentUserId.observe(viewLifecycleOwner, EventObserver { toast("saved : $it") })
    }

    private fun configureRecyclerView()
    {
        adapter = HomeReportFragmentAdapter(viewModel)
        binding.homeFragmentRecyclerView.adapter = adapter
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    // --- Navigation --- //

    private fun navigateToCustomersFragment() = findNavController().navigate(R.id.action_homeFragment_to_customerFragment)

    // -- Test

    private fun configureChart()
    {
        val max = 100.0f
        val first = 24.9f
        val second = 10.5f
        val third = 33.2f
        val empty = max - first - second - third

        val entries = arrayListOf<PieEntry>()

        entries.add(PieEntry(first, "Deadline over"))
        entries.add(PieEntry(second, "5 days"))
        entries.add(PieEntry(third, "15 days"))
        entries.add(PieEntry(empty, "no close deadlines"))
        val set = PieDataSet(entries, "").apply {
            val colors = IntArray(4)
            colors[0] = R.color.deadlineOverColor
            colors[1] = R.color.deadline5DaysColor
            colors[2] = R.color.deadline15DaysColor
            colors[3] = R.color.noDeadlineColor

            setColors(colors, requireContext())
        }
        val pieData = PieData(set).apply {
            setValueFormatter(PercentFormatter(binding.homeFragmentChart))
        }

        binding.homeFragmentChart.apply {
            setEntryLabelTextSize(25f)
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            centerText = "Deadlines"
            description = null
            data = pieData
            invalidate()
        }
    }
}