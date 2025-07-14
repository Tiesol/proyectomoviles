package com.ami.chamba_pofabo.ui.fragments

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ami.chamba_pofabo.R
import com.ami.chamba_pofabo.databinding.FragmentMyAppointmentsBinding
import com.ami.chamba_pofabo.model.Appointment
import com.ami.chamba_pofabo.ui.adapter.AppointmentAdapter
import com.ami.chamba_pofabo.viewmodel.MyAppointmentsViewModel

class MyAppointmentsFragment : Fragment(), AppointmentAdapter.AppointmentClickListener {

    private lateinit var binding: FragmentMyAppointmentsBinding
    private val viewModel: MyAppointmentsViewModel by viewModels()
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AppointmentAdapter().apply {
            setOnAppointmentClickListener(this@MyAppointmentsFragment)
        }

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MyAppointmentsFragment.adapter
        }

        binding.imgChambeador.setOnClickListener {
            findNavController().navigate(R.id.action_myAppointmentsFragment_to_categoryListFragment)
        }

        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        viewModel.loadProfileImage(token)

        viewModel.profileImageUrl.observe(viewLifecycleOwner) { result ->
            result.onSuccess { url ->
                if (url.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(url)
                        .placeholder(R.drawable.user_default)
                        .error(R.drawable.user_default)
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(binding.imgChambeador)
                } else {
                    binding.imgChambeador.setImageResource(R.drawable.user_default)
                }
            }
            result.onFailure {
                binding.imgChambeador.setImageResource(R.drawable.user_default)
                Log.e("MyAppointmentsFragment", "Error al cargar imagen: ${it.message}")
            }
        }

        viewModel.loadAppointments(token)

        viewModel.appointments.observe(viewLifecycleOwner) { result ->
            result.onSuccess { list ->
                adapter.setData(ArrayList(list))
            }
            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    it.message ?: "Error cargando citas",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onAppointmentClick(appointment: Appointment) {
        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        val parts = appointment.dateTime.split(" ")
        val date = parts.getOrNull(0) ?: ""
        val time = parts.getOrNull(1) ?: ""

        Log.d("MyAppointmentsFragment", "appointment.categoryId: ${appointment.categoryId}")

        val bundle = Bundle().apply {
            putInt("appointmentId", appointment.id)
            putInt("userId", appointment.userId)
            putInt("workerId", appointment.workerId)
            putString("workerName", appointment.clientName)
            putString("appointmentDate", date)
            putString("appointmentTime", time)
            putInt("appointmentStatus", appointment.statusCode)
            putString("latitude", appointment.latitude)
            putString("longitude", appointment.longitude)
            putInt("categoryId", appointment.categoryId)
        }

        findNavController().navigate(R.id.action_myAppointmentsFragment_to_chatFragment, bundle)
    }

}
