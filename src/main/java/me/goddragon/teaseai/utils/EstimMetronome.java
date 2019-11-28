package me.goddragon.teaseai.utils;

import devices.TwoB.TwoBChannel;
import devices.TwoB.TwoBMode;
import estimAPI.EstimAPI;
import estimAPI.Mode;
import me.goddragon.teaseai.TeaseAI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			.collect(Collectors.toSet());

	public EstimMetronome() {
		api = TeaseAI.application.getSession().getEstimAPI();
		Objects.requireNonNull(api, "Can't get the instance of EstimAPI");
		estimState = TeaseAI.application.getSession().getEstimState();
	}


	public void start(int bpm) {
		this.bpm = bpm;
		// TODO Transform BPM in some appropriate commands

		List<Mode> enabledModes = estimState.getEstimEnabledModes();
		Mode mode = enabledModes.get(random.nextInt(enabledModes.size()));

		api.setMode(mode);

		int bpmMin = TeaseAI.application.ESTIM_METRONOME_BPM_MIN.getInt();
		int bpmMax = TeaseAI.application.ESTIM_METRONOME_BPM_MAX.getInt();
		int channelCMin = TeaseAI.application.ESTIM_CHANNEL_C_MIN.getInt();
		int channelCMax = TeaseAI.application.ESTIM_CHANNEL_C_MAX.getInt();
		int channelDMin = TeaseAI.application.ESTIM_CHANNEL_D_MIN.getInt();
		int channelDMax = TeaseAI.application.ESTIM_CHANNEL_D_MAX.getInt();

		// Invert output if we are in a Special Mode
		if (SPECIAL_MODES.contains(mode)) {
			int valueC = bpmToOutput(bpm, bpmMin, bpmMax, channelCMin, channelCMax, true);
			int valueD = bpmToOutput(bpm, bpmMin, bpmMax, channelDMin, channelDMax, true);

			api.setChannelOutPut(TwoBChannel.C, valueC);
			api.setChannelOutPut(TwoBChannel.D, valueD);
		} else {
			int valueC = bpmToOutput(bpm, bpmMin, bpmMax, channelCMin, channelCMax, false);
			int valueD = RandomUtils.randInt(channelDMin, channelDMax);

			api.setChannelOutPut(TwoBChannel.C, valueC);
			api.setChannelOutPut(TwoBChannel.D, valueD);
		}

		// Restore Power, when enabled, else choose power in random interval
		if (TeaseAI.application.ESTIM_METRONOME_USER_CONTROLS_POWER.getBoolean()) {
			estimState.restorePower(api);
		} else {
			int channelAMin = TeaseAI.application.ESTIM_CHANNEL_A_MIN.getInt();
			int channelAMax = TeaseAI.application.ESTIM_CHANNEL_A_MAX.getInt();
			int valueA = RandomUtils.randInt(channelAMin, channelAMax);

			int channelBMin = TeaseAI.application.ESTIM_CHANNEL_B_MIN.getInt();
			int channelBMax = TeaseAI.application.ESTIM_CHANNEL_B_MAX.getInt();
			int valueB = RandomUtils.randInt(channelBMin, channelBMax);

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
		BigDecimal input = new BigDecimal(bpm);
		BigDecimal bpm_min = new BigDecimal(bpm_start);
		BigDecimal bpm_max = new BigDecimal(bpm_end);
		BigDecimal output_min = new BigDecimal(output_start);
		BigDecimal output_max = new BigDecimal(output_end);

		// transform input range to output range
		input = input.min(bpm_max);
		BigDecimal output = (input.subtract(bpm_min)).divide(bpm_max.subtract(bpm_min), 2, RoundingMode.HALF_UP)
				.multiply(output_max.subtract(output_min)).add(output_min);

		if (invert_output) {
			output = output.subtract(output_max.add(output_min)).abs();
		}

		return output.setScale(0, RoundingMode.HALF_UP).intValue();
	}

}
