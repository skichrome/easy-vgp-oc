package com.skichrome.oc.easyvgp.view.fragments

import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAddEditCustomerBinding
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.CustomerViewModelFactory
import kotlinx.android.synthetic.main.fragment_add_edit_customer.*

class AddEditCustomerFragment : BaseBindingFragment<FragmentAddEditCustomerBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: AddEditCustomerFragmentArgs by navArgs()
    private val viewModel by viewModels<CustomerViewModel> {
        CustomerViewModelFactory((requireActivity().application as EasyVGPApplication).customerRepository)
    }

    private lateinit var inputList: List<TextView>

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_add_edit_customer
    override fun configureFragment()
    {
        configureUI()
        configureViewModel()
        configureFab()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        inputList = listOf(
            addEditCustomerFragFirstNameText,
            addEditCustomerFragLastNameText,
            addEditCustomerFragSiretText,
            addEditCustomerFragAddressText,
            addEditCustomerFragPostCodeText,
            addEditCustomerFragCityText
        )

        binding.viewModel = viewModel

        if (args.customerId != -1L)
            viewModel.loadCustomerById(args.customerId)
    }

    private fun configureViewModel() = viewModel.apply {
        errorMessage.observe(this@AddEditCustomerFragment, EventObserver { binding.root.snackBar(getString(it)) })
        customersSaved.observe(this@AddEditCustomerFragment, EventObserver { findNavController().navigateUp() })
    }

    private fun configureFab()
    {
        addEditCustomerFragFab.setOnClickListener { getUserEnteredValues() }
    }

    // --- Actions methods --- //

    private fun getUserEnteredValues()
    {
        var canRegisterCustomer = true

        inputList.forEach { textView ->

            if (textView.text.toString() == "")
            {
                canRegisterCustomer = false
                textView.error = getString(R.string.frag_add_edit_customer_error_input)
                view?.snackBar(getString(R.string.frag_add_edit_customer_error_input_snack_bar_msg))
                return@forEach
            }
        }

        if (canRegisterCustomer)
        {
            val customer = Customers(
                id = if (args.customerId != -1L) args.customerId else 0,
                firstName = addEditCustomerFragFirstNameText.text.toString(),
                lastName = addEditCustomerFragLastNameText.text.toString(),
                siret = addEditCustomerFragSiretText.text.toString(),
                postCode = addEditCustomerFragPostCodeText.text.toString().toInt(),
                address = addEditCustomerFragAddressText.text.toString(),
                city = addEditCustomerFragCityText.text.toString(),
                email = addEditCustomerFragEmailText.text.toString(),
                mobilePhone = addEditCustomerFragMobilePhoneText.text.toString().toIntOrNull(),
                notes = addEditCustomerFragNotesText.text.toString(),
                phone = addEditCustomerFragPhoneText.text.toString().toIntOrNull()
            )

            if (args.customerId != -1L)
                viewModel.updateCustomer(customer)
            else
                viewModel.saveCustomer(customer)
        }
    }
}