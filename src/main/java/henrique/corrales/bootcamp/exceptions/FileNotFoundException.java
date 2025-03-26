package henrique.corrales.bootcamp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileNotFoundException extends RuntimeException{

	public FileNotFoundException(String ex) {
		super(ex);
	}
	public FileNotFoundException(String ex, Throwable cause) {super(ex, cause);}

	private static final long serialVersionUID = 1L;
}