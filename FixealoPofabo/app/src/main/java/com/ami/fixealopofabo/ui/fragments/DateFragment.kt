package com.ami.fixealopofabo.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentDateBinding

import com.ami.fixealopofabo.viewmodel.AppointmentViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateFragment : Fragment() {

    private var _binding: FragmentDateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentViewModel by viewModels()

    private var appointmentId: Int = 0
    private var latitude: String = ""
    private var longitude: String = ""
    private var token: String = ""
    private var selectedDate: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            appointmentId = it.getInt("appointmentId", 0)
            latitude = it.getString("latitude", "")
            longitude = it.getString("longitude", "")
            token = it.getString("token", "")
        }

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no recibido en argumentos", Toast.LENGTH_SHORT).show()
        }

        setupObservers()

        setupDatePicker()

        binding.btnConfirmarCita.setOnClickListener { confirmarCita() }
    }

    private fun setupObservers() {
        viewModel.confirmationResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(requireContext(),
                        "Cita confirmada con éxito",
                        Toast.LENGTH_SHORT).show()

                    findNavController().navigate(
                        R.id.myAppointmentsFragment,
                        null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.dateFragment, true)
                            .build()
                    )
                },
                onFailure = { e ->
                    Toast.makeText(requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.MaterialDatePickerTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = apiFormat.format(calendar.time)

                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDate.setText(displayFormat.format(calendar.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun confirmarCita() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(requireContext(),
                "Por favor, selecciona una fecha",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (token.isEmpty()) {
            Toast.makeText(requireContext(),
                "Error: No se encontró token de autenticación",
                Toast.LENGTH_SHORT).show()
            return
        }

        val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute
        val selectedTime = String.format("%02d:%02d", hour, minute)
        Log.d("DateFragment", "Confirmando cita con los siguientes datos:")
        Log.d("DateFragment", "appointmentId: $appointmentId")
        Log.d("DateFragment", "date: $selectedDate")
        Log.d("DateFragment", "time: $selectedTime")
        Log.d("DateFragment", "latitude: $latitude")
        Log.d("DateFragment", "longitude: $longitude")
        Log.d("DateFragment", "token: ${token.take(10)}...")

        viewModel.confirmAppointment(
            token = token,
            appointmentId = appointmentId,
            date = selectedDate,
            time = selectedTime,
            latitude = latitude,
            longitude = longitude
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}