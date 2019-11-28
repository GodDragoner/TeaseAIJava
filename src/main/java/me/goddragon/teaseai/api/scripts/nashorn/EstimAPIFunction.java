package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

import estimAPI.EstimAPI;

/**
 * Created by xman2B on 07.07.2019.
 */
public class EstimAPIFunction extends CustomFunction {

	public EstimAPIFunction() {
		super("estimAPI");
	}

	@Override
	public boolean isFunction() {
		return true;
	}

	@Override
	public Object call(Object object, Object... args) {
		super.call(object, args);

		if (args.length == 1) {
			EstimAPI api = TeaseAI.application.getSession().getEstimAPI();
			return api.execute(args[0].toString());

		}

		TeaseLogger.getLogger().log(Level.SEVERE,
				getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
		return null;
	}
}
