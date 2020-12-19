import java.util.ArrayList;

public class Hash {
    String hash;
    String password;
    String verifiedHashType;
    ArrayList<String> possibleHashTypes;

    protected Hash(String hash) {
        this.hash = hash;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected void setVerifiedHashType(String verifiedHashType) {
        this.verifiedHashType = verifiedHashType;
    }

    protected void setPossibleHashTypes(ArrayList<String> possibleHashTypes) {
        this.possibleHashTypes = possibleHashTypes;
    }

    public String toString() {
        return hash + ":" + verifiedHashType + ":" + password;
    }
}