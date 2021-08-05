import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSALibrary {

    public static BigInteger[] extendedEuclidean(BigInteger a, BigInteger b) {
        if(a.compareTo(b) == 1) {
            if (b.compareTo(BigInteger.ZERO) == 0) {
                BigInteger x = BigInteger.ONE;
                BigInteger y = BigInteger.ZERO;
                BigInteger d = a;
                BigInteger[] result = new BigInteger[]{x, y, d};
                return result;
            }
            BigInteger[] tempResult = extendedEuclidean(b, a.mod(b));
            BigInteger x = tempResult[1];
            BigInteger y = tempResult[0].subtract(a.divide(b).multiply(tempResult[1]));
            BigInteger d = tempResult[2];
            BigInteger[] result = new BigInteger[]{x, y, d};
            return result;
        }
        else
            return new BigInteger[]{BigInteger.ONE, BigInteger.ZERO, a};
    }

    public static BigInteger modInverse(BigInteger a, BigInteger b){
        BigInteger[] result;
        if(a.compareTo(b) < 0) {
            result = extendedEuclidean(b, a);
        }
        else {
            result = extendedEuclidean(a, b);
        }
        if(result[2].compareTo(BigInteger.ONE) == 0) {
            return (result[1].add(a)).mod(a);
        }
        else {
            return BigInteger.valueOf(-1);
        }
    }

    public static List<BigInteger> encryptText(String text, BigInteger e, BigInteger n) {

        List<String> chunks = RSALibrary.chunkMessage(text, 7);
        List<BigInteger> plainAsciiChunks = new ArrayList<>();
        List<BigInteger> cypherAsciiChunks = new ArrayList<>();

        // turns text chunks into ascii chunks
        for (String chunk: chunks) {
            BigInteger chunkPlainAscii = new BigInteger(chunk.getBytes(StandardCharsets.US_ASCII));
            plainAsciiChunks.add(chunkPlainAscii);
        }

        // encrypts ascii chunks into cypher chunks
        for(BigInteger plainAsciiChunk: plainAsciiChunks){
            cypherAsciiChunks.add(RSALibrary.modExp(plainAsciiChunk, e, n));
        }
        return cypherAsciiChunks;
    }

    public static List<String> decryptText(List<BigInteger> cypherText, BigInteger d, BigInteger n) {

        List<String> plainTextChunks = new ArrayList<>();

        for (BigInteger cypherAsciiChunk: cypherText) {
            BigInteger plainAsciiChunk = RSALibrary.modExp(cypherAsciiChunk, d, n);
            plainTextChunks.add(new String(plainAsciiChunk.toByteArray()));
        }

        return plainTextChunks;
    }


    public static BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) >= 0 || a.compareTo(b) >= 0) {
            if (b.compareTo(BigInteger.ZERO) == 0) {
                return a;
            }
            return gcd(b, a.mod(b));
        }
        return new BigInteger("-1");
    }

    public static BigInteger modExp(BigInteger x, BigInteger y, BigInteger N) {
        if (y.compareTo(BigInteger.ZERO) == 0) return BigInteger.ONE;
            BigInteger z = modExp(x, y.divide(BigInteger.TWO), N);
        if (y.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return z.multiply(z).mod(N);
        } else {
            return z.multiply(z).multiply(x).mod(N);
        }
    }

    public static BigInteger probablePrime(int bitLength, Random random) {

        while (true) {
            BigInteger probablePrimeNumber = new BigInteger(bitLength, random); // generate a random big integer with specified bitlength

            if (isProbablePrime(probablePrimeNumber)) {
                return probablePrimeNumber;
            }
        }
    }

    public static boolean isProbablePrime(BigInteger probablePrimeNumber) {

        boolean isComposite = false;

        // this is Fermats test, 99.99999999999% confirms that the number is prime, the same probability of not being hit by lighting
        for (int i = 0; i < 100; i++) {

            Random random = new Random(System.currentTimeMillis());
            BigInteger randomNumber = new BigInteger(64, random); // generate a random big integer with bitlength
            BigInteger a = probablePrimeNumber.subtract(randomNumber).abs();

            BigInteger aModCheck = modExp(a, probablePrimeNumber.subtract(BigInteger.ONE), probablePrimeNumber);

            if (!aModCheck.equals(BigInteger.ONE)) {
                isComposite = true;
                break;
            }
        }

        if (!isComposite) {
            return true;
        } else {
            return false;
        }

    }

    public static BigInteger nextProbablePrime(BigInteger num){
        BigInteger temp = num.add(BigInteger.ONE);
        if(!isProbablePrime(num))
            num = nextProbablePrime(temp.add(BigInteger.ONE));
        return num;
    }


    public static List<BigInteger> factorizeN(BigInteger N){
        BigInteger startPrime = BigInteger.TWO;
        List<BigInteger> factors = new ArrayList<>();
        while(startPrime.compareTo(N.sqrt()) < 0){
            if(N.mod(startPrime).equals(BigInteger.ZERO)) {
                BigInteger p = startPrime;
                BigInteger q = N.divide(p);
                factors.add(p);
                factors.add(q);
            }
            startPrime = nextProbablePrime(startPrime.add(BigInteger.ONE));
        }
        return factors;
    }

    public static BigInteger[] generateKeys() {

        BigInteger[] keysArray = new BigInteger[5];

        Random randomOne = new Random(System.currentTimeMillis());
        Random randomTwo = new Random(System.currentTimeMillis() * 10);
        Random randomThree = new Random(System.currentTimeMillis() * 100);

        BigInteger p = RSALibrary.probablePrime(32, randomOne);
        BigInteger q = RSALibrary.probablePrime(32, randomTwo);

        BigInteger n = p.multiply(q);

        BigInteger pMinusOne = p.subtract(new BigInteger("1"));
        BigInteger qMinusOne = q.subtract(new BigInteger("1"));

        BigInteger modPQ = pMinusOne.multiply(qMinusOne);
        BigInteger e = null;

        boolean isERelativelyPrime = false;

        while(!isERelativelyPrime){
            //e = RSALibrary.probablePrime(32, randomThree);
            e = new BigInteger(16, randomThree);
            if(e.compareTo(n) < 0)
                isERelativelyPrime = isRelativePrime(e, modPQ);
            else
                isERelativelyPrime = false;
        }

        BigInteger d = RSALibrary.modInverse(modPQ, e);

        keysArray[0] = n;
        keysArray[1] = e;
        keysArray[2] = d;

        return keysArray;
    }

    public static boolean isRelativePrime(BigInteger num1, BigInteger num2){
        BigInteger GCD = RSALibrary.gcd(num1, num2);
        if(GCD.equals(BigInteger.ONE))
            return true;
        else
            return false;
    }

    public static List<String> chunkMessage(String message, int size)
    {
        List<String> chunks = new ArrayList<String>();
        int index = 0;
        while (index < message.length()) {
            chunks.add(message.substring(index, Math.min(index + size,message.length())));
            index += size;
        }
        return chunks;
    }

    public static String plainAsciiMessage(List<String> chunks)
    {
        String message = "";
        for (String chunk: chunks) {
            message += chunk;
        }
        return message;
    }

    public static String encodedAsciiMessage(List<BigInteger> chunks)
    {
        StringBuilder message = new StringBuilder();
        for (BigInteger chunk: chunks) {
            message.append(new String(chunk.toByteArray(), StandardCharsets.US_ASCII).replaceAll("[\\x00-\\x1F\\x7F]", ""));
        }
        return message.toString();
    }
}