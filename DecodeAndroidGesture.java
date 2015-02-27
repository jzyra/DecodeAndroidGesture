import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;

/**
	* Read sequence gesture for validate Android's pattern lock screen in gesture.key file.
	* @author Jeremy ZYRA
	* @version 1.0
*/
public final class DecodeAndroidGesture {
	/**
		* Constructor of the class.
	*/
	private DecodeAndroidGesture() {
	}

	/**
		* Convert byte array to hexadecimal string.
		* @param hash The byte array
		* @return hexadecimal representation of byte array
	*/
	private static String byteToHex(final byte[] hash) {
 	   Formatter formatter = new Formatter();
	   //Convert to hexadecimal for each byte in hash
 	   for (byte b : hash) {
			formatter.format("%02x", b);
 	   }
 	   String result = formatter.toString();
 	   formatter.close();
 	   return result;
	}

	/**
		* Hash to SHA-1 the gesture code.
		* @param code Code in byte array
		* @return SHA-1 String representation
	*/
	private static String hashCode(final int[] code) {
		byte[] byteArray = new byte[code.length];
		//Convert int array to byte array
		for (int i = 0; i < code.length; ++i) {
			byteArray[i] = (byte) code[i];
		}
		String result = new String("");
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			//Hash code byte array
			sha1.update(byteArray);
			result = byteToHex(sha1.digest());
		} catch (NoSuchAlgorithmException e) {
			System.out.println("[-] Error: " + e.getMessage());
			System.exit(0);
		}
		return result;
	}

	/**
		* Concatenates all elements in array.
		* @param array It's array to concatenate.
		* @return String representation of all elements concatened
	*/
	private static String joinArray(final int[] array) {
		String result = new String("");
		//For each byte in array, add byte in result String.
		for (int i = 0; i < array.length; ++i) {
			result += array[i];
		}
		return result;
	}

	/**
		* Add element in array.
		* @param org It's array target.
		* @param added It's value to added at array.
		* @return Array with the new value
	*/
	private static int[] addElement(final int[] org, final int added) {
		int[] result = Arrays.copyOf(org, org.length + 1);
		result[org.length] = added;
		return result;
	}

	/**
		* Remove element in array.
		* @param arg It's array target.
		* @param value It's value to remove in array
		* @return Array without the value
	*/
	private static int[] deleteElement(final int[] arg, final int value) {
		int[] result = new int[arg.length - 1];
		int count = 0;
		for (int element : arg) {
			if (element != value) {
				result[count] = element;
				count++;
			}
		}
		return result;
	}

	/**
		* Search the gesture code.
		* @param tab1 Array of int elements
		* @param tab2 Array of int elements
		* @param hash SHA-1 hash in gesture.key file
		* @return Code in string format or empty string ifi algorithme don't find code.
	*/
	private static String genHashs(final int[] tab1, final int[] tab2, final String hash) {
		int[] code = {};
		int[] param = {};
		String result = new String("");
		//Browse all possible sequence.
		for (int i = 0; i < tab2.length; ++i) {
			code = tab1;
			code = addElement(code, tab2[i]);
			//If code size is more than 3.
			if (code.length > 3) {
				//If the hash code to sequence is equal to hash in gesture.key, the sequence is found.
				if (hashCode(code).equals(hash)) {
					return joinArray(code);
				}
			}
			param = tab2;
			param = deleteElement(param, tab2[i]);
			result = genHashs(code, param, hash);
			if (!result.equals("")) {
				return result;
			}
		}
		return "";
	}

	/**
		* Read the hash in gesture.key file.
		* @param file The path to gesture.key file.
		* @return SHA-1 hash in file.
	*/
	private static String readGestureFile(final String file) {
		String sha1 = new String("");
		try {
			int n = 0;
			//Open gesture.key file
			FileInputStream gesture = new FileInputStream(file);
			while ((n = gesture.read()) >= 0) {
				//If the byte is less than 16, add 0 in string for format byte with two digits in string.
				if (n < 0x10) {
					sha1 += "0" + Integer.toHexString(n);
				} else {
					sha1 += Integer.toHexString(n);
				}
			}
			gesture.close();
		//Display error if gesture.key file can't be opened.
		} catch (IOException e) {
			System.out.println("[-] Error: " + e.getMessage());
			System.exit(0);
		}
		return sha1;
	}

	/**
		* The function print the usage.
	*/
	private static void printHelp() {
		System.out.println("NAME");
		System.out.println("\tDecodeAndroidGesture.jar\n");
		System.out.println("SYNOPSIS");
		System.out.println("\tDecodeAndroidGesture.jar FILE\n");
		System.out.println("DESCRIPTION");
		System.out.println("\tDecode gesture.key file in Android device.");
		System.out.println("\tThis file is at : /data/system/gesture.key\n");
		System.out.println("\tFILE");
		System.out.println("\t\tSet the path to gesture.key file.\n");
		System.out.println("\tThe sequence returned by this application is the order of balls validation.");
		System.out.println("\tThe Android's screen can be represented by this pattern : (Each digit represent a ball)\n");
		System.out.println("\t+---+---+---+");
		System.out.println("\t| 0 | 1 | 2 |");
		System.out.println("\t+---+---+---+");
		System.out.println("\t| 3 | 4 | 5 |");
		System.out.println("\t+---+---+---+");
		System.out.println("\t| 6 | 7 | 8 |");
		System.out.println("\t+---+---+---+\n");
		System.out.println("AUTHOR");
		System.out.println("\tJeremy ZYRA");
	}

	/**
		* Entry point program.
		* @param args The path to gesture.key file.
	*/
	public static void main(final String[] args) {
		int[] tab1 = {};
		int[] tab2 = {0, 1, 2, 3, 4, 5, 6, 7, 8};
		String hash, sequence;
		//Check parameters.
		if (args.length == 1) {
			hash = readGestureFile(args[0]);
			System.out.println("[+] Searching...");
			//Launch algorithm for search sequence.
			sequence = genHashs(tab1, tab2, hash);
			//If sequence not found.
			if (sequence.equals("")) {
				System.out.println("[-] Sequence not found. ");
			} else {
				System.out.println("[+] Sequence: " + sequence);
			}
		//Print usage command.
		} else {
			printHelp();
		}
	}
}
