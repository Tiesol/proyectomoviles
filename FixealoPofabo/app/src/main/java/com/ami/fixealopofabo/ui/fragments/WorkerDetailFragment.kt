package com.ami.fixealopofabo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentWorkerDetailBinding

import com.ami.fixealopofabo.ui.adapters.ReviewsAdapter
import com.ami.fixealopofabo.viewmodel.WorkerDetailViewModel
import com.bumptech.glide.Glide

class WorkerDetailFragment : Fragment() {

    private lateinit var binding: FragmentWorkerDetailBinding
    private val viewModel: WorkerDetailViewModel by viewModels()
    private lateinit var reviewsAdapter: ReviewsAdapter

    private var workerId: Int = -1
    private var categoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workerId = it.getInt("workerId", -1)
            categoryId = it.getInt("categoryId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty() || workerId == -1) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        setupRecyclerView()

        viewModel.loadWorker(token, workerId)
        viewModel.loadWorkerReviews(token, workerId)

        viewModel.worker.observe(viewLifecycleOwner) { result ->
            result.onSuccess { worker ->
                Log.d("WorkerDetail", "Trabajador cargado: ${worker.name} ${worker.lastName}")
                binding.tvNombre.text = "${worker.name} ${worker.lastName}"
                binding.tvCalificacion.text = "★ %.1f".format(worker.rating)

                Glide.with(this)
                    .load(worker.profilePicture)
                    .placeholder(R.drawable.user_default)
                    .error(R.drawable.user_default)
                    .into(binding.imgChambeador)

                binding.btnContactar.setOnClickListener {
                    binding.btnContactar.isEnabled = false
                    viewModel.createAppointment(token, workerId, categoryId)
                }
            }

            result.onFailure {
                Toast.makeText(
                    context,
                    it.message ?: "Error cargando detalle",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.reviews.observe(viewLifecycleOwner) { result ->
            result.onSuccess { reviews ->
                Log.d("WorkerDetail", "Reseñas cargadas: ${reviews.size}")
                reviewsAdapter.updateReviews(reviews)

                if (reviews.isEmpty()) {
                    binding.rvResenas.visibility = View.GONE
                } else {
                    binding.rvResenas.visibility = View.VISIBLE
                }
            }

            result.onFailure {
                Log.e("WorkerDetail", "Error cargando reseñas: ${it.message}")
                binding.rvResenas.visibility = View.GONE
            }
        }

        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            binding.btnContactar.isEnabled = true

            result.onSuccess { appointmentId ->
                val worker = viewModel.worker.value?.getOrNull()
                if (worker != null) {
                    Log.d("WorkerDetail", "Cita creada: $appointmentId con receiverUserId: ${worker.userId}")
                    Toast.makeText(requireContext(), "Cita creada (#$appointmentId)", Toast.LENGTH_SHORT).show()

                    val bundle = Bundle().apply {
                        putInt("appointmentId", appointmentId)
                        putInt("userId", worker.userId)
                        putInt("receiverUserId", worker.userId)
                        putInt("workerId", worker.id)
                        putString("workerName", "${worker.name} ${worker.lastName}")
                        putString("workerPhoto", worker.profilePicture ?: "")
                    }

                    findNavController().navigate(
                        R.id.action_workerDetailFragment_to_chatFragment,
                        bundle
                    )
                }
            }

            result.onFailure {
                Toast.makeText(context, "Error al contactar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        reviewsAdapter = ReviewsAdapter(emptyList())
        binding.rvResenas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewsAdapter
        }
    }
}