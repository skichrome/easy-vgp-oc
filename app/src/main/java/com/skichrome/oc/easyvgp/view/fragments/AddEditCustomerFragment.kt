package com.skichrome.oc.easyvgp.view.fragments

import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.Injection
import kotlinx.android.synthetic.main.fragment_add_edit_customer.*

class AddEditCustomerFragment : BaseFragment()
{
    // =================================
    //              Fields
    // =================================

    private val args: AddEditCustomerFragmentArgs by navArgs()
    private val viewModel by viewModels<CustomerViewModel> { Injection.provideCustomerViewModelFactory(requireActivity().application) }
    private lateinit var inputList: List<TextView>

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_add_edit_customer
    override fun configureFragment()
    {
        configureUI()
        configureFab()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        inputList = listOf(
            addEditCustomerFragName,
            addEditCustomerFragSiretText
        )

        if (args.customerId != -1L)
            viewModel.loadCustomerById(args.customerId)
    }

    private fun configureFab()
    {
        addEditCustomerFragFab.setOnClickListener { getUserEnteredValues() }
    }

    private fun getUserEnteredValues()
    {
        var canRegisterCustomer = true

        inputList.forEach { textView ->

            if (textView.text.toString() == "")
            {
                canRegisterCustomer = false
                textView.error = "Required field"
                view?.snackBar("You must enter all required fields")
            }
        }

        val customer = Customers(
            name = addEditCustomerFragName.text.toString(),
            siret = addEditCustomerFragSiretText.text.toString()
        )

        if (canRegisterCustomer)
            viewModel.saveCustomer(customer)
    }
}