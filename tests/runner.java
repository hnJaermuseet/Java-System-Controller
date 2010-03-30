package tests;
import java.io.IOException;

import wol.*;
import wol.configuration.EthernetAddress;
import wol.configuration.*;

public class runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IllegalEthernetAddressException, IOException {
		
		String adresse = "00:1E:0B:63:58:17";
		System.out.println("Adresse: " + adresse);
		
		WakeUpUtil.wakeup(new EthernetAddress(adresse));
		System.out.println (adresse + " skal nå være startet.");
	}

}
