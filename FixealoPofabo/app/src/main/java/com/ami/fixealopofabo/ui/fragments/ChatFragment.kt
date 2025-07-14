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
import com.ami.FixealoPofabo.databinding.FragmentChatBinding

import com.ami.fixealopofabo.ui.adapter.ChatAdapter
import com.ami.fixealopofabo.viewmodel.ChatViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        appointmentId = args.getInt("appointmentId", -1)

        val receiverUserIdFromDetail = args.getInt("receiverUserId", -1)
        val workerUserIdFromAppointments = args.getInt("workerUserId", -1)
        var appointmentStatus = args.getInt("appointmentStatus", -1)

        val sharedPrefs = requireActivity().getSharedPreferences("reviews", Context.MODE_PRIVATE)
        val isReviewed = sharedPrefs.getBoolean("appointment_${appointmentId}_reviewed", false)

        if (appointmentStatus == 3 && isReviewed) {
            appointmentStatus = 4
            Log.d("ChatFragment", "Appointment $appointmentId ya fue calificado, cambiando a status 4")
        }

        if (receiverUserIdFromDetail != -1) {
            userIdFromAppointment = args.getInt("userId", -1)
            workerIdFromAppointment = args.getInt("workerId", -1)
            receiverId = receiverUserIdFromDetail
            Log.d("ChatFragment", "Flujo desde WorkerDetail")
        } else if (workerUserIdFromAppointments != -1) {
            userIdFromAppointment = args.getInt("userId", -1)
            workerIdFromAppointment = args.getInt("workerId", -1)
            receiverId = workerUserIdFromAppointments
            Log.d("ChatFragment", "Flujo desde MyAppointments")
        } else {
            userIdFromAppointment = args.getInt("userId", -1)
            workerIdFromAppointment = args.getInt("workerId", -1)
            receiverId = workerIdFromAppointment
            Log.d("ChatFragment", "Flujo fallback")
        }

        val workerName = args.getString("workerName") ?: "Trabajador"
        val workerPhoto = args.getString("workerPhoto") ?: ""
        val isConfirmed = args.getBoolean("isConfirmed", false)

        Log.d("ChatFragment", "Datos recibidos: appointmentId=$appointmentId, receiverId=$receiverId, workerName=$workerName, appointmentStatus=$appointmentStatus")

        val myId = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        Log.d("ChatFragment", "Datos de sesión: myId=$myId")

        if (appointmentId == -1 || receiverId == -1 || myId == -1) {
            Toast.makeText(requireContext(), "Datos de sesión o cita incompletos", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        when (appointmentStatus) {
            0 -> {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.alpha = 0.5f
                binding.btnConfirm.text = "Pendiente"
                Log.d("ChatFragment", "Botón deshabilitado - status: 0 (Pendiente)")
            }
            1 -> {
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.alpha = 1f
                binding.btnConfirm.text = "Confirmar Cita"
                Log.d("ChatFragment", "Botón habilitado - status: 1 (Confirmar)")
            }
            2 -> {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.alpha = 0.5f
                binding.btnConfirm.text = "Cita Confirmada"
                Log.d("ChatFragment", "Botón deshabilitado - status: 2 (Confirmada)")
            }
            3 -> {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.alpha = 0.5f
                binding.btnConfirm.text = "Trabajo Completado"
                Log.d("ChatFragment", "Botón deshabilitado - status: 3 (Completado)")
            }
            4 -> {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.alpha = 0.5f
                binding.btnConfirm.text = "✅ Calificado"
                Log.d("ChatFragment", "Botón deshabilitado - status: 4 (Calificado)")
            }
            else -> {
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.alpha = 1f
                binding.btnConfirm.text = "Confirmar Cita"
                Log.d("ChatFragment", "Botón habilitado - nueva cita")
            }
        }

        binding.tvWorkerName.text = workerName
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
            when (appointmentStatus) {
                0 -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Concretar cita")
                        .setMessage("¿Estás seguro de solicitar esta cita?")
                        .setPositiveButton("Sí") { _, _ ->
                            val bundle = Bundle().apply {
                                putInt("appointmentId", appointmentId)
                            }
                            findNavController().navigate(R.id.action_chatFragment_to_locationPickerFragment, bundle)
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
                else -> {
                    when (appointmentStatus) {
                        1 -> Toast.makeText(requireContext(), "Cita ya solicitada, esperando confirmación del trabajador.", Toast.LENGTH_SHORT).show()
                        2 -> Toast.makeText(requireContext(), "Cita ya confirmada por el trabajador.", Toast.LENGTH_SHORT).show()
                        3 -> Toast.makeText(requireContext(), "Trabajo completado.", Toast.LENGTH_SHORT).show()
                        4 -> Toast.makeText(requireContext(), "Cita ya calificada.", Toast.LENGTH_SHORT).show()
                        else -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Concretar cita")
                                .setMessage("¿Estás seguro de solicitar esta cita?")
                                .setPositiveButton("Sí") { _, _ ->
                                    val bundle = Bundle().apply {
                                        putInt("appointmentId", appointmentId)
                                    }
                                    findNavController().navigate(R.id.action_chatFragment_to_locationPickerFragment, bundle)
                                }
                                .setNegativeButton("No", null)
                                .show()
                        }
                    }
                }
            }
        }

        viewModel.fetchMessages(requireContext(), appointmentId)
        viewModel.startPolling(requireContext(), appointmentId)

        if (appointmentStatus == 3) {
            binding.root.postDelayed({
                showReviewDialog(workerName, workerPhoto)
            }, 500)
        }
    }

    private fun showReviewDialog(workerName: String, workerPhoto: String) {
        val bundle = Bundle().apply {
            putInt("appointmentId", appointmentId)
            putString("workerName", workerName)
            putString("workerPhoto", workerPhoto)
        }

        val reviewDialog = ReviewDialogFragment()
        reviewDialog.arguments = bundle
        reviewDialog.isCancelable = false
        reviewDialog.show(parentFragmentManager, "ReviewDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopPolling()
        _binding = null
    }
}