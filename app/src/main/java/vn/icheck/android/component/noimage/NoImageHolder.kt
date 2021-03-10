package vn.icheck.android.component.noimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.databinding.HolderNoImageBinding

class NoImageHolder(val binding:HolderNoImageBinding):RecyclerView.ViewHolder(binding.root){

    companion object{
        fun create(parent:ViewGroup): NoImageHolder {
            val binding = HolderNoImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoImageHolder(binding)
        }
    }
}