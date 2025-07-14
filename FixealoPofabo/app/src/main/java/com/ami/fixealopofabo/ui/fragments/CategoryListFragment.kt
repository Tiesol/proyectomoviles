package com.ami.fixealopofabo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ami.fixealopofabo.model.Category
import com.ami.fixealopofabo.ui.adapter.CategoryAdapter
import com.ami.fixealopofabo.viewmodel.CategoryListViewModel
import androidx.core.content.edit
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryListViewModel by viewModels()

    private lateinit var adapter: CategoryAdapter
    private var fullList: ArrayList<Category> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchFilter()
        setupViewModelObservers()
        setupLogoutButton()

        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_categoryListFragment_to_loginFragment)
        } else {
            viewModel.loadCategories(token)
        }

        binding.btnAppointments.setOnClickListener {
            findNavController().navigate(R.id.action_categoryListFragment_to_myAppointmentsFragment)
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            requireActivity()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .edit {
                    remove("token")
                }

            val navOpts = NavOptions.Builder()
                .setPopUpTo(R.id.categoryListFragment, /*inclusive=*/true)
                .build()

            findNavController().navigate(
                R.id.loginFragment,
                null,
                navOpts
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter()
        adapter.setOnCategoryClickListener(this)

        binding.rvCategorias.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@CategoryListFragment.adapter
        }
    }

    private fun setupSearchFilter() {
        binding.etFilterCategory.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().trim()
            val filtered = if (query.isEmpty()) {
                fullList
            } else {
                fullList.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.setData(ArrayList(filtered))
        }
    }

    private fun setupViewModelObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { list ->
                fullList = ArrayList(list)
                adapter.setData(fullList)
            }
            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    it.message ?: "Error al obtener categorías",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCategoryClick(category: Category) {
        val bundle = Bundle().apply {
            putInt("categoryId", category.id)
            putString("categoryName", category.name)
        }
        findNavController().navigate(
            R.id.action_categoryListFragment_to_workerListFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
