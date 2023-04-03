import java.awt.image.*;
import java.io.*;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @author My Tran
 * @version 1.0
 * @description implements methods and functionality of Shamir's Secret Sharing
 *              algorithm on images
 */
public class SSS {
    /* Data members */
    private BufferedImage secretImage; // Secret image to create shares out of
    private Share shareArray[]; // Array holding share images and x value keys
    private int shareKeyX[];

    private int coefficients[][]; // random coefficient values for encrytion function
    public int primeField; // prime field made public for GUI
    private Random rng; // random number generator for encryption and value generation
    public int k, n; // SSS scheme values. k-1 is the polynomial degree. n is the number of shares

    /**
     * Constructor for SSS
     * 
     * @param secret    secret passed in through a buffered image
     * @param degree    polynomial degree is k-1 for scheme
     * @param numShares number of shares produced from scheme
     */
    public SSS(BufferedImage secret, int degree, int numShares) {
        secretImage = secret;
        k = degree;
        n = numShares;
    }

    /* Member initializer methods */

    /**
     * Initiallize the random number generator member
     */
    private void InitializeRNG() {
        // current time in ms for better pseudo-random value
        long seed = System.currentTimeMillis();
        rng = new Random(seed);
    }

    /**
     * Generate random coefficient values
     */
    private void GenerateRandomCoefficients() {
        // k-1 coefficients are needed for (k, n) SSS scheme for each byte
        coefficients = new int[secretImage.getWidth() * secretImage.getHeight() / 4][k - 1];
        for (int i = 0; i < coefficients.length; i++) {
            for (int j = 0; j < k - 1; j++) {
                coefficients[i][j] = Modulo.Mod(rng.nextInt(), 249) + 1;
            }
        }
    }

    /**
     * Randomly generate keys needed for each share
     */
    private void GenerateRandomKeys() {
        shareKeyX = new int[n]; // n keys are needed for n shares

        for (int i = 0; i < shareKeyX.length; i++) {
            shareKeyX[i] = rng.nextInt(primeField);
        }
    }

    /**
     * Choose primeField q to be randomly from list of primes between 0 - 255
     */
    private void GeneratePrimeField() {
        // possible byte values are between 0 and 255
        primeField = 251;// primePixelValues[Math.abs(rng.nextInt()) % primePixelValues.length];
    }

    /**
     * Creates share images from secret image
     * 
     * @throws IOException
     */
    public void CreateShares() throws IOException {
        shareArray = new Share[n]; // memory to hold shares

        // Initiallize members that are part of encryption
        InitializeRNG();
        GeneratePrimeField();
        GenerateRandomCoefficients();
        GenerateRandomKeys();

        // create n share images
        for (int i = 0; i < n; i++) {
            // Create key pair in share object
            shareArray[i] = new Share();

            // Create image for share image
            shareArray[i].shareImage = new BufferedImage(secretImage.getWidth(), secretImage.getHeight(),
                    secretImage.getType());
            shareArray[i].keyX = shareKeyX[i]; // Store x key of share

            // Apply secret sharing function with given share and corresponding x key
            EncryptionFunction(shareArray[i]);
            // GenerateRandomCoefficients();
        }
    }

    /**
     * Reconstruct secret image using given shares in parameter (for (2,n) SSS
     * scheme)
     * 
     * @param selectedShares array containing shares used to decrypt
     * @return
     * @throws IOException
     */
    public BufferedImage DecryptSecret(Share selectedShares[]) throws IOException {
        // Read bytes array from share 1
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ImageIO.write(selectedShares[0].shareImage, "bmp", bOut);
        byte shareBytes0[] = bOut.toByteArray();
        bOut.close();

        // Read bytes array from share 2
        bOut = new ByteArrayOutputStream();
        ImageIO.write(selectedShares[1].shareImage, "bmp", bOut);
        byte shareBytes1[] = bOut.toByteArray();
        bOut.close();

        // Stores bytes for reconstructed image
        byte decryptedBytes[] = new byte[shareBytes0.length];

        // Grab keys for each selected share
        int key1 = selectedShares[0].keyX;
        int key2 = selectedShares[1].keyX;

        // temp values for decryption calculation
        int temp[] = new int[shareBytes0.length];
        int y1, y2;

        // preserved header
        for (int i = 0; i < 54; i++) {
            decryptedBytes[i] = shareBytes0[i];
        }

        for (int i = 54; i < shareBytes0.length; i++) {
            // Bias negatives by 256 to account for byte values greater than 127
            y1 = (int) shareBytes0[i] + ((shareBytes0[i] < 0) ? 256 : 0);
            y2 = (int) shareBytes1[i] + ((shareBytes1[i] < 0) ? 256 : 0);

            // LaGrange interpolation y = f(0) = y1(-x2)/(x1 - x2) + y2(-x1)/(x2 - x1) mod q
            temp[i] = (y1 * (-key2) * Modulo.InverseMod((key1 - key2), primeField)) +
                    (y2 * (-key1) * Modulo.InverseMod((key2 - key1), primeField));

            // apply mod q
            temp[i] = Modulo.Mod(temp[i], primeField);

            // account for twos complement
            if (temp[i] > 127) {
                // unbias by 256 and negate to avoid faulty casting between bytes and ints
                temp[i] = 256 - temp[i];
                decryptedBytes[i] = (byte) temp[i];
                decryptedBytes[i] *= -1;
            } else {
                decryptedBytes[i] = (byte) temp[i];
            }
        }

        // Returns image created from byte stream
        try {
            return ImageIO.read(new ByteArrayInputStream(decryptedBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return empty image otherwise
        return new BufferedImage(secretImage.getWidth(), secretImage.getHeight(), secretImage.getType(), null);
    }

    /**
     * Get the shares created from CreateShares
     * 
     * @return
     */
    public Share[] GetShares() {
        return shareArray;
    }

    /**
     * Returns a downscaled version of a given image
     * 
     * @param source desired image for downscaling
     * @return source downscaled by half
     * @throws IOException
     */
    public BufferedImage Downscale(BufferedImage source) throws IOException {
        int sourceRGB[] = source.getRGB(0, 0, source.getWidth(null), source.getHeight(null), null, 0,
                source.getWidth(null));
        int sourcePixelMap[][] = new int[source.getHeight()][source.getWidth()];

        // fill pixel map
        for (int i = 0, count = 0; i < source.getHeight(); i++) {
            for (int j = 0; j < source.getWidth(); j++, count++) {
                sourcePixelMap[i][j] = sourceRGB[count];
            }
        }

        // downscaling by a factor of two reduces the number of pixels by a scale factor
        // of 4
        int[] scaledRGB = new int[sourceRGB.length / 4];
        long temp = 0;

        for (int i = 0, l = 0; i < source.getHeight() - 1; i += 2) {
            for (int j = 0; j < source.getWidth() - 1; j += 2, l++) {
                // Grab a 2x2 grid of pixels
                long topLeft = (long) sourcePixelMap[i][j]
                        + ((sourcePixelMap[i][j] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);

                long topRight = (long) sourcePixelMap[i][j + 1]
                        + ((sourcePixelMap[i][j + 1] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);

                long bottomLeft = (long) sourcePixelMap[i + 1][j]
                        + ((sourcePixelMap[i + 1][j] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);

                long bottomRight = (long) sourcePixelMap[i + 1][j + 1]
                        + ((sourcePixelMap[i + 1][j + 1] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);

                // To hold a pixel
                int tempByte[] = new int[4];

                // Finding average of alpha values
                tempByte[0] = Modulo.Mod((int) ((((((topLeft >> 24) & 0xff) > 250) ? 250 : ((topLeft >> 24) & 0xff))
                        + ((((topRight >> 24) & 0xff) > 250) ? 250 : ((topRight >> 24) & 0xff))
                        + ((((bottomLeft >> 24) & 0xff) > 250) ? 250 : ((bottomLeft >> 24) & 0xff))
                        + ((((bottomRight >> 24) & 0xff) > 250) ? 250 : ((bottomRight >> 24) & 0xff))) >> 2),
                        primeField);

                // Finding average of red values
                tempByte[1] = Modulo.Mod((int) ((((((topLeft >> 16) & 0xff) > 250) ? 250 : ((topLeft >> 16) & 0xff))
                        + ((((topRight >> 16) & 0xff) > 250) ? 250 : ((topRight >> 16) & 0xff))
                        + ((((bottomLeft >> 16) & 0xff) > 250) ? 250 : ((bottomLeft >> 16) & 0xff))
                        + ((((bottomRight >> 16) & 0xff) > 250) ? 250 : ((bottomRight >> 16) & 0xff))) >> 2),
                        primeField);

                // Finging average of green values
                tempByte[2] = Modulo.Mod((int) ((((((topLeft >> 8) & 0xff) > 250) ? 250 : ((topLeft >> 8) & 0xff))
                        + ((((topRight >> 8) & 0xff) > 250) ? 250 : ((topRight >> 8) & 0xff))
                        + ((((bottomLeft >> 8) & 0xff) > 250) ? 250 : ((bottomLeft >> 8) & 0xff))
                        + ((((bottomRight >> 8) & 0xff) > 250) ? 250 : ((bottomRight >> 8) & 0xff))) >> 2),
                        primeField);

                // Finding average of blue values
                tempByte[3] = Modulo.Mod((int) ((((((topLeft) & 0xff) > 250) ? 250 : ((topLeft) & 0xff))
                        + ((((topRight) & 0xff) > 250) ? 250 : ((topRight) & 0xff))
                        + ((((bottomLeft) & 0xff) > 250) ? 250 : ((bottomLeft) & 0xff))
                        + ((((bottomRight) & 0xff) > 250) ? 250 : ((bottomRight) & 0xff))) >> 2),
                        primeField);

                // Stitch temp bytes together into an ARGB int
                temp = ((long) tempByte[0]) << 24 | ((long) tempByte[1]) << 16 | ((long) tempByte[2]) << 8
                        | (long) tempByte[3];

                // account for twos complement
                if (temp > Integer.MAX_VALUE) {
                    // unbias by max value of unsigned int and negate to avoid faulty casting
                    // between ints and longs
                    temp = (2 * ((long) Integer.MAX_VALUE + 1)) - temp;
                    scaledRGB[l] = (int) temp;
                    scaledRGB[l] *= -1;
                } else {
                    scaledRGB[l] = (int) temp;
                }
            }
        }

        // Apply pixels into image object
        BufferedImage scaledImage = new BufferedImage(source.getWidth() / 2, source.getHeight() / 2, source.getType());

        scaledImage.setRGB(0, 0, scaledImage.getWidth(null), scaledImage.getHeight(null), scaledRGB, 0,
                scaledImage.getWidth(null));
        return scaledImage;
    }

    /**
     * Applies encryption function on secret in a 2x2 pixel block manner to create
     * share
     * 
     * @param share Share containing keyX to create share from
     */
    private void EncryptionFunction(Share share) {
        int secretRGB[] = secretImage.getRGB(0, 0, secretImage.getWidth(null), secretImage.getHeight(null), null, 0,
                secretImage.getWidth(null));
        int secretPixelMap[][] = new int[secretImage.getHeight()][secretImage.getWidth()];

        // fill pixel map
        for (int i = 0, count = 0; i < secretImage.getHeight(); i++) {
            for (int j = 0; j < secretImage.getWidth(); j++, count++) {
                secretPixelMap[i][j] = secretRGB[count];
            }
        }

        int sharePixelMap[][] = new int[secretImage.getHeight()][secretImage.getWidth()];
        int[] shareRGB = new int[secretRGB.length];

        for (int i = 0, l = 0; i < secretImage.getHeight() - 1; i += 2) {
            for (int j = 0; j < secretImage.getWidth() - 1; j += 2, l++) {
                // Getting the 2x2 pixel values as long value usable for calculations
                long tempPixelBlock[][] = new long[2][2];
                tempPixelBlock[0][0] = (long) secretPixelMap[i][j]
                        + ((secretPixelMap[i][j] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);
                tempPixelBlock[0][1] = (long) secretPixelMap[i][j + 1]
                        + ((secretPixelMap[i][j + 1] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);
                tempPixelBlock[1][0] = (long) secretPixelMap[i + 1][j]
                        + ((secretPixelMap[i + 1][j] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);
                tempPixelBlock[1][1] = (long) secretPixelMap[i + 1][j + 1]
                        + ((secretPixelMap[i + 1][j + 1] < 0) ? (2 * ((long) Integer.MAX_VALUE + 1)) : 0);

                long tempByte[] = new long[4];
                // f(x) = s0 + a1*x + a2*x^2 + ... mod q on all pixels in block
                for (int m = 0; m < 2; m++) { // encrypt function
                    for (int p = 0; p < 2; p++) {
                        tempByte[0] = (tempPixelBlock[m][p] >> 24) & 0xFF;// A byte
                        tempByte[1] = (tempPixelBlock[m][p] >> 16) & 0xFF;// R byte
                        tempByte[2] = (tempPixelBlock[m][p] >> 8) & 0xFF; // G byte
                        tempByte[3] = (tempPixelBlock[m][p]) & 0xFF; // B byte

                        // Set any pixel above the prime field to 250 to fit in encryption range
                        for (int q = 0; q < 4; q++) {
                            if (tempByte[q] > 250) {
                                tempByte[q] = 250;
                            }

                            // Apply encrytion function to all pixel bytes
                            for (int r = 1; r < k; r++) {
                                tempByte[q] += (coefficients[l][r - 1] * Math.pow(share.keyX, r));
                            }

                            // Masked to positive value so no need to account for negative mod
                            tempByte[q] %= primeField;

                            // Stitch bytes together
                            tempPixelBlock[m][p] = tempByte[0] << 24 | tempByte[1] << 16 | tempByte[2] << 8
                                    | tempByte[3];

                            // fill in share pixels
                            if (tempPixelBlock[m][p] > Integer.MAX_VALUE) {
                                tempPixelBlock[m][p] = (2 * ((long) Integer.MAX_VALUE + 1)) - tempPixelBlock[0][0];
                                sharePixelMap[i + m][j + p] = (int) tempPixelBlock[0][0];
                                sharePixelMap[i + m][j + p] *= -1;
                            } else {
                                sharePixelMap[i + m][j + p] = (int) tempPixelBlock[0][0];
                            }
                        }
                    }
                }
            }
        }

        // fill pixel map
        for (int i = 0, count = 0; i < secretImage.getHeight(); i++) {
            for (int j = 0; j < secretImage.getWidth(); j++, count++) {
                shareRGB[count] = sharePixelMap[i][j];
            }
        }

        share.shareImage.setRGB(0, 0, secretImage.getWidth(null), secretImage.getHeight(null), shareRGB, 0,
                secretImage.getWidth(null));

    }

    /**
     * Calculates the MAE between a secret image and its decrypted version
     * 
     * @param secret
     * @param decrypted
     * @return
     * @throws IOException
     */
    public long MeanAverageError(BufferedImage secret, BufferedImage decrypted) throws IOException {
        // Write bytes of bmp file to stream and stores in an array
        // ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        // ImageIO.write(secret, "bmp", bOut);
        // byte secretBytes[] = bOut.toByteArray();
        // bOut.close();

        // bOut = new ByteArrayOutputStream();
        // ImageIO.write(decrypted, "bmp", bOut);
        // byte decryptedBytes[] = bOut.toByteArray();
        // bOut.close();

        // long result = 0;

        // for (int i = 0; i < secretBytes.length; i++) {
        // result += Math.abs(secretBytes[i] - decryptedBytes[i]);
        // }

        // return result;
        // }
        // Bytes of secret image
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ImageIO.write(secret, "bmp", bOut);
        byte secretBytes[] = bOut.toByteArray();
        bOut.close();

        // Bytes of decrypted image
        bOut = new ByteArrayOutputStream();
        ImageIO.write(decrypted, "bmp", bOut);
        byte decryptedBytes[] = bOut.toByteArray();
        bOut.close();

        long result = 0;

        //temp values for colors
        int secretA, secretR, secretG, secretB,
                decA, decR, decG, decB;
        for (int i = 0; i < secretBytes.length - 4; i += 4) {
            //Get pixel values for one pixel from secret
            secretA = (int) secretBytes[i] + ((secretBytes[i] < 0) ? 256 : 0);
            secretR = (int) secretBytes[i + 1] + ((secretBytes[i + 1] < 0) ? 256 : 0);
            secretG = (int) secretBytes[i + 2] + ((secretBytes[i + 2] < 0) ? 256 : 0);
            secretB = (int) secretBytes[i + 3] + ((secretBytes[i + 3] < 0) ? 256 : 0);

            //Get pixel values for one pixel from decrypted
            decA = (int) decryptedBytes[i] + ((decryptedBytes[i] < 0) ? 256 : 0);
            decR = (int) decryptedBytes[i + 1] + ((decryptedBytes[i + 1] < 0) ? 256 : 0);
            decG = (int) decryptedBytes[i + 2] + ((decryptedBytes[i + 2] < 0) ? 256 : 0);
            decB = (int) decryptedBytes[i + 3] + ((decryptedBytes[i + 3] < 0) ? 256 : 0);

            // Stitch together the pixels
            long secretPixel = secretA << 24 | secretR << 16 | secretG << 8 | secretB;
            long decPixel = decA << 24 | decR << 16 | decG << 8 | decB;

            // Account for twos complement
            secretPixel = (2 * ((long) Integer.MAX_VALUE + 1)) - secretPixel;
            decPixel = (2 * ((long) Integer.MAX_VALUE + 1)) - decPixel;

            // accumulate abs value of secret and decrypted pixel
            result += (secretPixel - decPixel) * ((secretPixel - decPixel < 0) ? -1 : 1);
        }

        return result;
    }

}