package cz.kinst.jakub.diploma.jdeecoudptest;

import android.app.Application;
import android.content.Context;

/**
 * Created by jakubkinst on 19/11/14.
 */
public class ConvoyApplication extends Application {

	private static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = this;
	}

	public static Context getsContext() {
		return sContext;
	}
}
