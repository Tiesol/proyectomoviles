package com.ami.fixealopofabo.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentLocationPickerBinding

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class LocationPickerFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationPickerBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private var appointmentId: Int = -1

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointmentId = arguments?.getInt("appointmentId", -1) ?: -1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnConfirmarUbicacion.setOnClickListener {
            confirmarUbicacion()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        checkLocationPermission()

        map.setOnCameraIdleListener {
            val center = map.cameraPosition.target
            updateLocationInfo(center)
        }
    }

    private fun updateLocationInfo(location: LatLng) {
        binding.tvLocationCoordinates.text = "Lat: ${String.format("%.6f", location.latitude)}, Lng: ${String.format("%.6f", location.longitude)}"
    }

    private fun checkLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (::map.isInitialized) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    moveCamera(currentLocation!!)
                } ?: run {
                    val defaultLocation = LatLng(-17.783616, -63.182084)
                    moveCamera(defaultLocation)
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun moveCamera(location: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        updateLocationInfo(location)
    }
    private fun confirmarUbicacion() {
        val ubicacionSeleccionada = map.cameraPosition.target

        val token = requireActivity()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No se encontró token de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putInt("appointmentId", appointmentId)
            putString("latitude", ubicacionSeleccionada.latitude.toString())
            putString("longitude", ubicacionSeleccionada.longitude.toString())
            putString("token", token)
        }

        findNavController().navigate(R.id.action_locationPickerFragment_to_dateFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}