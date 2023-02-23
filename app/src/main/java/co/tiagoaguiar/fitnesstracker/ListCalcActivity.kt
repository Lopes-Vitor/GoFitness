package co.tiagoaguiar.fitnesstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.fitnesstracker.model.Calc
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class ListCalcActivity : AppCompatActivity() {

    private lateinit var rvList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc_activity)

        val result = mutableListOf<Calc>()
        val adapter = ListCalcAdapter(result)
        rvList = findViewById(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        val type =
            intent?.extras?.getString("type") ?: throw IllegalStateException("type not found")

        Thread {
            val app = application as App
            val dao = app.db.calcDao()
            val response = dao.getRegisterByType(type)


            runOnUiThread() {
                result.addAll(response)
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    private inner class ListCalcAdapter(
        private val listCalc: List<Calc>,
    ) : RecyclerView.Adapter<ListCalcAdapter.ListViewHolder>() {
        // 1- Qual é o layout da célula (item)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ListViewHolder(view)
        }

        // 2 - Disparado toda vez que houver rolagem na tela e for necessario trocar o conteudo da celula
        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val itemCurrent = listCalc[position]
            holder.bind(itemCurrent)
        }

        // 3 - informar quantas celulas a listagem vai ter
        override fun getItemCount(): Int {
            return listCalc.size
        }

        // classe que administra/busca a célula
        private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc) {
                val tv = itemView as TextView

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                val data = sdf.format(item.createdDate)
                val res = item.res

                tv.text = getString(R.string.list_response, res, data)
            }
        }

    }
}
