package com.ami.fixealopofabo.ui.fragments
import android.content.Context
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
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentMyAppointmentsBinding

import com.ami.fixealopofabo.model.Appointment
import com.ami.fixealopofabo.ui.adapter.AppointmentAdapter
import com.ami.fixealopofabo.viewmodel.MyAppointmentsViewModel

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

        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
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

        val bundle = Bundle().apply {
            putInt("appointmentId", appointment.id)
            putInt("userId", -1)
            putInt("workerId", -1)
            putString("workerName", appointment.workerName)
            putString("workerPhoto", appointment.workerPhoto ?: "")
            putInt("workerUserId", appointment.workerUserId)
            putInt("appointmentStatus", appointment.statusCode)
        }

        Log.d("MyAppointmentsFragment", "Chat con workerUserId=${appointment.workerUserId}, status=${appointment.statusCode}")

        findNavController().navigate(R.id.action_myAppointmentsFragment_to_chatFragment, bundle)
    }
}