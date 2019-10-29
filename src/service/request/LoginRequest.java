package service.request;

/** LoginRequest represents a request to log in
 */
public class LoginRequest {
    /** Constructs an empty <code>LoginRequest</code>
     */
    public LoginRequest() {}

    /** Constructs a populated <code>LoginRequest</code>
     * 
     * @param Username the username of the user logging in
     * @param Password the password of the user logging in
     */
    public LoginRequest(String Username, String Password) {
        userName = Username;
        password = Password;
    }

    private String userName;
    private String password;

    /**
     * @return the username
     */
    public String getUsername() {
        return userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Verifies that the fields in the login request are valid:
     * Password and Username must not be null or empty;
     * @return a boolean indicating validity of the request object
     */
    public boolean isValid() {
        if (userName == null || userName.equals("")) return false;
        if (password == null || password.equals("")) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o) return true;

        LoginRequest that = (LoginRequest)o;

        if (!this.getPassword().equals(that.getPassword())) return false;
        if (!this.getUsername().equals(that.getUsername())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode() ^ this.getPassword().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Username: %s  Password: %s",
            (this.getUsername() == null) ? "<null>" : this.getUsername(),
            (this.getPassword() == null) ? "<null>" : this.getPassword()));
        return sb.toString();
    }
};
