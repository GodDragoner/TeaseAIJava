package me.goddragon.teaseai.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import devices.TwoB.TwoBMode;
import estimAPI.Channel;
import estimAPI.EstimAPI;
import estimAPI.Mode;

public class EstimState {

	private List<Mode> estimEnabledModes = null;
	private Map<Channel, Integer> channelValues = new HashMap<Channel, Integer>();

	private List<Mode> string2List(String string) {
		if (string.isEmpty()) {
			return new ArrayList<Mode>();
		} else {
			return Stream.of(string.split(",")).map(mode -> TwoBMode.valueOf(mode)).collect(Collectors.toList());
		}
	}

	// Save the values of channel A and B
	public void savePower(EstimAPI api) {
		List<Channel> channels = api.getChannels();
		Channel channelA = channels.get(0);
		channelValues.put(channelA, channelA.getValue());
		Channel channelB = channels.get(1);
		channelValues.put(channelB, channelB.getValue());
	}

	public void restorePower(EstimAPI api) {
		for (Channel c : channelValues.keySet()) {
			if (c.getID() == 0 || c.getID() == 1) {
				api.setChannelOutPut(c, channelValues.get(c));
			}
		}
	}

	public List<Mode> getEstimEnabledModes() {
		return estimEnabledModes;
	}

	public Map<Channel, Integer> getEstimChannelValues() {
		return channelValues;
	}

	public void setEnabledModes(String enabledModes) {
		estimEnabledModes = string2List(enabledModes);

	}

}
