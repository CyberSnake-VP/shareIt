package example.exception;

import java.util.List;

public record ErrorResponse(String error, List<ViolationResponse> violations) {
}
