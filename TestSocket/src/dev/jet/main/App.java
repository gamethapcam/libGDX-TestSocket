package dev.jet.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.DataOutput;

import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.SocketHints;

public class App implements ApplicationListener {
	
	private Stage stage;
	
	private TextButton bServer;
	private TextButton bClient;
	private Label lState;
	
	boolean connected;
	boolean isServer;
	
	private ServerSocket server;
	private Socket client;
	
	private DataInputStream in;
	private DataOutputStream out;
	
	private int touchCount;
	
	public void create () {
		
		connected = false;
		isServer = false;
		touchCount = 0;
		
		stage = new Stage();
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		Texture normalTex = new Texture(Gdx.files.internal("style-button-normal.png"));
		Texture activeTex = new Texture(Gdx.files.internal("style-button-active.png"));
		
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.up = new TextureRegionDrawable(new TextureRegion(normalTex));
		bStyle.down = new TextureRegionDrawable(new TextureRegion(activeTex));
		bStyle.font = generator.generateFont(20);
		
		LabelStyle lStyle = new LabelStyle();
		lStyle.font = generator.generateFont(20);
		lStyle.fontColor = new Color(1,1,1,1);
		
		bServer = new TextButton("Server", bStyle);
		bClient = new TextButton("Client", bStyle);
		lState = new Label("Pick Client or Server", lStyle);
		
		bServer.setPosition(550, 100);
		bClient.setPosition(150, 100);
		lState.setPosition(300, 250);
		
		bServer.addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                
	        		if (! connected ) {
	        			
	        			lState.setText("Waiting client ...");
	        			
	        			Runnable rWaitCl = new Runnable(){
	        				
	        				public void run(){
	        					
	        					server = Gdx.net.newServerSocket(Protocol.TCP, 8080, new ServerSocketHints());
	    	        			client = server.accept(new SocketHints());
	    	        			
	    	        			connected = true; 
	    	        			isServer = true;
	    	        			
	    	        			in = new DataInputStream(client.getInputStream());
	    	        			out = new DataOutputStream(client.getOutputStream());
	    	        			
	    	        			lState.setText("Connection Established");
	    	        			
	    	        			while(client.isConnected()) {
	    	        				
	    	        				try {
										int count = in.readInt();
										lState.setText("touch count from client: "+count); 
									} catch (IOException e) {
										
									}
	    	        			}
	    	        			
	    	        			lState.setText("Cient Disconnect :(");
	    	        			
	        				}
	        			};
	        			
	        			(new Thread(rWaitCl)).start();
	        			
	        		} else if (isServer) {
	        			
	        			touchCount += 1;
	        			
	        			try {
							out.writeInt(touchCount);
							
						} catch (IOException e) {
							
						}
	        		}
	        		
	        	
	                return true;
	        }
        });
		
		bClient.addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                
	        	if (! connected) {
	        		
	        		lState.setText("Searching server ...");
	        		
	        		try {
	        			client = Gdx.net.newClientSocket(Protocol.TCP, "192.168.1.54", 8080, new SocketHints());
	        			
	        			connected = true;
	        			isServer = false;
	        			
	        			in = new DataInputStream(client.getInputStream());
	        			out = new DataOutputStream(client.getOutputStream());
	        			
	        			Runnable rWaitData = new Runnable(){
	        				
	        				public void run(){
	        					
	        					try {
	        						
									while(client.isConnected()) {
		        						int count = in.readInt();
										lState.setText("Touch count from server: " + count);
									}
									
									lState.setText("Server Disconnect :(");
									
								} catch (IOException e) {
									lState.setText("Connection error !!");
								}
	        					
	        					
	        				}
	        			};
	        			
	        			(new Thread(rWaitData)).start();
	        			
	        			lState.setText("Connected");
	        		} catch (Exception ex){
	        			
	        			lState.setText("Server Not Found");
	        		}
	        	} else if (! isServer) {
	        		
	        		touchCount += 1;
        			
        			try {
						out.writeInt(touchCount);
						
					} catch (IOException e) {
						
					}
	        		
	        	}
	        	
	        	return true;
	        	
	        }
        });
		
		stage.addActor(bServer);
		stage.addActor(bClient);
		stage.addActor(lState);
		
		Gdx.input.setInputProcessor(stage);
		
	}
	
	public void render () {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}
	
	public void resize (int width, int height) {
		stage.setViewport(width, height);
	}
	
	public void pause () {
	}
	
	public void resume () {
	}
	
	public void dispose () {
	}

}
