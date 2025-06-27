package com.example.futbolcolombiano.ui.leaguedetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futbolcolombiano.databinding.FragmentMatchesBinding
import com.example.futbolcolombiano.ui.leaguedetails.LeagueDetailsViewModel
import com.example.futbolcolombiano.ui.leaguedetails.LeaguePagerAdapter
import com.example.futbolcolombiano.ui.leaguedetails.adapters.MatchAdapter

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private val leagueDetailsViewModel: LeagueDetailsViewModel by activityViewModels()
    private var competitionApiId: Int = -1 // Cambiado a Int, valor por defecto inválido
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Leer el ID como Int
            competitionApiId = it.getInt(LeaguePagerAdapter.ARG_COMPETITION_API_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        if (competitionApiId != -1) {
            if (leagueDetailsViewModel.matches.value.isNullOrEmpty()) { // Cargar solo si no hay datos ya
                // Usar el competitionApiId (Int)
                leagueDetailsViewModel.loadMatches(competitionApiId)
            }
        } else {
            Toast.makeText(context, "Error: ID de competición API no disponible en MatchesFragment", Toast.LENGTH_LONG).show()
            binding.tvNoMatches.text = "Error: ID de competición API no encontrado."
            binding.tvNoMatches.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter { match ->
            // Acción al hacer clic en un partido, ej:
            // Toast.makeText(context, "Partido: ${match.localTeam.name} vs ${match.visitorTeam.name}", Toast.LENGTH_SHORT).show()
        }
        binding.rvMatches.apply {
            adapter = matchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        leagueDetailsViewModel.matches.observe(viewLifecycleOwner) { matches ->
            matchAdapter.submitList(matches)
            if (matches.isNullOrEmpty() && leagueDetailsViewModel.errorMatches.value == null && (leagueDetailsViewModel.isLoadingMatches.value == false)) {
                binding.tvNoMatches.text = "No hay partidos programados o disponibles."
                binding.tvNoMatches.visibility = View.VISIBLE
            } else if (!matches.isNullOrEmpty()){
                binding.tvNoMatches.visibility = View.GONE
            }
        }

        leagueDetailsViewModel.isLoadingMatches.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvNoMatches.text = "Cargando partidos..."
                binding.tvNoMatches.visibility = View.VISIBLE
                binding.rvMatches.visibility = View.GONE // Ocultar RecyclerView mientras carga
            } else {
                binding.rvMatches.visibility = View.VISIBLE // Mostrar RecyclerView
                // La visibilidad de tvNoMatches se maneja en el observer de 'matches' y 'errorMatches'
                 if (matchAdapter.itemCount > 0) binding.tvNoMatches.visibility = View.GONE
            }
        }

        leagueDetailsViewModel.errorMatches.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (matchAdapter.itemCount == 0) {
                    binding.tvNoMatches.text = it
                    binding.tvNoMatches.visibility = View.VISIBLE
                    binding.rvMatches.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
