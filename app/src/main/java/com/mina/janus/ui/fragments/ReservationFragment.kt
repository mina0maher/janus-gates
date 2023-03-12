package com.mina.janus.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.mina.janus.R
import com.mina.janus.adapters.AddCarAdapter
import com.mina.janus.adapters.CarsAdapter
import com.mina.janus.adapters.GatesAdapter
import com.mina.janus.adapters.GatesAdapter.Companion.checkedGates
import com.mina.janus.listeners.CarsListener
import com.mina.janus.listeners.GatesListener
import com.mina.janus.models.*
import com.mina.janus.utilities.Constants.KEY_JSESSOIONID
import com.mina.janus.utilities.Constants.isOnline
import com.mina.janus.utilities.Constants.showToast
import com.mina.janus.utilities.PreferenceManager
import com.mina.janus.viewmodles.ApiViewModel

class ReservationFragment : Fragment(),CarsListener,GatesListener {
private var array :IntArray?=null
    private val apiViewModel: ApiViewModel by viewModels()
    private var gatesModel:GatesModel? = null
    private var carsModel:CarModel?=null
    private lateinit var gatesRecyclerView: RecyclerView
    private lateinit var gatesProgressPar:ProgressBar
    private lateinit var carsRecyclerView: RecyclerView
    private lateinit var carsProgressPar:ProgressBar
    private lateinit var payNowProgressBar:ProgressBar
    private lateinit var payNowButton:Button
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var totalTextView: TextView
    private lateinit var gatesAdapter: GatesAdapter
    private lateinit var carsAdapter: CarsAdapter
    private var checkedCar:CarModelItem?=null
    private var ticketText:String = ""
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var dialog: Dialog

    private lateinit var addCarButtonProgressBar:ProgressBar
    private lateinit var addCarButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservation, container, false)
        preferenceManager = PreferenceManager(requireActivity())
        gatesRecyclerView = view.findViewById(R.id.recycler_gates)
        gatesProgressPar = view.findViewById(R.id.gates_progress_par)
        carsRecyclerView = view.findViewById(R.id.recycler_cars)
        carsProgressPar = view.findViewById(R.id.cars_progress_par)
        totalTextView = view.findViewById(R.id.text_total)
        payNowButton = view.findViewById(R.id.button_pay)
        payNowProgressBar=view.findViewById(R.id.payNowProgressBar)
        arguments?.let { array = it.getIntArray("gatesID") }
        dialog = Dialog(requireContext())


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isOnline(requireContext())) {
            apiViewModel.getAllGates()
        }else{
            showOfflineDialog("getAllGates")
        }
        gatesLoading(true)
        if(isOnline(requireContext())) {
            preferenceManager.getString(KEY_JSESSOIONID)?.let { apiViewModel.getCars(it) }
        }else{
            showOfflineDialog("getCars")
        }

        payNowButton.setOnClickListener{
            if(isOnline(requireContext())) {
                payNowLoading(true)
                if (checkedCar != null && checkedGates.isNotEmpty()) {
                    for (gate in checkedGates) {
                        apiViewModel.reserveTicket(
                            preferenceManager.getString(KEY_JSESSOIONID)!!,
                            getTicketInfo(gate, checkedCar!!)
                        )
                    }
                }
            }else{
                showOfflineDialog("noInternet")
            }
        }


        apiViewModel.gatesBodyLiveData.observe(requireActivity()){
            gatesModel = it
            if(array!=null) {
                for (i in array!!) {
                    for ((k, j) in it.withIndex()) {
                        if (i == j.id) {
                            gatesModel!![k].isChecked = true
                        }
                    }
                }
            }
            gatesRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            gatesAdapter = GatesAdapter(gatesModel!!,requireContext(),this)
            gatesRecyclerView.adapter = gatesAdapter
            gatesLoading(false)
        }
        apiViewModel.ticketBodyLiveData.observe(requireActivity()){
            payNowLoading(false)
            ticketText+= "1 * ${it.gate!!.name} = ${it.paidPrice} \n"
            openConfirmationDialog(ticketText)
        }
        apiViewModel.carsBodyLiveData.observe(requireActivity()){
            carsModel=it
            carsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            carsAdapter= CarsAdapter(carsModel!!,requireContext(),this)
            carsRecyclerView.adapter =carsAdapter
            carsLoading(false)
        }
        apiViewModel.codesLiveData.observe(requireActivity()){
            showToast(it.toString(),requireContext())
        }
        apiViewModel.errorMessageLiveData.observe(requireActivity()){
            showToast(it,requireContext())


                showToast(it,requireContext())
                if(it.startsWith("getAllGates")){
                    showOfflineDialog("getAllGates")
                }else if(it.startsWith("getCars")){
                        showOfflineDialog("getAllGates")
                }else{
                    showOfflineDialog(it)
                }

        }
        apiViewModel.carBodyLiveData.observe(requireActivity()){
            carsModel!!.add(it)
            carsAdapter.notifyItemChanged(carsModel!!.size-1)
            dialog.setContentView(R.layout.confirmation_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val textView: TextView = dialog.findViewById(R.id.textDismiss)
            val ticketsText: TextView = dialog.findViewById(R.id.ticket_text)
            val successText: TextView = dialog.findViewById(R.id.title)
            successText.text = "Success"
            textView.visibility=View.GONE
            ticketsText.visibility=View.GONE
            val button: Button = dialog.findViewById(R.id.buttonSeeGates)
            button.text="dismiss"
            textView.setOnClickListener { dialog.dismiss() }
            button.setOnClickListener {
                dialog.dismiss()
            }
            val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
            dialog.window!!.setLayout(width, height)
            dialog.setCancelable(true)
            dialog.show()
        }

    }
    private fun getTicketInfo(gate:GatesAlongRoute,car:CarModelItem):TicketPostModel{
        return TicketPostModel(gate.name,car.licensePlate,car.type!!.name)
    }

    private fun gatesLoading(isLoading:Boolean){
        if(isLoading){
            gatesRecyclerView.visibility=View.INVISIBLE
            gatesProgressPar.visibility=View.VISIBLE
        }else{
            gatesRecyclerView.visibility=View.VISIBLE
            gatesProgressPar.visibility=View.INVISIBLE
        }
    }
    private fun payNowLoading(isLoading: Boolean){
        if(isLoading){
            payNowButton.visibility=View.INVISIBLE
            payNowProgressBar.visibility=View.VISIBLE
        }else{
            payNowButton.visibility=View.VISIBLE
            payNowProgressBar.visibility=View.INVISIBLE
        }
    }
    private fun carsLoading(isLoading:Boolean){
        if(isLoading){
            carsRecyclerView.visibility=View.INVISIBLE
            carsProgressPar.visibility=View.VISIBLE
        }else{
            carsRecyclerView.visibility=View.VISIBLE
            carsProgressPar.visibility=View.INVISIBLE
        }
    }

    override fun onCarClicked(lastCheckedPosition: Int?,checkedCar:CarModelItem?) {
        lastCheckedPosition?.let { carsAdapter.deselectItem(it) }
        this.checkedCar = checkedCar
        if(checkedCar!=null&&checkedGates.isNotEmpty()){
            totalTextView.text = "Total : ${getTolls(checkedGates,checkedCar)} EGP "
        }else{
            totalTextView.text = "Total : 0 EGP"
        }
    }

    override fun onAddCarClicked() {
        dialog.setContentView(R.layout.add_car_dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        autoCompleteTextView=dialog.findViewById(R.id.autoCompleteTextView)
        val vehicleTypes=ArrayList<VehicleType>()
        for(price in gatesModel?.get(0)?.prices!!){
            vehicleTypes.add(price.vehicleType!!)
        }
        val addCarDropdownAdapter= AddCarAdapter(requireContext(),vehicleTypes)
        addCarDropdownAdapter.setDropDownViewResource(R.layout.addcar_dropdown_item)
        autoCompleteTextView.setAdapter(addCarDropdownAdapter)
        val licensePlateNumber: TextInputEditText = dialog.findViewById(R.id.licensePlateNumber)
        val carModelName:TextInputEditText = dialog.findViewById(R.id.carModelName)
        val carNameName:TextInputEditText = dialog.findViewById(R.id.carNameName)
         addCarButtonProgressBar = dialog.findViewById(R.id.buttonProgressBar);
         addCarButton = dialog.findViewById(R.id.buttonSeeGates)
        val textView: TextView = dialog.findViewById(R.id.textDismiss)

        textView.setOnClickListener { dialog.dismiss() }
        addCarButton.text = "ADD CAR"
        addCarButton.setOnClickListener {
            if (autoCompleteTextView.text.isNotEmpty()&&
                    licensePlateNumber.text!!.isNotEmpty()&&
                    carModelName.text!!.isNotEmpty()&&
                        carNameName.text!!.isNotEmpty()
                    ){
                if(isOnline(requireContext())){
                    apiViewModel.addCar(preferenceManager.getString(KEY_JSESSOIONID)!!,
                        CarPostModel(licensePlateNumber.text.toString(),
                            carModelName.text.toString(),
                            carNameName.text.toString(),
                            autoCompleteTextView.text.toString())

                    )
                    addCarButton.visibility=View.INVISIBLE
                    addCarButtonProgressBar.visibility=View.VISIBLE

                }else{
                    showOfflineDialog("noInternet");
                }
            }else{
                showToast("please complete you car's data",requireContext())
            }

        }
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        dialog.window!!.setLayout(width, height)
        dialog.setCancelable(true)
        dialog.show()

    }

    override fun onGateClicked(checkedGates: GatesModel) {

        if(checkedCar!=null&&checkedGates.isNotEmpty()){
            totalTextView.text = "Total : ${getTolls(checkedGates,checkedCar)} EGP "
        }else{
            totalTextView.text = "Total : 0 EGP "
        }
    }
    private fun getTolls(checkedGates:GatesModel, checkedCar:CarModelItem?):Double{
      var tolls = 0.0
        for (gate in checkedGates){
            for (price in gate.prices!!){
                if (price.vehicleType!!.id == checkedCar!!.type!!.id){
                    tolls += price.price!!
                }
            }
        }
        return tolls
    }
    private fun openConfirmationDialog(text:String) {
        dialog.setContentView(R.layout.confirmation_dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val textView: TextView = dialog.findViewById(R.id.textDismiss)
        val ticketsText: TextView = dialog.findViewById(R.id.ticket_text)
        ticketsText.text=text
        val button: Button = dialog.findViewById(R.id.buttonSeeGates)
        textView.setOnClickListener { dialog.dismiss() }
        button.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack()
        }
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        dialog.window!!.setLayout(width, height)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showOfflineDialog(errorName:String){
        dialog.setContentView(R.layout.no_internet_for_buttons)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val textView = dialog.findViewById<TextView>(R.id.textDismiss)
        val button = dialog.findViewById<Button>(R.id.buttonSeeGates)
        payNowLoading(false)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        when (errorName) {
            "getAllGates" -> {
                textView.visibility = View.VISIBLE
            }
            "getCars" -> {
                textView.visibility = View.VISIBLE
            }
            else -> {
                textView.visibility = View.GONE
            }
        }
        textView.setOnClickListener{
            findNavController().popBackStack()
        }
        button.setOnClickListener {
            when (errorName) {
                "getAllGates" -> {
                    apiViewModel.getAllGates()
                }
                "getCars" -> {
                    preferenceManager.getString(KEY_JSESSOIONID)?.let { apiViewModel.getCars(it) }
                }
                else -> {
                    dialog.dismiss()
                }
            }
        }
        dialog.setCancelable(false)
        dialog.window!!.setLayout(width, height)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        apiViewModel.errorMessageLiveData.removeObservers(requireActivity())
        apiViewModel.gatesBodyLiveData.removeObservers(requireActivity())
        apiViewModel.ticketBodyLiveData.removeObservers(requireActivity())
        apiViewModel.carsBodyLiveData.removeObservers(requireActivity())
        apiViewModel.codesLiveData.removeObservers(requireActivity())


    }
}