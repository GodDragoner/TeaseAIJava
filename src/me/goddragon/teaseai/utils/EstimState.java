package me.goddragon.teaseai.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import devices.TwoB.TwoBMode;
import estimAPI.Channel;
import estimAPI.Mode;

public class EstimState {

	private List<Mode> estimEnabledModes = null;
	private Map<Channel, Integer> estimChannelValues = new HashMap<Channel, Integer>();

	private List<Mode> string2List(String string) {
		if (string.isEmpty()) {
			return new ArrayList<Mode>();
		} else {
			return Stream.of(string.split(",")).map(mode -> TwoBMode.valueOf(mode)).collect(Collectors.toList());
		}
	}

	public List<Mode> getEstimEnabledModes() {
		return estimEnabledModes;
	}

	public Map<Channel, Integer> getEstimChannelValues() {
		return estimChannelValues;
	}

	public void setEnabledModes(String enabledModes) {
		estimEnabledModes = string2List(enabledModes);

	}

}
