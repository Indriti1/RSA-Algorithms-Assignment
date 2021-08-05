import javax.swing.plaf.basic.BasicColorChooserUI;
import java.beans.BeanInfo;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // generate keys for bob
        BigInteger[] bobKeys = RSALibrary.generateKeys();
        User bob = new User(bobKeys[0], bobKeys[1], bobKeys[2]);

        // generate keys for alice
        BigInteger[] aliceKeys = RSALibrary.generateKeys();
        User alice = new User(aliceKeys[0], aliceKeys[1], aliceKeys[2]);

        System.out.println("Bob says: Hello World, my public key is N=" + bob.getPublicN() + " and E=" + bob.getPublicE());
        System.out.println("Alice says: Hello World, my public key is N=" + alice.getPublicN() + " and E=" + alice.getPublicE());

        // encrypt alices message using bobs public key
        System.out.print("Alice says: ");
        String aliceText = input.nextLine();  // Read user input
        List<BigInteger> aliceCypher = RSALibrary.encryptText(aliceText, bob.getPublicE(), bob.getPublicN());

        System.out.println("Charlie reads (encoded text): " + RSALibrary.encodedAsciiMessage(aliceCypher));

        // decrypt alices message using bobs private key
        List<String> alicePlainText = RSALibrary.decryptText(aliceCypher, bob.getPrivateD(), bob.getPublicN());
        System.out.println("Bob reads (after decoding): " + RSALibrary.plainAsciiMessage(alicePlainText));

        // encrypt bobs message using alices public key
        System.out.print("Bob says: ");
        String bobText = input.nextLine();  // Read user input

        List<BigInteger> bobCypher = RSALibrary.encryptText(bobText, alice.getPublicE(), alice.getPublicN());
        System.out.println("Charlie reads (encoded text): " + RSALibrary.encodedAsciiMessage(bobCypher));


        // decrypt bobs message using alices private key
        List<String> bobPlainText = RSALibrary.decryptText(bobCypher, alice.getPrivateD(), alice.getPublicN());
        System.out.println("Alice reads (after decoding): " + RSALibrary.plainAsciiMessage(bobPlainText));

    }
}
