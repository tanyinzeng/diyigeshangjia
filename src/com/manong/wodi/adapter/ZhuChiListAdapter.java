package com.manong.wodi.adapter;

import com.manong.wodi.R;
import com.manong.wodi.entity.WanJiaEntity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ZhuChiListAdapter extends BaseAdapter {
	private List<WanJiaEntity> entitys;
	private Context context;
	
	public ZhuChiListAdapter(Context context,List<WanJiaEntity> entitys){
		this.context = context;
		this.entitys = entitys;
	}
	
	@Override
	public int getCount() {
		return entitys.size()-1;
	}

	@Override
	public Object getItem(int arg0) {
		return entitys.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.zhu_chi_list_item, null);
			holder.tvZhuChi = (TextView)convertView.findViewById(R.id.zhu_chi_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		WanJiaEntity entity = entitys.get(position);
		holder.tvZhuChi.setText((position+1)+"ºÅµÄÅÆÊÇ£º"+entity.getCard());
		if(entity.isWoDi() || entity.isBaiBan()){
			holder.tvZhuChi.setTextColor(context.getResources().getColor(R.color.red));
		}else{
			holder.tvZhuChi.setTextColor(context.getResources().getColor(R.color.black));
		}
		return convertView;
	}
	
	private class ViewHolder{
		private TextView tvZhuChi;
	}

}
