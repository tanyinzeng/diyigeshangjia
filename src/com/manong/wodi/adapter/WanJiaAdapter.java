package com.manong.wodi.adapter;

import java.util.List;

import com.manong.wodi.R;
import com.manong.wodi.entity.WanJiaEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WanJiaAdapter extends BaseAdapter {
	private List<WanJiaEntity> entitys;
	private Context context;
	
	public WanJiaAdapter(Context context,List<WanJiaEntity> entitys){
		this.entitys = entitys;
		this.context = context;
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
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_view_item, null);
			vh.tvStatus = (TextView)convertView.findViewById(R.id.wan_jia_status);
			vh.layout = (ImageView)convertView.findViewById(R.id.layout_bg);
			vh.tvNum = (TextView)convertView.findViewById(R.id.num);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}
		WanJiaEntity entity = entitys.get(position);
		vh.tvNum.setText((position+1)+"");
		if(entity.getIsOpen() == 2){
			vh.layout.setBackgroundResource(R.drawable.open);
			if(entity.isWoDi()){
				vh.tvNum.setVisibility(View.GONE);
				vh.tvStatus.setVisibility(View.VISIBLE);
				vh.tvStatus.setText(context.getResources().getString(R.string.wo_di));
			}else if(entity.isSucc() ){
				vh.tvNum.setVisibility(View.GONE);
				vh.tvStatus.setVisibility(View.VISIBLE);
				vh.tvStatus.setText(context.getResources().getString(R.string.sheng_li));
			}else{
				vh.tvNum.setVisibility(View.GONE);
				vh.tvStatus.setVisibility(View.VISIBLE);
				vh.tvStatus.setText(context.getResources().getString(R.string.yuan_si));
			}
		}else if(entity.getIsOpen() == 1){
			vh.tvNum.setVisibility(View.VISIBLE);
			vh.tvStatus.setVisibility(View.GONE);
			vh.layout.setBackgroundResource(R.drawable.open);
		}
		else{
			vh.tvStatus.setVisibility(View.GONE);
			vh.layout.setBackgroundResource(R.drawable.close);
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		private TextView tvStatus,tvNum;
		private ImageView layout;
	}

}
