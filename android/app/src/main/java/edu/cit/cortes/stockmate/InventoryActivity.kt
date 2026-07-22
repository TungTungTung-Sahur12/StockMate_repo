package edu.cit.cortes.stockmate

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.cortes.stockmate.model.CreateProductRequest
import edu.cit.cortes.stockmate.model.ProductResponse
import edu.cit.cortes.stockmate.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryActivity : AppCompatActivity() {
    private lateinit var productAdapter: ProductAdapter
    private val displayCategories = listOf("All", "Tee", "Box Fit Tee", "Hoodie", "Pants", "Cap", "Polo", "Shorts", "Longsleeves", "Accessories")
    private val backendCategories = listOf<String?>(null, "TEE", "BOX_FIT_TEE", "HOODIE", "PANTS", "CAP", "POLO", "SHORTS", "LONGSLEEVES", "ACCESSORIES")
    private val sizeOptions = listOf("Select size", "XS", "S", "M", "L", "XL", "XXL")
    private var allProducts: List<ProductResponse> = emptyList()
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val backButton = findViewById<MaterialButton>(R.id.backToDashboard)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val nameLayout = findViewById<TextInputLayout>(R.id.nameLayout)
        val sizeSpinner = findViewById<Spinner>(R.id.sizeSpinner)
        val sizeError = findViewById<TextView>(R.id.sizeError)
        val priceLayout = findViewById<TextInputLayout>(R.id.priceLayout)
        val quantityLayout = findViewById<TextInputLayout>(R.id.quantityLayout)
        val thresholdLayout = findViewById<TextInputLayout>(R.id.thresholdLayout)
        val nameInput = findViewById<TextInputEditText>(R.id.productNameInput)
        val priceInput = findViewById<TextInputEditText>(R.id.productPriceInput)
        val quantityInput = findViewById<TextInputEditText>(R.id.productQuantityInput)
        val thresholdInput = findViewById<TextInputEditText>(R.id.productThresholdInput)
        val addButton = findViewById<MaterialButton>(R.id.addProductButton)
        val recyclerView = findViewById<RecyclerView>(R.id.productRecyclerView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        categorySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = backendCategories[position]
                loadProducts()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }

        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizeOptions)
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sizeSpinner.adapter = sizeAdapter

        productAdapter = ProductAdapter(
            emptyList(),
            onDeleteClick = { product -> deleteProduct(product) },
            showDelete = SessionManager.getInstance(this).getUserRole().equals("ADMIN", ignoreCase = true)
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter

        backButton.setOnClickListener { finish() }

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val priceText = priceInput.text.toString().trim()
            val quantityText = quantityInput.text.toString().trim()
            val thresholdText = thresholdInput.text.toString().trim()

            nameLayout.error = null
            sizeError.visibility = View.GONE
            priceLayout.error = null
            quantityLayout.error = null
            thresholdLayout.error = null

            if (name.isBlank()) {
                nameLayout.error = "Name is required"
                return@setOnClickListener
            }

            val sizePosition = sizeSpinner.selectedItemPosition
            if (sizePosition <= 0) {
                sizeError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            val size = sizeOptions[sizePosition]

            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0.0) {
                priceLayout.error = "Price must be greater than zero"
                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()
            if (quantity == null || quantity < 0) {
                quantityLayout.error = "Quantity cannot be negative"
                return@setOnClickListener
            }

            val threshold = thresholdText.toIntOrNull()
            if (threshold == null || threshold < 0) {
                thresholdLayout.error = "Threshold cannot be negative"
                return@setOnClickListener
            }

            val request = CreateProductRequest(
                name = name,
                size = size,
                category = selectedCategory ?: "TEE",
                price = price,
                quantity = quantity,
                lowStockThreshold = threshold.coerceAtLeast(1)
            )

            addProduct(request)
        }

        loadProducts()
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.getProductApiService(this@InventoryActivity).getProducts(selectedCategory)
                val products = if (response.isSuccessful) response.body().orEmpty() else emptyList()
                withContext(Dispatchers.Main) {
                    allProducts = products
                    productAdapter.submitList(products)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InventoryActivity, "Unable to load inventory", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addProduct(request: CreateProductRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.getProductApiService(this@InventoryActivity).createProduct(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@InventoryActivity, "Product added", Toast.LENGTH_SHORT).show()
                        loadProducts()
                    } else {
                        Toast.makeText(this@InventoryActivity, "Unable to add product", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InventoryActivity, "Unable to add product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteProduct(product: ProductResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.getProductApiService(this@InventoryActivity).deleteProduct(product.productId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@InventoryActivity, "Product deleted", Toast.LENGTH_SHORT).show()
                        loadProducts()
                    } else {
                        Toast.makeText(this@InventoryActivity, "You do not have permission to delete products", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InventoryActivity, "Unable to delete product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
