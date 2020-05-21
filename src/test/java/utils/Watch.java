package utils;

import java.util.Date;

public class Watch {

	/**
	 * Pegar o TimeStamp atual para o calculo de tempo de execucao de um caso
	 * 
	 * @return TimeStamp atual
	 */
	public static long getCurrentTime() {

		return new Date().getTime();
	}

	/**
	 * Calcular a duracao de execucao do caso de teste
	 * 
	 * @param initStopwatch
	 *            TimeStamp em que a execucao do caso iniciou
	 * @param finishStopwatch
	 *            TimeStamp em que a execucao do caso finalizou
	 * @return Tempo de execucao total do caso de teste
	 */
	public static String stopWatchElapsedTime(long initStopwatch,
			long finishStopwatch) {

		long milli = finishStopwatch - initStopwatch;
		int[] executionTime = new int[] { 0, 0, 0, 0 };
		executionTime[0] = (int) (milli / 3600000);
		executionTime[1] = (int) (milli / 60000) % 60;
		executionTime[2] = (int) (milli / 1000) % 60;
		executionTime[3] = (int) (milli) % 1000;
		return String.format("%02d:%02d:%02d.%03d", executionTime[0],
				executionTime[1], executionTime[2], executionTime[3]);
	}
}
