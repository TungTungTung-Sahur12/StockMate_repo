package edu.cit.cortes.stockmate

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.cortes.stockmate.model.CreateSaleRequest
import edu.cit.cortes.stockmate.model.ProductResponse
import edu.cit.cortes.stockmate.viewmodel.SaleUiState
import edu.cit.cortes.stockmate.viewmodel.SaleViewModel
import java.util.Calendar
import java.util.Locale

class RecordSaleActivity : AppCompatActivity() {

    private lateinit var viewModel: SaleViewModel
    private lateinit var saleAdapter: SaleAdapter

    private lateinit var productSpinner: Spinner
    private lateinit var productSearchInput: TextInputEditText
    private lateinit var quantityLayout: TextInputLayout
    private lateinit var quantityInput: TextInputEditText
    private lateinit var totalPreview: TextView
    private lateinit var summaryCount: TextView
    private lateinit var summaryRevenue: TextView
    private lateinit var startDateInput: TextInputEditText
    private lateinit var endDateInput: TextInputEditText
    private lateinit var filterProductNameInput: TextInputEditText
    private lateinit var emptyState: TextView

    private var allProducts: List<ProductResponse> = emptyList()
    private var spinnerProducts: List<ProductResponse> = emptyList()
    private var selectedProduct: ProductResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_sale)

        viewModel = ViewModelProvider(this)[SaleViewModel::class.java]

        productSpinner = findViewById(R.id.productSpinner)
        productSearchInput = findViewById(R.id.productSearchInput)
        quantityLayout = findViewById(R.id.quantityLayout)
        quantityInput = findViewById(R.id.quantityInput)
        totalPreview = findViewById(R.id.totalPreview)
        summaryCount = findViewById(R.id.summaryCount)
        summaryRevenue = findViewById(R.id.summaryRevenue)
        startDateInput = findViewById(R.id.startDateInput)
        endDateInput = findViewById(R.id.endDateInput)
        filterProductNameInput = findViewById(R.id.filterProductNameInput)
        emptyState = findViewById(R.id.emptyState)

        val backButton = findViewById<MaterialButton>(R.id.backToDashboard)
        val recordButton = findViewById<MaterialButton>(R.id.recordSaleButton)
        val filterButton = findViewById<MaterialButton>(R.id.filterButton)
        val clearFilterButton = findViewById<MaterialButton>(R.id.clearFilterButton)
        val recyclerView = findViewById<RecyclerView>(R.id.salesRecyclerView)

        saleAdapter = SaleAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = saleAdapter

        backButton.setOnClickListener { finish() }

        productSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedProduct = spinnerProducts.getOrNull(position)
                quantityLayout.error = null
                updateTotalPreview()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedProduct = null
            }
        }

        productSearchInput.addTextChangedListener(simpleWatcher { rebuildSpinner(it) })
        quantityInput.addTextChangedListener(simpleWatcher {
            quantityLayout.error = null
            updateTotalPreview()
        })

        startDateInput.setOnClickListener { showDatePicker(startDateInput) }
        endDateInput.setOnClickListener { showDatePicker(endDateInput) }

        recordButton.setOnClickListener { submitSale() }

        filterButton.setOnClickListener {
            loadSales()
        }
        clearFilterButton.setOnClickListener {
            startDateInput.setText("")
            endDateInput.setText("")
            filterProductNameInput.setText("")
            loadSales()
        }

        observeViewModel()

        viewModel.loadProducts()
        loadSales()
    }

    private fun observeViewModel() {
        viewModel.products.observe(this) { products ->
            allProducts = products
            rebuildSpinner(productSearchInput.text?.toString().orEmpty())
        }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is SaleUiState.SaleRecorded -> {
                    val sale = state.sale
                    Toast.makeText(
                        this,
                        "Sale recorded: ${sale.quantitySold}x ${sale.productName} — ₱%.2f".format(sale.totalAmount),
                        Toast.LENGTH_LONG
                    ).show()
                    quantityInput.setText("")
                    totalPreview.visibility = View.GONE
                    loadSales() // refresh history + summary
                }
                is SaleUiState.SalesLoaded -> {
                    saleAdapter.submitList(state.sales)
                    emptyState.visibility = if (state.sales.isEmpty()) View.VISIBLE else View.GONE
                    val summary = state.summary
                    summaryCount.text = (summary?.totalCount ?: 0L).toString()
                    summaryRevenue.text = "₱%.2f".format(summary?.totalRevenue ?: 0.0)
                }
                is SaleUiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    private fun rebuildSpinner(query: String) {
        val q = query.trim().lowercase(Locale.getDefault())
        spinnerProducts = if (q.isBlank()) allProducts
        else allProducts.filter { it.name.lowercase(Locale.getDefault()).contains(q) }

        val labels = spinnerProducts.map { "${it.name} — ${it.quantity} in stock" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productSpinner.adapter = adapter
        selectedProduct = spinnerProducts.firstOrNull()
        updateTotalPreview()
    }

    private fun updateTotalPreview() {
        val product = selectedProduct
        val qty = quantityInput.text?.toString()?.trim()?.toIntOrNull()
        if (product != null && qty != null && qty > 0) {
            totalPreview.text = "Preview total: ₱%.2f".format(product.price * qty)
            totalPreview.visibility = View.VISIBLE
        } else {
            totalPreview.visibility = View.GONE
        }
    }

    private fun submitSale() {
        quantityLayout.error = null
        val product = selectedProduct
        if (product == null) {
            Toast.makeText(this, "Please select a product.", Toast.LENGTH_SHORT).show()
            return
        }
        val qty = quantityInput.text?.toString()?.trim()?.toIntOrNull()
        if (qty == null || qty < 1) {
            quantityLayout.error = "Quantity must be at least 1."
            return
        }
        // Client-side oversell guard (FR-014); backend enforces authoritatively.
        if (qty > product.quantity) {
            quantityLayout.error = "Quantity exceeds available stock (${product.quantity})."
            return
        }
        viewModel.recordSale(CreateSaleRequest(productId = product.productId, quantitySold = qty))
    }

    private fun loadSales() {
        val start = startDateInput.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
        val end = endDateInput.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
        val name = filterProductNameInput.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
        viewModel.loadSales(start, end, name)
    }

    private fun showDatePicker(target: TextInputEditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                target.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun simpleWatcher(onChange: (String) -> Unit): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            onChange(s?.toString().orEmpty())
        }
    }
}
