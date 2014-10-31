package cz.kinst.jakub.diploma.convoy.model;

/**
 * Created by jakubkinst on 31/10/14.
 */
public class LogEvent {
	private final String message;

	public LogEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
