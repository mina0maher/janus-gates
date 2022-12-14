package com.mina.janus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mina.janus.R
import com.mina.janus.models.CarModelItem
import de.hdodenhof.circleimageview.CircleImageView

class CarsAdapter (private  val carsList:ArrayList<CarModelItem>,private val context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType== NORMAL_VIEW_TYPE){
            NormalViewHolder(
                LayoutInflater.from(parent.context)
                .inflate(R.layout.item_car, parent, false))
        }else{
            AddViewHolder(
                LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_car, parent, false))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)== NORMAL_VIEW_TYPE
            && holder is NormalViewHolder) {
            holder.setData(carsList[position],context)
        }
    }



    override fun getItemViewType(position: Int): Int {
        return if(position == carsList.size){
            ADD_VIEW_TYPE
        }else{
            NORMAL_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return carsList.size+1
    }


    class AddViewHolder(view: View): RecyclerView.ViewHolder(view){
       // private val addLayout :LinearLayout = view.findViewById(R.id.add_layout)
    }
    class NormalViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val layout : ConstraintLayout = itemView.findViewById(R.id.item_car_layout)
        private val carImage:CircleImageView = view.findViewById(R.id.carImage)
        private val carName:TextView = view.findViewById(R.id.text_car_name)
        private val carNumber:TextView = view.findViewById(R.id.text_car_number)
        private val carModel:TextView = view.findViewById(R.id.text_car_model)
        private val carCheck:ImageView = view.findViewById(R.id.gate_check)
        fun setData(item:CarModelItem,context : Context){
            Glide.with(View(context)).load(item.type!!.imageUrl).into(carImage)
            carName.text = item.name
            carNumber.text = item.licensePlate
            carModel.text=item.type.name
            if(item.isChecked){
                carCheck.visibility= View.VISIBLE
                layout.setBackgroundResource(R.drawable.item_gate_background)
            }
            layout.setOnClickListener{
                if(!item.isChecked){
                    carCheck.visibility= View.VISIBLE
                    layout.setBackgroundResource(R.drawable.item_gate_background)
                    item.isChecked=true
                }else{
                    carCheck.visibility= View.INVISIBLE
                    layout.setBackgroundResource(R.drawable.item_normal_background)
                    item.isChecked=false
                }
            }
        }
    }

    companion object {
        private const val NORMAL_VIEW_TYPE = 2
        private const val ADD_VIEW_TYPE = 1
    }

}