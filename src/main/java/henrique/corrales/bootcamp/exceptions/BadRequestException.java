package henrique.corrales.bootcamp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{

	public BadRequestException() {

		super("Unsupported file extension.");
	}

	private static final long serialVersionUID = 1L;
}