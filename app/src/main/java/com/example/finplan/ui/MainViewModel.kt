package com.example.finplan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finplan.logic.FinancialCalculator
import com.example.finplan.model.ExpenseCategory
import com.example.finplan.model.InvestmentProfile
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Currency
import java.util.Locale

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"

    var monthlySalary by mutableStateOf("")
    var investmentPercentage by mutableStateOf("")

    var expenseCategories by mutableStateOf<List<ExpenseCategory>>(emptyList())
    var investmentProfiles by mutableStateOf<List<InvestmentProfile>>(emptyList())

    var locationMessage by mutableStateOf("Buscando localização...")
    var isInBrazil by mutableStateOf<Boolean?>(null)
    
    // Novo: Locale dinâmico baseado na localização detectada (padrão Brasil)
    var currentLocale by mutableStateOf(Locale("pt", "BR"))
        private set

    fun calculate() {
        val salary = monthlySalary.toDoubleOrNull() ?: 0.0
        val percentage = investmentPercentage.toDoubleOrNull() ?: 0.0

        if (salary > 0 && percentage >= 0 && percentage <= 100) {
            expenseCategories = FinancialCalculator.calculateExpenses(salary, percentage)
            val investmentAmount = salary * (percentage / 100.0)
            investmentProfiles = FinancialCalculator.getInvestmentProfiles(investmentAmount)
        } else {
            expenseCategories = emptyList()
            investmentProfiles = emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    fun checkLocation(context: Context) {
        viewModelScope.launch {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                
                // Estratégia de busca com timeouts
                var location: Location? = withTimeoutOrNull(8000) {
                    try {
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                    } catch (e: Exception) { null }
                }

                if (location == null) {
                    location = withTimeoutOrNull(5000) {
                        try {
                            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                        } catch (e: Exception) { null }
                    }
                }

                if (location == null) {
                    location = try {
                        fusedLocationClient.lastLocation.await()
                    } catch (e: Exception) { null }
                }

                if (location != null) {
                    processLocation(context, location)
                } else {
                    updateLocationStatus(null, null)
                }
            } catch (e: Exception) {
                updateLocationStatus(null, null)
            }
        }
    }

    private suspend fun processLocation(context: Context, location: Location) {
        if (!Geocoder.isPresent()) {
            updateLocationStatus(null, null)
            return
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    viewModelScope.launch(Dispatchers.Main) {
                        val address = addresses.firstOrNull()
                        updateLocationStatus(address?.countryCode, address?.countryName)
                    }
                }
            } else {
                val addresses = withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    } catch (e: Exception) { null }
                }
                val address = addresses?.firstOrNull()
                updateLocationStatus(address?.countryCode, address?.countryName)
            }
        } catch (e: Exception) {
            updateLocationStatus(null, null)
        }
    }

    private fun updateLocationStatus(countryCode: String?, countryName: String?) {
        if (countryCode.isNullOrBlank()) {
            isInBrazil = null
            locationMessage = "Localização não determinada. Usando BRL por padrão."
            currentLocale = Locale("pt", "BR")
            return
        }

        // Define o Locale baseado no país detectado
        val detectedLocale = Locale("", countryCode)
        currentLocale = detectedLocale
        
        val currencyCode = try {
            Currency.getInstance(detectedLocale).currencyCode
        } catch (e: Exception) { "???" }

        if (countryCode.equals("BR", ignoreCase = true)) {
            isInBrazil = true
            locationMessage = "Localizado: Brasil. Moeda: BRL (R$)"
        } else {
            isInBrazil = false
            val displayCountry = countryName ?: detectedLocale.displayCountry
            locationMessage = "Detectado: $displayCountry. App ajustado para a moeda local ($currencyCode)."
        }
        
        // Recalcula para garantir que a UI mostre os novos formatos
        calculate()
        Log.d(TAG, "updateLocationStatus: $locationMessage | Locale: ${currentLocale.displayName}")
    }
}
