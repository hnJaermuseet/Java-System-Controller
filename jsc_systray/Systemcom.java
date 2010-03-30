package jsc_systray;

public class Systemcom {
	public static void shutdown () {
		// Windows: shutdown -s
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			Runtime rt = Runtime.getRuntime();
			
			//Windows
			if (OS.startsWith("windows")) {
				Process proc = rt.exec("shutdown -s -t 0");
		    }
		    //Unix variants: bash
		    else {
		    	Process proc = rt.exec("shutdown -h now");
		    }
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		System.exit(1);
	}
	
	public static void reboot () {
		// Windows: shutdown -r
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			Runtime rt = Runtime.getRuntime();
			
			//Windows
			if (OS.startsWith("windows")) {
				Process proc = rt.exec("shutdown -r -t 0");
		    }
		    //Unix variants: bash
		    else {
		    	Process proc = rt.exec("shutdown -r now");
		    }
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		System.exit(1);
	}
}
