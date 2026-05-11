package example.user;

public record UpdateUserRequest(
        String name,
        String email
) {
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}
