package dev.jet.desktop.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import dev.jet.main.App;

public class DesktopApp {
        public static void main (String[] args) {
                new LwjglApplication(new App(), "Game", 800, 480, false);
        }
}