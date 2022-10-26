package com.mina.janus.ui.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mina.janus.R
import com.mina.janus.models.UserLoginModel
import com.mina.janus.utilities.Constants
import com.mina.janus.utilities.Constants.KEY_IS_LOGIN_CLICKED
import com.mina.janus.utilities.Constants.KEY_USER_EMAIL
import com.mina.janus.utilities.Constants.KEY_USER_NAME
import com.mina.janus.utilities.Constants.isOnline
import com.mina.janus.utilities.Constants.showToast
import com.mina.janus.utilities.PreferenceManager
import com.mina.janus.viewmodles.ApiViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    //declare views

    private lateinit var buttonSignIn :Button
    private lateinit var progressBar: ProgressBar
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var textCreateNewAccount:TextView
    private lateinit var preferenceManager: PreferenceManager
    private val apiViewModel: ApiViewModel by viewModels()
    private var isLoginClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //handle enter & exit animation
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        if(savedInstanceState!=null){
            isLoginClicked = savedInstanceState.getBoolean(KEY_IS_LOGIN_CLICKED)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_LOGIN_CLICKED,isLoginClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //init views
        preferenceManager = PreferenceManager(requireActivity())
        textCreateNewAccount = view.findViewById(R.id.textCreateNewAccount)
        buttonSignIn =view.findViewById(R.id.buttonSignIn)
        inputEmail = view.findViewById(R.id.inputEmail)
        inputPassword = view.findViewById(R.id.inputPassword)
        progressBar = view.findViewById(R.id.progressBar)
        if(isLoginClicked){
            signIn()
        }
        setListeners()
    }

    private fun setListeners(){
        textCreateNewAccount.setOnClickListener{
            //navigate from login to signup
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        buttonSignIn.setOnClickListener{
            loading(true)
            if (isValidSignInDetails()) {
                signIn()
            }else{
                loading(false)
            }
        }
    }
    private fun isValidSignInDetails():Boolean
    {
        return if (inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter email",requireActivity())
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString())
                .matches()
        ) {
            showToast("Enter valid email",requireActivity())
            false
        } else if (inputPassword.text.toString().trim().isEmpty()) {
            showToast("Enter password",requireActivity())
            false
        } else {
            true
        }
    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            buttonSignIn.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
            buttonSignIn.visibility = View.VISIBLE
        }
    }

    private fun signIn(){

        if(isOnline(requireActivity())){
            isLoginClicked = true
            loading(true)


            apiViewModel.signIn(UserLoginModel(inputEmail.text.toString(),inputPassword.text.toString()))
            showToast("email:${inputEmail.text.toString()} password:${inputPassword.text.toString()}",requireContext())
            apiViewModel.codesLiveData.observe(requireActivity()) {
                when (it) {
                    200 -> {
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                        findNavController().navigate(R.id.action_loginFragment_to_mapsFragment)
                        loading(false)
                    }
                    422 -> {
                        showToast("email & password not correct",requireActivity())
                        loading(false)
                        isLoginClicked=false
                    }
                    401 -> {
                        showToast("password not correct",requireActivity())
                        loading(false)
                        isLoginClicked=false
                    }
                    else -> {
                        showToast("error $it",requireActivity())
                        loading(false)
                        isLoginClicked=false
                    }
                }
            }
            apiViewModel.errorMessageLiveData.observe(requireActivity()) {
                showToast(it,requireContext())
                loading(false)
            }
            apiViewModel.bodyLiveData.observe(requireActivity()){
                preferenceManager.putString(KEY_USER_NAME, it.name)
                preferenceManager.putString(KEY_USER_EMAIL,it.email)

            }
        }else{
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Error")
            builder.setMessage("check your internet connection and try again")
            builder.setCancelable(true)
           // builder.setIcon(R.drawable.ic_no_internet)
            builder.setPositiveButton("reload") { _, _ ->
                signIn()
                loading(false)
            }

            builder.setNegativeButton("exit") { _, _ ->
                requireActivity().finish()
                loading(false)
            }
            builder.setOnCancelListener { loading(false) }


            builder.show()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        apiViewModel.codesLiveData.removeObservers(requireActivity())
        apiViewModel.errorMessageLiveData.removeObservers(requireActivity())
        apiViewModel.bodyLiveData.removeObservers (requireActivity())
    }

}