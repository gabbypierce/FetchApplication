package com.hfad.fetchdata

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var itemsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Fetch Application"

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchData()
    }

    private fun fetchData() {
        RetrofitClient.instance.fetchItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val filteredItems = body
                            .filter { it.name != null && it.name.isNotBlank() }
                            .sortedWith(compareBy({ it.listId }, { it.name }))

                        itemsRecyclerView.adapter = ItemsAdapter(filteredItems)
                    } ?: run {
                        showError("Received empty response")
                    }
                } else {
                    showError("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                showError("Failed to fetch data: ${t.message}")
            }
        })
        }


    private fun showError(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}
