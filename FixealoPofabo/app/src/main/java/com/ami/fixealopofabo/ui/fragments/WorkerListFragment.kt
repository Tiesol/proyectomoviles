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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentWorkerListBinding

import com.ami.fixealopofabo.model.Worker
import com.ami.fixealopofabo.ui.adapter.WorkerAdapter
import com.ami.fixealopofabo.viewmodel.WorkerListViewModel

class WorkerListFragment : Fragment(), WorkerAdapter.WorkerClickListener {

    private lateinit var binding: FragmentWorkerListBinding
    private val viewModel: WorkerListViewModel by viewModels()

    private lateinit var adapter: WorkerAdapter
    private var fullList: ArrayList<Worker> = arrayListOf()

    private var categoryId: Int = -1
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt("categoryId", -1)
            categoryName = it.getString("categoryName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchFilter()
        setupViewModelObservers()

        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty() || categoryId == -1) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.loadWorkers(token, categoryId)
        }
    }

    private fun setupRecyclerView() {
        adapter = WorkerAdapter()
        adapter.setOnWorkerClickListener(this)

        binding.rvPersonas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WorkerListFragment.adapter
        }
    }

    private fun setupSearchFilter() {
        binding.etFilterWorkers.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().trim()
            val filtered = if (query.isEmpty()) {
                fullList
            } else {
                fullList.filter {
                    "${it.name} ${it.lastName}".contains(query, ignoreCase = true)
                }
            }
            adapter.setData(ArrayList(filtered))
        }
    }

    private fun setupViewModelObservers() {
        viewModel.workerList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { list ->
                fullList = ArrayList(list)
                adapter.setData(fullList)
            }
            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    it.message ?: "Error al cargar trabajadores",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onWorkerClick(worker: Worker) {
        val bundle = Bundle().apply {
            putInt("workerId", worker.id)
            putInt("userId", worker.userId)
            putInt("categoryId", categoryId)
        }
        findNavController().navigate(
            R.id.action_workerListFragment_to_workerDetailFragment,
            bundle
        )
    }
}
