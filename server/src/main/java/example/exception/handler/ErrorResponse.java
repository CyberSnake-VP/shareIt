package example.exception.handler;

import java.util.List;

public record ErrorResponse(String error, List<ViolationResponse> violations) {
}
