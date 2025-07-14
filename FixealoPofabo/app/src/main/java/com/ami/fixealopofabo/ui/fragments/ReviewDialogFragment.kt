package com.ami.fixealopofabo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.R
import com.ami.FixealoPofabo.databinding.FragmentReviewDialogBinding

import com.ami.fixealopofabo.viewmodel.ReviewViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReviewDialogFragment : DialogFragment() {

    private var _binding: FragmentReviewDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReviewViewModel by viewModels()

    private var appointmentId: Int = -1
    private var workerName: String = ""
    private var workerPhoto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appointmentId = it.getInt("appointmentId", -1)
            workerName = it.getString("workerName", "")
            workerPhoto = it.getString("workerPhoto", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvWorkerName.text = workerName
        Glide.with(this)
            .load(workerPhoto.takeIf { it.isNotBlank() && it != "null" })
            .placeholder(R.drawable.user_default)
            .into(binding.ivWorkerPhoto)

        viewModel.reviewResult.observe(viewLifecycleOwner) { result ->
            binding.btnEnviarReview.isEnabled = true
            binding.progressBar.visibility = View.GONE

            result.fold(
                onSuccess = {
                    val sharedPrefs = requireActivity().getSharedPreferences("reviews", Context.MODE_PRIVATE)
                    sharedPrefs.edit()
                        .putBoolean("appointment_${appointmentId}_reviewed", true)
                        .apply()

                    Log.d("ReviewDialog", "Appointment $appointmentId marcado como calificado")

                    Toast.makeText(requireContext(), "¡Review enviado exitosamente!", Toast.LENGTH_SHORT).show()
                    dismiss()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnEnviarReview.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.etComment.text.toString().trim()

            if (rating == 0) {
                Toast.makeText(requireContext(), "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnEnviarReview.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            val token = requireActivity()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("token", "")

            viewModel.sendReview(token!!, appointmentId, rating, comment, true)
        }

        binding.btnCerrar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Cerrar sin enviar review?")
                .setMessage("¿Estás seguro de que quieres cerrar sin calificar el trabajo?")
                .setPositiveButton("Sí, cerrar") { _, _ ->
                    dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}