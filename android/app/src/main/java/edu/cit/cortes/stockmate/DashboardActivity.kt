package edu.cit.cortes.stockmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

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
        val staffCard = findViewById<MaterialCardView>(R.id.staffCard)
        val manageButton = findViewById<MaterialButton>(R.id.manageUsersButton)

        val products = listOf(
            ProductItem("Signature Jackets"),
            ProductItem("Tailored Trousers"),
            ProductItem("Layered Scarves"),
            ProductItem("Street Sneakers"),
            ProductItem("Structured Bags"),
            ProductItem("Caps"),
            ProductItem("Formal Shirts"),
            ProductItem("Premium Fabrics"),
            ProductItem("Limited Drops"),
            ProductItem("Bundle Sets")
        )

        productRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productRecyclerView.adapter = ProductAdapter(products)

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

        if (role.equals("ADMIN", ignoreCase = true)) {
            staffSectionTitle.text = "ASCENDIA Team Accounts"
            staffSectionDescription.text = "Add staff accounts and manage who can access ASCENDIA."
            manageButton.visibility = MaterialButton.VISIBLE
            manageButton.setOnClickListener {
                startActivity(Intent(this, ManageUsersActivity::class.java))
            }
            staffCard.visibility = MaterialCardView.GONE
        } else {
            staffSectionTitle.text = "Staff Access"
            staffSectionDescription.text = "You're logged in as Staff. Inventory and sales tools will appear here once enabled by your Admin."
            manageButton.visibility = MaterialButton.GONE
            staffCard.visibility = MaterialCardView.VISIBLE
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
