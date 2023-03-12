package com.mina.janus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mina.janus.R
import com.mina.janus.listeners.CarsListener
import com.mina.janus.listeners.GatesListener
import com.mina.janus.models.GatesAlongRoute
import com.mina.janus.models.GatesModel
import de.hdodenhof.circleimageview.CircleImageView

class GatesAdapter(val list:ArrayList<GatesAlongRoute>,private val context: Context, private val gatesListener: GatesListener):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        GatesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gate,parent,false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GatesViewHolder){
            holder.setData(list[position],context,gatesListener)
        }
    }



    override fun getItemCount(): Int {
        return list.size
    }
    class GatesViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val layout : ConstraintLayout = itemView.findViewById(R.id.item_gate_layout)
        private val gateName :TextView = itemView.findViewById(R.id.gate_name)
        private val gateInfo:TextView = itemView.findViewById(R.id.gate_info)
        private val gateImage:CircleImageView = itemView.findViewById(R.id.gate_image)
        private val gateCheck:ImageView = itemView.findViewById(R.id.gate_check)
        fun setData(gate:GatesAlongRoute,context : Context,gatesListener: GatesListener){
            gateName.text = gate.name
            gateInfo.text=gate.address
            if(gate.isChecked){
                checkedGates.add(gate)
                gateCheck.visibility= View.VISIBLE
                layout.setBackgroundResource(R.drawable.item_gate_background)
            }else{
                gateCheck.visibility= View.INVISIBLE
                layout.setBackgroundResource(R.drawable.item_normal_background)
            }
            Glide.with(View(context)).load(gate.imageUrl).into(gateImage)
            layout.setOnClickListener{
                if(!gate.isChecked){
                    gateCheck.visibility= View.VISIBLE
                    layout.setBackgroundResource(R.drawable.item_gate_background)
                    gate.isChecked=true
                    checkedGates.add(gate)
                }else{
                    gateCheck.visibility= View.INVISIBLE
                    layout.setBackgroundResource(R.drawable.item_normal_background)
                    gate.isChecked=false
                    checkedGates.remove(gate)
                }
                gatesListener.onGateClicked(checkedGates)
            }
        }
    }
    companion object{
        val checkedGates = GatesModel()
    }
}