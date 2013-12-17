package com.manong.wodi.adapter;

import java.util.List;

import com.manong.wodi.R;
import com.manong.wodi.R.id;
import com.manong.wodi.R.layout;
import com.manong.wodi.entity.ProduceEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestLvAdapter extends BaseAdapter {
	private List<ProduceEntity> entitys;
	private Context context;
	
	public TestLvAdapter(Context context,List<ProduceEntity> entitys){
		this.context = context;
		this.entitys = entitys;
	}
	
	@Override
	public int getCount() {
		return entitys.size();
	}

	@Override
	public Object getItem(int position) {
		return entitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.test_lv_item_layout, null);
			vh.tvName = (TextView)convertView.findViewById(R.id.name);
			vh.tvPrice = (TextView)convertView.findViewById(R.id.price);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}
		ProduceEntity entity = entitys.get(position);
		vh.tvName.setText(entity.getName());
		vh.tvPrice.setText(entity.getPrice());
		return convertView;
	}
	private class ViewHolder{
		private TextView tvName,tvPrice;
	}
}
