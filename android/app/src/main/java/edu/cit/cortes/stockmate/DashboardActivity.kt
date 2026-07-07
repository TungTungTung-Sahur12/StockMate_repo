package edu.cit.cortes.stockmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

import edu.cit.cortes.stockmate.SessionManager

class DashboardActivity : AppCompatActivity() {
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

        val products = listOf(
            ProductItem("🧴", "Shampoo Sachets"),
            ProductItem("🥫", "Canned Goods"),
            ProductItem("🍬", "Candies"),
            ProductItem("🧃", "Softdrinks"),
            ProductItem("🍚", "Rice (Bigas)"),
            ProductItem("🧼", "Soap"),
            ProductItem("🥤", "Instant Coffee"),
            ProductItem("🍜", "Instant Noodles"),
            ProductItem("📶", "E-load"),
            ProductItem("🥖", "Bread (Pandesal)")
        )

        productRecyclerView.layoutManager = GridLayoutManager(this, 3)
        productRecyclerView.adapter = ProductAdapter(products)

        val sessionRole = SessionManager.getInstance(this).getUserRole()
        val name = intent.getStringExtra("name") ?: "Admin"
        val role = intent.getStringExtra("role") ?: sessionRole ?: "ADMIN"
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Magandang umaga"
            hour < 18 -> "Magandang hapon"
            else -> "Magandang gabi"
        }

        title.text = "🏪 StockMate"
        subtitle.text = "$greeting, $name!"
        roleBadge.text = role.uppercase()

        if (role.equals("ADMIN", ignoreCase = true)) {
            staffSectionTitle.text = "🙋 Admin Access"
            staffSectionDescription.text = "You are logged in as Admin. Use Manage User Accounts to create and manage staff members."
        } else {
            staffSectionTitle.text = "🙋 Staff Access"
            staffSectionDescription.text = "You're logged in as Staff. Inventory and sales tools will appear here once enabled by your Admin."
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        val manageButton = findViewById<MaterialButton>(R.id.manageUsersButton)
        if (role == "ADMIN") {
            manageButton.setOnClickListener {
                startActivity(Intent(this, ManageUsersActivity::class.java))
            }
            manageButton.visibility = MaterialButton.VISIBLE
        } else {
            manageButton.visibility = MaterialButton.GONE
        }
    }
}
