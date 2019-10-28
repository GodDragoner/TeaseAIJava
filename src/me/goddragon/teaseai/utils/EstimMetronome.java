package me.goddragon.teaseai.utils;

import estimAPI.EstimAPI;
import estimAPI.Mode;
import me.goddragon.teaseai.TeaseAI;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class EstimMetronome {
	private int bpm;
	private EstimAPI api;
	private EstimState estimState;
	private Random random = new Random();

	private static final Set<Mode> SPECIAL_MODES = Stream
			.of(TwoBMode.THROB, TwoBMode.THRUST, TwoBMode.CYCLE, TwoBMode.TWIST)
			.collect(Collectors.toUnmodifiableSet());

	public EstimMetronome() {
		api = TeaseAI.application.getSession().getEstimAPI();
		Objects.requireNonNull(api, "Can't get the instance of EstimAPI");
		estimState = TeaseAI.application.getSession().getEstimState();
	}


	public void start(int bpm) {
		this.bpm = bpm;
		// TODO Transform BPM in some appropriate commands

		var enabledModes = estimState.getEstimEnabledModes();
		var mode = enabledModes.get(random.nextInt(enabledModes.size()));

		api.setMode(mode);

		var bpmMin = TeaseAI.application.ESTIM_METRONOME_BPM_MIN.getInt();
		var bpmMax = TeaseAI.application.ESTIM_METRONOME_BPM_MAX.getInt();
		var channelCMin = TeaseAI.application.ESTIM_CHANNEL_C_MIN.getInt();
		var channelCMax = TeaseAI.application.ESTIM_CHANNEL_C_MAX.getInt();
		var channelDMin = TeaseAI.application.ESTIM_CHANNEL_D_MIN.getInt();
		var channelDMax = TeaseAI.application.ESTIM_CHANNEL_D_MAX.getInt();

		// Invert output if we are in a Special Mode
		if (SPECIAL_MODES.contains(mode)) {
			var valueC = bpmToOutput(bpm, bpmMin, bpmMax, channelCMin, channelCMax, true);
			var valueD = bpmToOutput(bpm, bpmMin, bpmMax, channelDMin, channelDMax, true);

			api.setChannelOutPut(TwoBChannel.C, valueC);
			api.setChannelOutPut(TwoBChannel.D, valueD);
		} else {
			var valueC = bpmToOutput(bpm, bpmMin, bpmMax, channelCMin, channelCMax, false);
			var valueD = RandomUtils.randInt(channelDMin, channelDMax);

			api.setChannelOutPut(TwoBChannel.C, valueC);
			api.setChannelOutPut(TwoBChannel.D, valueD);
		}

		// Restore Power, when enabled, else choose power in random interval
		if (TeaseAI.application.ESTIM_METRONOME_USER_CONTROLS_POWER.getBoolean()) {
			estimState.restorePower(api);
		} else {
			var channelAMin = TeaseAI.application.ESTIM_CHANNEL_A_MIN.getInt();
			var channelAMax = TeaseAI.application.ESTIM_CHANNEL_A_MAX.getInt();
			var valueA = RandomUtils.randInt(channelAMin, channelAMax);

			var channelBMin = TeaseAI.application.ESTIM_CHANNEL_B_MIN.getInt();
			var channelBMax = TeaseAI.application.ESTIM_CHANNEL_B_MAX.getInt();
			var valueB = RandomUtils.randInt(channelBMin, channelBMax);

			api.setChannelOutPut(TwoBChannel.A, valueA);
			api.setChannelOutPut(TwoBChannel.B, valueB);
		}
	}

	public void stop() {
		estimState.savePower(api);
		api.kill();
	}

	/*
	 * Calculate the output of channel C
	 */
	private int bpmToOutput(int bpm, int bpm_start, int bpm_end, int output_start, int output_end,
			boolean invert_output) {
		var input = new BigDecimal(bpm);
		var bpm_min = new BigDecimal(bpm_start);
		var bpm_max = new BigDecimal(bpm_end);
		var output_min = new BigDecimal(output_start);
		var output_max = new BigDecimal(output_end);

		// transform input range to output range
		input = input.min(bpm_max);
		var output = (input.subtract(bpm_min)).divide(bpm_max.subtract(bpm_min), 2, RoundingMode.HALF_UP)
				.multiply(output_max.subtract(output_min)).add(output_min);

		if (invert_output) {
			output = output.subtract(output_max.add(output_min)).abs();
		}

		return output.setScale(0, RoundingMode.HALF_UP).intValue();
	}

}
