package com.ami.fixealopofabo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.fixealopofabo.databinding.ItemAppointmentBinding
import com.ami.fixealopofabo.model.Appointment

class AppointmentAdapter(
    private var appointments: ArrayList<Appointment> = arrayListOf()
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    private var listener: AppointmentClickListener? = null

    fun setData(newList: ArrayList<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }

    fun setOnAppointmentClickListener(listener: AppointmentClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = appointments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position], listener)
    }

    class ViewHolder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Appointment, listener: AppointmentClickListener?) = with(binding) {
            tvWorkerName.text = item.workerName
            tvCategory.text   = item.categoryName
            tvDateTime.text   = item.dateTime
            tvStatus.text     = item.status.uppercase()

            root.setOnClickListener { listener?.onAppointmentClick(item) }
        }
    }

    interface AppointmentClickListener {
        fun onAppointmentClick(appointment: Appointment)
    }
}