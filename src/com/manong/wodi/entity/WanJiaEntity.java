package com.manong.wodi.entity;

public class WanJiaEntity {
	private String card;
	private boolean isWoDi = false;
	private boolean isSucc = false;
	private int isOpen = 0;//1¿ªÅÆ 2 Ê¤¸º
	private boolean isBaiBan = false;
	public boolean isBaiBan() {
		return isBaiBan;
	}
	public void setBaiBan(boolean isBaiBan) {
		this.isBaiBan = isBaiBan;
	}
	
	public int getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}
	public boolean isSucc() {
		return isSucc;
	}
	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public boolean isWoDi() {
		return isWoDi;
	}
	public void setWoDi(boolean isWoDi) {
		this.isWoDi = isWoDi;
	}
	
}
