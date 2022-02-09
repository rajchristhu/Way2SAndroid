package com.christhu.way2s
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.christhu.way2s.databinding.AdapterViewBinding
import com.christhu.way2s.map.MapActivity
import org.jetbrains.anko.startActivity

class MainAdapter(val mainActivity: MainActivity,val lat: Double?,val longs: Double?) : RecyclerView.Adapter<MainViewHolder>() {

    var datas = mutableListOf<ModelClass>()

    fun setDataList(modelClasses: List<ModelClass>) {
        this.datas = modelClasses.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterViewBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = datas[position]
        holder.binding.name.text = data.title
        holder.binding.body.text = data.body
        holder.binding.route.setOnClickListener {
            mainActivity.startActivity<MapActivity>("lat" to lat.toString(),"long" to longs.toString())
        }

    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class MainViewHolder(val binding: AdapterViewBinding) : RecyclerView.ViewHolder(binding.root) {

}