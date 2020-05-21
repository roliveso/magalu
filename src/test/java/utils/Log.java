package utils;

import org.apache.log4j.Logger;

import enums.LogType;

public class Log {

	private static final org.apache.log4j.Logger log = Logger.getLogger(Log.class);

	/**
	 * Inserir uma mensagem de log nas saidas definidas
	 * 
	 * @param message Mensagem do log
	 * @param type    Tipo de log
	 */
	public static void sendLog(String message, LogType type) {

		switch (type) {
		case ERROR:
			log.error(message);
			break;
		case INFO:
			log.info(message);
			break;
		case WARN:
			log.warn(message);
			break;
		}
	}
}
