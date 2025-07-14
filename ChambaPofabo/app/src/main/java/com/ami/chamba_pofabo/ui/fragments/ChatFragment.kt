package com.ami.chamba_pofabo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ami.chamba_pofabo.R
import com.ami.chamba_pofabo.databinding.FragmentChatBinding
import com.ami.chamba_pofabo.ui.adapter.ChatAdapter
import com.ami.chamba_pofabo.viewmodel.ChatViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()

    private lateinit var adapter: ChatAdapter
    private var appointmentId = -1
    private var userIdFromAppointment = -1
    private var workerIdFromAppointment = -1
    private var receiverId = -1
    private var appointmentLatitude: String? = null
    private var appointmentLongitude: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ChatFragment", "=== INICIANDO onViewCreated ===")

        val args = requireArguments()
        appointmentId = args.getInt("appointmentId", -1)
        userIdFromAppointment = args.getInt("userId", -1)
        workerIdFromAppointment = args.getInt("workerId", -1)
        appointmentLatitude = args.getString("latitude")
        appointmentLongitude = args.getString("longitude")

        Log.d("ChatFragment", "appointmentLatitude recibida: '$appointmentLatitude'")
        Log.d("ChatFragment", "appointmentLongitude recibida: '$appointmentLongitude'")

        val appointmentStatus = args.getInt("appointmentStatus", 0)
        val workerName = args.getString("workerName") ?: "Trabajador"
        val workerPhoto = args.getString("workerPhoto") ?: ""
        val isConfirmed = args.getBoolean("isConfirmed", false)
        val categoryId = args.getInt("categoryId", 1) // Por defecto 1 si no viene

        Log.d("ChatFragment", "Datos recibidos: appointmentId=$appointmentId, userId=$userIdFromAppointment, workerId=$workerIdFromAppointment, workerName=$workerName, workerPhoto=$workerPhoto, isConfirmed=$isConfirmed")

        val myId = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        Log.d("ChatFragment", "Datos de sesión: myId=$myId")

        if (appointmentId == -1 || userIdFromAppointment == -1 || workerIdFromAppointment == -1 || myId == -1) {
            Toast.makeText(requireContext(), "Datos de sesión o cita incompletos", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        when (appointmentStatus) {
            0 -> {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.alpha = 0.5f
                binding.btnConfirm.text = "Confirmar Cita"
                Log.d("ChatFragment", "Botón configurado para status 0 - DESHABILITADO")
            }
            1 -> {
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.alpha = 1f
                binding.btnConfirm.text = "Confirmar Cita"
                Log.d("ChatFragment", "Botón configurado para status 1 - CONFIRMAR CITA")
            }
            2 -> {
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.alpha = 1f
                binding.btnConfirm.text = "Finalizar Trabajo"
                Log.d("ChatFragment", "Botón configurado para status 2 - FINALIZAR TRABAJO")
            }
            else -> {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.alpha = 0.5f
                binding.btnConfirm.text = "Cita Completada"
                Log.d("ChatFragment", "Botón configurado para status ${appointmentStatus} - COMPLETADA")
            }
        }

        Log.d("ChatFragment", "myId=$myId, userId=$userIdFromAppointment, workerId=$workerIdFromAppointment")

        receiverId = if (myId == userIdFromAppointment) workerIdFromAppointment else userIdFromAppointment
        binding.tvClientName.text = workerName
        Glide.with(this)
            .load(workerPhoto.takeIf { it.isNotBlank() && it != "null" })
            .placeholder(R.drawable.user_default)
            .into(binding.ivProfile)

        adapter = ChatAdapter(arrayListOf(), myId)
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = this@ChatFragment.adapter
        }

        viewModel.confirmationResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Cita confirmada exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al confirmar la cita", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.finalizeResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Trabajo finalizado exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al finalizar el trabajo", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.messages.observe(viewLifecycleOwner) { msgs ->
            Log.d("ChatFragment", "Mensajes recibidos: ${msgs.size}")
            msgs.forEach {
                Log.d("ChatFragment", "Mensaje: '${it.message}' de ${it.sender.id}")
            }
            adapter.setData(ArrayList(msgs))
            if (msgs.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(msgs.lastIndex)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                Log.d("ChatFragment", "Enviando a $receiverId desde $myId: $text")
                viewModel.sendMessage(requireContext(), appointmentId, text, receiverId)
                binding.etMessage.setText("")
            }
        }

        binding.btnConfirm.setOnClickListener {
            Log.d("ChatFragment", "=== BOTÓN PRESIONADO - appointmentStatus: $appointmentStatus ===")

            when (appointmentStatus) {
                1 -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Confirmar cita")
                        .setMessage("¿Estás seguro de concretar esta cita?")
                        .setPositiveButton("Sí") { _, _ ->
                            Log.d("ChatFragment", "Confirmando cita: appointmentId=$appointmentId, workerId=$workerIdFromAppointment, categoryId=$categoryId")
                            viewModel.confirmAppointment(
                                context = requireContext(),
                                appointmentId = appointmentId,
                                workerId = workerIdFromAppointment,
                                categoryId = categoryId
                            )
                        }
                        .setNeutralButton("Ver ubicación") { _, _ ->
                            val bundle = Bundle().apply {
                                putInt("appointmentId", appointmentId)
                                putString("latitude", appointmentLatitude)
                                putString("longitude", appointmentLongitude)
                            }
                            findNavController().navigate(R.id.action_chatFragment_to_locationPickerFragment, bundle)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

                2 -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Finalizar trabajo")
                        .setMessage("¿Está seguro que desea finalizar el trabajo actual?")
                        .setPositiveButton("Sí") { _, _ ->
                            Log.d("ChatFragment", "Finalizando trabajo: appointmentId=$appointmentId, workerId=$workerIdFromAppointment, categoryId=$categoryId")
                            viewModel.finalizeAppointment(
                                context = requireContext(),
                                appointmentId = appointmentId,
                                workerId = workerIdFromAppointment,
                                categoryId = categoryId
                            )
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Esta cita no está disponible para confirmar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        Log.d("ChatFragment", "=== FINALIZANDO onViewCreated ===")

        viewModel.fetchMessages(requireContext(), appointmentId)
        viewModel.startPolling(requireContext(), appointmentId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopPolling()
        _binding = null
    }
}