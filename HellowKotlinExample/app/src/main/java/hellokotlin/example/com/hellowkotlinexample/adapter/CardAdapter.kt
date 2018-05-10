package hellokotlin.example.com.hellowkotlinexample.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import hellokotlin.example.com.hellowkotlinexample.R
import hellokotlin.example.com.hellowkotlinexample.model.responses.TestModelResponase


class CardAdapter(var list: ArrayList<TestModelResponase.Model>, var context: Context) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size

    }

    override fun onCreateViewHolder(parent: ViewGroup?, position: Int): ViewHolder {
        //Create our view from our xml file
        val view = LayoutInflater.from(context).inflate(R.layout.row_recycler_view, parent, false)

        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        holder?.bindItem(list[position])

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var login: TextView = itemView.findViewById(R.id.login) as TextView
        var repos: TextView = itemView.findViewById(R.id.repos) as TextView
        var blog: TextView = itemView.findViewById(R.id.blog) as TextView


        fun bindItem(item: TestModelResponase.Model) {

            login.text = item.name
            repos.text = item.value1
            blog.text = item.value2

            itemView.setOnClickListener {
                Toast.makeText(context, item.name, Toast.LENGTH_LONG).show()
            }

        }

    }


}