package com.ami.chamba_pofabo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.chamba_pofabo.databinding.ItemCategoriaBinding
import com.ami.chamba_pofabo.model.Category

class CategoryAdapter(
    private var categories: ArrayList<Category> = arrayListOf()
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var listener: CategoryClickListener? = null
    private val checkedStates = mutableMapOf<Int, Boolean>()

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
    fun updateCheckState(categoryId: Int, isChecked: Boolean) {
        checkedStates[categoryId] = isChecked
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        val isChecked = checkedStates[item.id] ?: false
        holder.bind(item, isChecked, listener)
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category, isChecked: Boolean, listener: CategoryClickListener?) {
            binding.cbCategoria.text = item.name

            binding.cbCategoria.setOnCheckedChangeListener(null)

            binding.cbCategoria.isChecked = isChecked

            binding.cbCategoria.setOnCheckedChangeListener { _, checked ->
                listener?.onCategoryClick(item, checked)
            }
        }
    }

    interface CategoryClickListener {
        fun onCategoryClick(category: Category, isChecked: Boolean)
    }
}