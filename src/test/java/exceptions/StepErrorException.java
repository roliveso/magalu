package exceptions;

public class StepErrorException extends Exception {

	private static final long	serialVersionUID	= 2318517712706899836L;
	private static String		DEFAULT_MSG			= "Erro no passo: ";

	/**
	 * Excecao lancada quando ocorre um erro em um passo
	 * 
	 * @param step
	 *            Passo onde ocorreu o erro
	 * @param errorMsg
	 *            Mensagem do erro
	 */
	public StepErrorException(String step, String errorMsg){
		super(DEFAULT_MSG + step + " - " + errorMsg);
	}
}
