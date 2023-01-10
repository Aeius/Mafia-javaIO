package com.socket;

import java.io.Serializable;

public class User implements Serializable{
	String num = "";
	String name = "";
	String ready = "false";
	String job = "citizen";
	String alive = "alive";
	
	public User(String name, String ready) {
		this.name = name;
		this.ready = ready;
	}
	
}
