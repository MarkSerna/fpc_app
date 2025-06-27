package com.example.futbolcolombiano.ui.leaguedetails

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.example.futbolcolombiano.R
import com.example.futbolcolombiano.databinding.ActivityLeagueDetailsBinding

class LeagueDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeagueDetailsBinding
    // ViewModel instanciado pero no usado directamente aquí para cargar datos de la actividad en sí.
    // Los fragments obtendrán su propia instancia o una compartida.
    private val leagueDetailsViewModel: LeagueDetailsViewModel by viewModels()
    private lateinit var leaguePagerAdapter: LeaguePagerAdapter
    private var competitionApiId: Int = -1 // Usaremos -1 como indicador de ID no válido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeagueDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // El ID de la competición de la API es un Int. Nuestro modelo Competition usa String.
        // Lo recibimos como String y lo convertimos.
        val competitionIdString = intent.getStringExtra(EXTRA_COMPETITION_ID)
        val competitionName = intent.getStringExtra(EXTRA_COMPETITION_NAME)

        competitionApiId = competitionIdString?.toIntOrNull() ?: -1

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = competitionName ?: getString(R.string.league_details_title_default)

        if (competitionApiId == -1) {
            Toast.makeText(this, "Error: ID de competición inválido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViewPagerAndTabs(competitionApiId)
    }

    private fun setupViewPagerAndTabs(competitionIdForApi: Int) {
        leaguePagerAdapter = LeaguePagerAdapter(this, competitionIdForApi)
        binding.viewPager.adapter = leaguePagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = leaguePagerAdapter.getPageTitle(position)
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        // Estos siguen siendo los nombres de los extras que MainActivity envía
        const val EXTRA_COMPETITION_ID = "extra_competition_id" // Se envía como String (ID de API)
        const val EXTRA_COMPETITION_NAME = "extra_competition_name"
    }
}
