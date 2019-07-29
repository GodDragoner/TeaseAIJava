package me.goddragon.teaseai.utils;

import estimAPI.Channel;
import estimAPI.EstimAPI;
import estimAPI.Mode;
import me.goddragon.teaseai.TeaseAI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import devices.TwoB.TwoBChannel;
import devices.TwoB.TwoBMode;

/**
 * Created by xman2B on 07.07.2019.
 */
public class EstimApi {
	private int bpm;
	private EstimAPI api;
	private List<Mode> enabledModes;
	private Map<Channel, Integer> channelValues;
	private Random random = new Random();
	
	private static final Set<Mode> SPECIAL_MODES = Stream.of(TwoBMode.THROB, TwoBMode.THRUST, TwoBMode.CYCLE, TwoBMode.TWIST).collect(Collectors.toUnmodifiableSet());

	
	public EstimApi() {
		api = TeaseAI.application.getSession().getEstimAPI();
		Objects.requireNonNull(api, "Can't get the instance of EstimAPI");
		enabledModes = TeaseAI.application.getSession().getEstimEnabledModes();
		channelValues = TeaseAI.application.getSession().getEstimChannelValues();
	}
	
	
	// Save the values of channel A and B
	private void savePower() {
		List<Channel> channels = api.getChannels(); 
		Channel channelA = channels.get(0);
		channelValues.put(channelA, channelA.getValue());
		Channel channelB = channels.get(1);
		channelValues.put(channelB, channelB.getValue());
	}
	
	private void restorePower() {
		for (Channel c: channelValues.keySet()) {
			if (c.getID() == 0 || c.getID() == 1) {
				api.setChannelOutPut(c, channelValues.get(c));
			}
		}
	}
	

	public void start(int bpm) {
		this.bpm = bpm;
		// TODO Transform BPM in some appropriate commands

		var mode = enabledModes.get(random.nextInt(enabledModes.size()));
		
		api.setMode(mode);

		
		// Invert output if we are in a Special Mode
		if (SPECIAL_MODES.contains(mode)) {
			var valueC = bpmToOutput(bpm, TeaseAI.application.ESTIM_METRONOME_BPM_MIN.getInt(), TeaseAI.application.ESTIM_METRONOME_BPM_MAX.getInt(), TeaseAI.application.ESTIM_CHANNEL_C_MIN.getInt(), TeaseAI.application.ESTIM_CHANNEL_C_MAX.getInt(), true);
			var valueD = bpmToOutput(bpm, TeaseAI.application.ESTIM_METRONOME_BPM_MIN.getInt(), TeaseAI.application.ESTIM_METRONOME_BPM_MAX.getInt(), TeaseAI.application.ESTIM_CHANNEL_D_MIN.getInt(), TeaseAI.application.ESTIM_CHANNEL_D_MAX.getInt(), true);
			
			api.setChannelOutPut(TwoBChannel.C, valueC);
			api.setChannelOutPut(TwoBChannel.D, valueD);
		}
		else {
			var valueC = bpmToOutput(bpm, TeaseAI.application.ESTIM_METRONOME_BPM_MIN.getInt(), TeaseAI.application.ESTIM_METRONOME_BPM_MAX.getInt(), TeaseAI.application.ESTIM_CHANNEL_C_MIN.getInt(), TeaseAI.application.ESTIM_CHANNEL_C_MAX.getInt(), false);
			var valueD = RandomUtils.randInt(TeaseAI.application.ESTIM_CHANNEL_D_MIN.getInt(), TeaseAI.application.ESTIM_CHANNEL_D_MAX.getInt());
			
			api.setChannelOutPut(TwoBChannel.C, valueC);
			api.setChannelOutPut(TwoBChannel.D, valueD);
		}
		
		// Restore Power, when enabled, else choose power in random interval
		if (TeaseAI.application.ESTIM_METRONOME_USER_CONTROLS_POWER.getBoolean()) {
			restorePower();
		}
		else {
			var valueA = RandomUtils.randInt(TeaseAI.application.ESTIM_CHANNEL_A_MIN.getInt(), TeaseAI.application.ESTIM_CHANNEL_A_MAX.getInt());
			var valueB = RandomUtils.randInt(TeaseAI.application.ESTIM_CHANNEL_B_MIN.getInt(), TeaseAI.application.ESTIM_CHANNEL_B_MAX.getInt());
			api.setChannelOutPut(TwoBChannel.A, valueA);
			api.setChannelOutPut(TwoBChannel.B, valueB);
		}
	}

	public void stop() {
		savePower();
		api.kill();
	}
	
	/*
	 * Calculate the output of channel C
	 */
	private int bpmToOutput(int bpm, int bpm_start, int bpm_end, int output_start, int output_end, boolean invert_output) {
		var input = new BigDecimal(bpm);
		var bpm_min = new BigDecimal(bpm_start);
		var bpm_max = new BigDecimal(bpm_end);
		var output_min = new BigDecimal(output_start);
		var output_max = new BigDecimal(output_end);
		
		// transform input range to output range
		input = input.min(bpm_max);
		var output = (input.subtract(bpm_min)).divide(bpm_max.subtract(bpm_min), 2, RoundingMode.HALF_UP).multiply(output_max.subtract(output_min)).add(output_min);

		if (invert_output) {
			output = output.subtract(output_max.add(output_min)).abs();
		}
		
		return output.setScale(0, RoundingMode.HALF_UP).intValue();
	}

}
