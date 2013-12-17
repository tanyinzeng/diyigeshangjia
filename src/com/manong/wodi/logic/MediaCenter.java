package com.manong.wodi.logic;

import java.util.ArrayList;
import java.util.List;
import com.manong.wodi.entity.CardEntity;

public class MediaCenter {
	
	private static MediaCenter ins;

	public static MediaCenter getIns() {
		if (ins == null) {
			ins = new MediaCenter();
		}

		return ins;
	}
	
	private boolean isPoint = false;
	
	
	public boolean isPoint() {
		return isPoint;
	}

	public void setPoint(boolean isPoint) {
		this.isPoint = isPoint;
	}

	private List<CardEntity> cardEntitys = new ArrayList<CardEntity>();
	
	public void addCardEntity(CardEntity entity){
		cardEntitys.add(entity);
	}
	
	public List<CardEntity> getCardEntitys(){
		return cardEntitys;
	}
	
	public void clearCardEntitys(){
		cardEntitys.clear();
	}
}
