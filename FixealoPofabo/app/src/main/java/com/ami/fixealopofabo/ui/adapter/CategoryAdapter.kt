package com.ami.fixealopofabo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.fixealopofabo.databinding.ItemCategoriaBinding
import com.ami.fixealopofabo.model.Category

class CategoryAdapter(
    private var categories: ArrayList<Category> = arrayListOf()
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var listener: CategoryClickListener? = null

    fun setData(newCategories: ArrayList<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    fun setOnCategoryClickListener(listener: CategoryClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], listener)
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category, listener: CategoryClickListener?) {
            binding.tvNombreCategoria.text = item.name
            binding.root.setOnClickListener { listener?.onCategoryClick(item) }
        }
    }

    interface CategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}
