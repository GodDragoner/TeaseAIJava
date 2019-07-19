package me.goddragon.teaseai.utils;

import estimAPI.EstimAPI;
import me.goddragon.teaseai.TeaseAI;

import java.util.Objects;

import devices.TwoB.TwoBChannel;
import devices.TwoB.TwoBMode;

/**
 * Created by xman2B on 07.07.2019.
 */
public class EstimApi {
	private int bpm;
	private EstimAPI api;
	
	public EstimApi() {
		api = TeaseAI.application.getSession().getEstimAPI();
		Objects.requireNonNull(api, "Can't get the instance of EstimAPI");
	}

	public void start(int bpm) {
		this.bpm = bpm;
		// TODO Transform BPM in some appropriate commands

		api.setMode(TwoBMode.MILK);
		api.setChannelOutPut(TwoBChannel.C, 42);
		api.setChannelOutPut(TwoBChannel.D, 69);
	}

	public void stop() {
		api.kill();
	}

}
