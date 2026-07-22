package edu.cit.cortes.stockmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import edu.cit.cortes.stockmate.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class DashboardActivity : AppCompatActivity() {
    private var isAdminRole = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val title = findViewById<MaterialTextView>(R.id.dashboardTitle)
        val subtitle = findViewById<MaterialTextView>(R.id.dashboardSubtitle)
        val roleBadge = findViewById<MaterialTextView>(R.id.roleBadge)
        val logoutButton = findViewById<MaterialButton>(R.id.logoutButton)
        val productRecyclerView = findViewById<RecyclerView>(R.id.productRecyclerView)
        val staffSectionTitle = findViewById<MaterialTextView>(R.id.staffSectionTitle)
        val staffSectionDescription = findViewById<MaterialTextView>(R.id.staffSectionDescription)
        val staffCard = findViewById<MaterialCardView>(R.id.staffCard)
        val manageButton = findViewById<MaterialButton>(R.id.manageUsersButton)
        val inventoryButton = findViewById<MaterialButton>(R.id.inventoryButton)
        val recordSaleButton = findViewById<MaterialButton>(R.id.recordSaleButton)
        val statCardStaff = findViewById<MaterialCardView>(R.id.statCardStaff)
        val valueTotalProducts = findViewById<TextView>(R.id.valueTotalProducts)
        val valueLowStock = findViewById<TextView>(R.id.valueLowStock)
        val valueTodaysRevenue = findViewById<TextView>(R.id.valueTodaysRevenue)
        val valueTotalRevenue = findViewById<TextView>(R.id.valueTotalRevenue)
        val valueStaffAccounts = findViewById<TextView>(R.id.valueStaffAccounts)

        productRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productRecyclerView.adapter = ProductAdapter(emptyList(), onDeleteClick = {}, showDelete = false)

        val sessionRole = SessionManager.getInstance(this).getUserRole()
        val name = intent.getStringExtra("name") ?: "Admin"
        val role = intent.getStringExtra("role") ?: sessionRole ?: "ADMIN"
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }

        title.text = "ASCENDIA"
        subtitle.text = "$greeting, $name!"
        roleBadge.text = role.uppercase()

        inventoryButton.visibility = View.VISIBLE
        inventoryButton.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // Both Admin and Staff can record sales (BR-003).
        recordSaleButton.visibility = View.VISIBLE
        recordSaleButton.setOnClickListener {
            startActivity(Intent(this, RecordSaleActivity::class.java))
        }

        isAdminRole = role.equals("ADMIN", ignoreCase = true)
        statCardStaff.visibility = if (isAdminRole) View.VISIBLE else View.GONE

        if (isAdminRole) {
            staffSectionTitle.text = "ASCENDIA Team Accounts"
            staffSectionDescription.text = "Add staff accounts and manage who can access ASCENDIA."
            manageButton.visibility = View.VISIBLE
            manageButton.setOnClickListener {
                startActivity(Intent(this, ManageUsersActivity::class.java))
            }
            staffCard.visibility = MaterialCardView.GONE
        } else {
            staffSectionTitle.text = "Staff Access"
            staffSectionDescription.text = "You're logged in as Staff. Use the buttons above to browse inventory and record sales."
            manageButton.visibility = View.GONE
            staffCard.visibility = MaterialCardView.VISIBLE
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        loadDashboardStats(valueTotalProducts, valueLowStock, valueTodaysRevenue, valueTotalRevenue, valueStaffAccounts)
    }

    private fun loadDashboardStats(
        totalProductsView: TextView,
        lowStockView: TextView,
        todaysRevenueView: TextView,
        totalRevenueView: TextView,
        staffAccountsView: TextView
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productsResponse = ApiClient.getProductApiService(this@DashboardActivity).getProducts()
                if (productsResponse.isSuccessful) {
                    val products = productsResponse.body().orEmpty()
                    val lowStockCount = products.count {
                        InventoryUiUtils.isLowStock(it.quantity, it.lowStockThreshold)
                    }
                    withContext(Dispatchers.Main) {
                        totalProductsView.text = products.size.toString()
                        lowStockView.text = lowStockCount.toString()
                    }
                }
            } catch (e: Exception) {
                // Leave placeholders on failure; dashboard stays usable.
            }

            try {
                val today = todayDateString()
                val todaySummary = ApiClient.getSaleApiService(this@DashboardActivity).getSalesSummary(today, today)
                if (todaySummary.isSuccessful) {
                    val revenue = todaySummary.body()?.totalRevenue ?: 0.0
                    withContext(Dispatchers.Main) {
                        todaysRevenueView.text = "₱%.2f".format(revenue)
                    }
                }
            } catch (e: Exception) {
                // Leave placeholder on failure.
            }

            try {
                val allTimeSummary = ApiClient.getSaleApiService(this@DashboardActivity).getSalesSummary(null, null)
                if (allTimeSummary.isSuccessful) {
                    val revenue = allTimeSummary.body()?.totalRevenue ?: 0.0
                    withContext(Dispatchers.Main) {
                        totalRevenueView.text = "₱%.2f".format(revenue)
                    }
                }
            } catch (e: Exception) {
                // Leave placeholder on failure.
            }

            if (isAdminRole) {
                try {
                    val usersResponse = ApiClient.getAdminApiService(this@DashboardActivity).getUsers()
                    if (usersResponse.isSuccessful) {
                        val staffCount = usersResponse.body().orEmpty().count { it.role.equals("STAFF", ignoreCase = true) }
                        withContext(Dispatchers.Main) {
                            staffAccountsView.text = staffCount.toString()
                        }
                    }
                } catch (e: Exception) {
                    // Leave placeholder on failure.
                }
            }
        }
    }

    private fun todayDateString(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            Locale.US,
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
