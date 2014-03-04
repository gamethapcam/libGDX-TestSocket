package dev.jet.android.main;

import com.badlogic.gdx.backends.android.AndroidApplication;

import dev.jet.main.App;

public class AndroidApp extends AndroidApplication {
	
	 public void onCreate (android.os.Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         initialize(new App(), false);
	 }	

}