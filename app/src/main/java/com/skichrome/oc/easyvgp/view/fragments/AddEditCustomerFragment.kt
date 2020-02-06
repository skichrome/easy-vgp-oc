package com.skichrome.oc.easyvgp.view.fragments

import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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

        if (args.customerId != -1L)
            viewModel.loadCustomerById(args.customerId)
    }

    private fun configureViewModel()
    {
        viewModel.customersSaved.observe(this, Observer {
            it?.let { isSaved ->
                if (isSaved)
                    findNavController().navigateUp()
                else
                    view?.snackBar(getString(R.string.frag_add_edit_customer_error))
            }
        })
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
                textView.error = "Required field"
                view?.snackBar("You must enter all required fields")
                return@forEach
            }
        }

        if (canRegisterCustomer)
        {
            val customer = Customers(
                firstName = addEditCustomerFragFirstNameText.text.toString(),
                lastName = addEditCustomerFragLastNameText.text.toString(),
                siret = addEditCustomerFragSiretText.text.toString().toLong(),
                postCode = addEditCustomerFragPostCodeText.text.toString().toInt(),
                address = addEditCustomerFragAddressText.text.toString(),
                city = addEditCustomerFragCityText.text.toString(),
                email = addEditCustomerFragEmailText.text.toString(),
                mobilePhone = addEditCustomerFragMobilePhoneText.text.toString().toIntOrNull(),
                notes = addEditCustomerFragNotesText.text.toString(),
                phone = addEditCustomerFragPhoneText.text.toString().toIntOrNull()
            )

            viewModel.saveCustomer(customer)
        }
    }
}