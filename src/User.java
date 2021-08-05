import java.math.BigInteger;

public class User {


    public BigInteger publicN;
    public BigInteger publicE;
    private BigInteger privateD;

    public User(BigInteger publicN, BigInteger publicE, BigInteger privateD) {
        this.publicN = publicN;
        this.publicE = publicE;
        this.privateD = privateD;

    }

    public BigInteger getPrivateD() {
        return privateD;
    }

    public void setPrivateD(BigInteger privateD) {
        this.privateD = privateD;
    }

    public BigInteger getPublicN() {
        return publicN;
    }

    public void setPublicN(BigInteger publicN) {
        this.publicN = publicN;
    }

    public BigInteger getPublicE() {
        return publicE;
    }

    public void setPublicE(BigInteger publicE) {
        this.publicE = publicE;
    }
}
