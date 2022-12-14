package com.mina.janus.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.mina.janus.models.UserRegisterModel
import com.mina.janus.utilities.Constants
import com.mina.janus.utilities.Constants.KEY_IS_SIGNUP_CLICKED
import com.mina.janus.utilities.Constants.isOnline
import com.mina.janus.utilities.Constants.showToast
import com.mina.janus.utilities.PreferenceManager
import com.mina.janus.viewmodles.ApiViewModel


class SignupFragment : Fragment(R.layout.fragment_signup) {
    //declare views
    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var buttonSignUp:Button
    private lateinit var textSignIn: TextView
    private lateinit var progressBar: ProgressBar
    //
    private lateinit var preferenceManager: PreferenceManager
    private val apiViewModel: ApiViewModel by viewModels()
    //vars
    private var isSignUpClicked = false
    private lateinit var dialog:Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //handle enter & exit animation
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        //save state
        if(savedInstanceState!=null){
            isSignUpClicked = savedInstanceState.getBoolean(KEY_IS_SIGNUP_CLICKED)
        }
        dialog = Dialog(requireContext())


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init views
        preferenceManager = PreferenceManager(requireActivity())
        inputName = view.findViewById(R.id.inputName)
        inputEmail = view.findViewById(R.id.inputEmail)
        inputPassword = view.findViewById(R.id.inputPassword)
        inputConfirmPassword = view.findViewById(R.id.inputConfirmPassword)
        buttonSignUp = view.findViewById(R.id.buttonSignUp)
        textSignIn = view.findViewById(R.id.textSignIn)
        progressBar =view.findViewById(R.id.progressBar)

        setListeners()

        if(isSignUpClicked){
            signUp()
        }

    }


    private fun setListeners(){
        textSignIn.setOnClickListener{
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        buttonSignUp.setOnClickListener {
            loading(true)
            if (isValidSignUpDetails()) {
                if(isOnline(requireContext())) {
                    signUp()
                }else{
                    loading(false)
                    dialog.setContentView(R.layout.no_internet_for_buttons)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val textView = dialog.findViewById<TextView>(R.id.textDismiss)
                    val button = dialog.findViewById<Button>(R.id.buttonContact)
                    textView.visibility = View.GONE
                    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
                    val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
                    button.setOnClickListener { dialog.dismiss() }
                    dialog.setCancelable(true)
                    dialog.window!!.setLayout(width, height)
                    dialog.show()
                }
            }else{
                loading(false)
            }
        }
    }

    private fun signUp(){

            isSignUpClicked = true
            loading(true)


            apiViewModel.signUp(UserRegisterModel(inputEmail.text.toString(),inputName.text.toString(),inputPassword.text.toString()))
            apiViewModel.codesLiveData.observe(requireActivity()) {
                when (it) {
                    200 -> {
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setTitle("Successful")
                        builder.setMessage("check your email inbox to confirm registration")
                        builder.setCancelable(true)
                        builder.setPositiveButton("cancel") { _, _ -> }
                        builder.show()
                        findNavController().navigate(R.id.action_signupFragment_to_mapsFragment)
                    }
                    422 -> {
                        showToast("email & password not correct",requireActivity())
                        isSignUpClicked=false
                    }
                    401 -> {
                        showToast("password not correct",requireActivity())
                        isSignUpClicked=false
                    }
                    else -> {
                        showToast("error $it",requireActivity())
                        isSignUpClicked=false
                    }
                }
                loading(false)
            }
        apiViewModel.jsessionidLiveData.observe(requireActivity()){
            preferenceManager.putString(Constants.KEY_JSESSOIONID,it)
        }
            apiViewModel.errorMessageLiveData.observe(requireActivity()) {
                showToast(it,requireContext())
                loading(false)
            }

            apiViewModel.bodyLiveData.observe(requireActivity()){
                preferenceManager.putString(Constants.KEY_USER_NAME, it.name)
                preferenceManager.putString(Constants.KEY_USER_EMAIL,it.email)
            }

    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            buttonSignUp.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
            buttonSignUp.visibility = View.VISIBLE
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        return if (inputName.text.toString().trim().isEmpty()) {
            showToast("Enter name",requireContext())
            false
        } else if (inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter email",requireContext())
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString())
                .matches()
        ) {
            showToast("Enter valid email",requireContext())
            false
        } else if (inputPassword.text.toString().trim().isEmpty()) {
            showToast("Enter Password",requireContext())
            false
        } else if (inputConfirmPassword.text.toString().trim().isEmpty()) {
            showToast("Confirm your Password",requireContext())
            false
        } else if (inputPassword.text.toString() != inputConfirmPassword.text.toString()) {
            showToast("Password & confirm password must be same",requireContext())
            false
        } else {
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_SIGNUP_CLICKED,isSignUpClicked)
    }

    override fun onDestroy() {
        super.onDestroy()
        apiViewModel.codesLiveData.removeObservers(requireActivity())
        apiViewModel.errorMessageLiveData.removeObservers(requireActivity())
        apiViewModel.bodyLiveData.removeObservers (requireActivity())
    }

}