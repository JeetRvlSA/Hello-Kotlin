package hellokotlin.example.com.hellowkotlinexample.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import hellokotlin.example.com.hellowkotlinexample.R
import hellokotlin.example.com.hellowkotlinexample.adapter.CardAdapter
import hellokotlin.example.com.hellowkotlinexample.model.responses.TestModelResponase
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    private var listTestResponase: ArrayList<TestModelResponase.Model>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        listTestResponase = ArrayList()

        for (index in 0..10) {
            var item = TestModelResponase.Model()
            item.name = "test $index"
            item.value1 = "value $index"
            item.value2 = "value $index"
            listTestResponase!!.add(item)
        }

        var layoutManager = LinearLayoutManager(this)

        var adapter = CardAdapter(listTestResponase!!, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

    }
}
