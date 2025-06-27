package com.example.futbolcolombiano.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futbolcolombiano.databinding.ActivityMainBinding
import com.example.futbolcolombiano.ui.leaguedetails.LeagueDetailsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var competitionAdapter: CompetitionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        observeViewModel()

        mainViewModel.loadCompetitions()
    }

    private fun setupRecyclerView() {
        competitionAdapter = CompetitionAdapter { competition ->
            // Acción al hacer clic en una competición
            val intent = Intent(this, LeagueDetailsActivity::class.java).apply {
                // TODO: Pasar datos relevantes a LeagueDetailsActivity, ej: competition.id
                putExtra(LeagueDetailsActivity.EXTRA_COMPETITION_ID, competition.id)
                putExtra(LeagueDetailsActivity.EXTRA_COMPETITION_NAME, competition.name)
            }
            startActivity(intent)
        }
        binding.rvCompetitions.apply {
            adapter = competitionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeViewModel() {
        mainViewModel.competitions.observe(this) { competitions ->
            competitionAdapter.submitList(competitions)
            binding.rvCompetitions.visibility = if (competitions.isEmpty()) View.GONE else View.VISIBLE
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        mainViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // Podrías mostrar un TextView con el error en lugar de/además del Toast
                if (competitionAdapter.itemCount == 0) { // Si no hay datos y hay error
                    // tvNoData.text = it
                    // tvNoData.visibility = View.VISIBLE
                }
            }
        }
    }
}
