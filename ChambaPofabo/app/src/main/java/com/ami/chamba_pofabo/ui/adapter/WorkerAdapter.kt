package com.ami.chamba_pofabo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.chamba_pofabo.R
import com.ami.chamba_pofabo.databinding.ItemWorkerBinding
import com.ami.chamba_pofabo.model.Worker
import com.bumptech.glide.Glide

class WorkerAdapter(
    private var workers: ArrayList<Worker> = arrayListOf()
) : RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder>() {

    private var listener: WorkerClickListener? = null

    fun setOnWorkerClickListener(listener: WorkerClickListener) {
        this.listener = listener
    }

    fun setData(newWorkers: ArrayList<Worker>) {
        workers = newWorkers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val binding = ItemWorkerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        holder.bind(workers[position], listener)
    }

    override fun getItemCount(): Int = workers.size

    class WorkerViewHolder(private val binding: ItemWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Worker, listener: WorkerClickListener?) = with(binding) {
            tvNombrePersona.text = "${item.name} ${item.lastName}"
            tvCalificacion.text  = "★ %.1f".format(item.rating)
            val context = root.context
            Glide.with(context)
                .load(item.profilePicture)
                .placeholder(R.drawable.user_default)
                .error(R.drawable.user_default)
                .into(imgPerfilWorker)
            root.setOnClickListener { listener?.onWorkerClick(item) }
        }
    }

    interface WorkerClickListener {
        fun onWorkerClick(worker: Worker)
    }
}
