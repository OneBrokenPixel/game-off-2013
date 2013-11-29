package com.me.corruption.client;

import com.me.corruption.CorruptionGdxGame;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(1055, 600);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new CorruptionGdxGame();
	}
}