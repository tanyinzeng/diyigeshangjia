package com.manong.wodi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_layout);
		findViewById(R.id.title_save).setVisibility(View.INVISIBLE);
		findViewById(R.id.user_deal).setVisibility(View.INVISIBLE);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		findViewById(R.id.title_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tvTitle = (TextView)findViewById(R.id.title_text);
		tvTitle.setText("ÓÎÏ·ËµÃ÷");
	}
}
