package cyou.mrd.game.mail;

public class MailException extends Exception {

	private static final long serialVersionUID = 1L;

	public MailException(String message) {
		super(message);
	}
	
	public MailException(Exception cause) {
		super(cause);
	}
}
