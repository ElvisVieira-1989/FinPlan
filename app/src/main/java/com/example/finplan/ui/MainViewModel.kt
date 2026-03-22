package com.example.finplan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
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
import java.util.Locale

class MainViewModel : ViewModel() {
    var monthlySalary by mutableStateOf("")
    var investmentPercentage by mutableStateOf("")

    var expenseCategories by mutableStateOf<List<ExpenseCategory>>(emptyList())
    var investmentProfiles by mutableStateOf<List<InvestmentProfile>>(emptyList())

    var locationMessage by mutableStateOf("Buscando localização...")
    var isInBrazil by mutableStateOf<Boolean?>(null)

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
                val location: Location? = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                updateLocationStatus(addresses[0].countryCode)
                            } else {
                                updateLocationStatus(null)
                            }
                        }
                    } else {
                        val addresses = withContext(Dispatchers.IO) {
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        }
                        if (!addresses.isNullOrEmpty()) {
                            updateLocationStatus(addresses[0].countryCode)
                        } else {
                            updateLocationStatus(null)
                        }
                    }
                } else {
                    updateLocationStatus(null)
                }
            } catch (e: Exception) {
                updateLocationStatus(null)
            }
        }
    }

    private fun updateLocationStatus(countryCode: String?) {
        if (countryCode == "BR") {
            isInBrazil = true
            locationMessage = "Você Está no Brasil"
        } else {
            isInBrazil = false
            locationMessage = if (countryCode != null) {
                "Você está fora do Brasil, o app está configurado apenas para Reais BRL"
            } else {
                "Não foi possível determinar o país, o app está configurado apenas para Reais BRL"
            }
        }
    }
}
