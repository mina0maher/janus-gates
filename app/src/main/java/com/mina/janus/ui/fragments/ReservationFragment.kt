package com.mina.janus.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mina.janus.R
import com.mina.janus.adapters.CarsAdapter
import com.mina.janus.adapters.GatesAdapter
import com.mina.janus.listeners.CarsListener
import com.mina.janus.listeners.GatesListener
import com.mina.janus.models.CarModelItem
import com.mina.janus.models.GatesModel
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
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var totalTextView: TextView
    private lateinit var gatesAdapter: GatesAdapter
    private lateinit var carsAdapter: CarsAdapter
    private var checkedCar:CarModelItem?=null
    private var checkedGates:GatesModel = GatesModel()
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
        arguments?.let { array = it.getIntArray("gatesID") }
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


        return view
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
}