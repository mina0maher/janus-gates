package com.mina.janus.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mina.janus.R
import com.mina.janus.adapters.CarsAdapter
import com.mina.janus.adapters.GatesAdapter
import com.mina.janus.listeners.CarsListener
import com.mina.janus.listeners.GatesListener
import com.mina.janus.models.CarModelItem
import com.mina.janus.models.GatesAlongRoute
import com.mina.janus.models.GatesModel
import com.mina.janus.models.TicketPostModel
import com.mina.janus.utilities.Constants
import com.mina.janus.utilities.Constants.showToast
import com.mina.janus.utilities.PreferenceManager
import com.mina.janus.viewmodles.ApiViewModel

class ReservationFragment : Fragment(),CarsListener,GatesListener {
private var array :IntArray?=null
    private val apiViewModel: ApiViewModel by viewModels()
    private var gatesModel:GatesModel? = null
    private lateinit var gatesRecyclerView: RecyclerView
    private lateinit var gatesProgressPar:ProgressBar
    private lateinit var carsRecyclerView: RecyclerView
    private lateinit var carsProgressPar:ProgressBar
    private lateinit var payNowButton:Button
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var totalTextView: TextView
    private lateinit var gatesAdapter: GatesAdapter
    private lateinit var carsAdapter: CarsAdapter
    private var checkedCar:CarModelItem?=null
    private var checkedGates:GatesModel = GatesModel()
    private var ticketText:String = ""
    private lateinit var dialog: Dialog


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
        arguments?.let { array = it.getIntArray("gatesID") }
        dialog = Dialog(requireContext())
        apiViewModel.getAllGates()
        gatesLoading(true)
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
        preferenceManager.getString(Constants.KEY_JSESSOIONID)?.let { apiViewModel.getCars(it) }
        apiViewModel.carBodyLiveData.observe(requireActivity()){
            carsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            carsAdapter= CarsAdapter(it,requireContext(),this)
            carsRecyclerView.adapter =carsAdapter
            carsLoading(false)
        }
apiViewModel.codesLiveData.observe(requireActivity()){
    showToast(it.toString(),requireContext())
}
        apiViewModel.errorMessageLiveData.observe(requireActivity()){
            showToast(it,requireContext())
        }


        payNowButton.setOnClickListener{
            if(checkedCar != null && checkedGates.isNotEmpty()){
                for(gate in checkedGates){
                    apiViewModel.reserveTicket(preferenceManager.getString(Constants.KEY_JSESSOIONID)!!,getTicketInfo(gate,checkedCar!!))
                }
            }
        }
        apiViewModel.ticketBodyLiveData.observe(requireActivity()){
            ticketText+= "1 * ${it.gate!!.name} = ${it.paidPrice} \n"
            openConfirmationDialog(ticketText)
        }



        return view
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
            totalTextView.text = "Total : 0 EGP "
        }
    }

    override fun onGateClicked(checkedGates: GatesModel) {
        this.checkedGates=checkedGates
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
        val button: Button = dialog.findViewById(R.id.buttonContact)
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
}