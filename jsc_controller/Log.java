package jsc_controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {
	public static void saveLog (String name, String event)
	{
		try {
			DateFormat date_filename  = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat date_log       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			
			FileWriter file = 
				new FileWriter(System.getProperty("user.home") +
						File.separatorChar + "jsc_config" + 
						File.separatorChar + "logs" +
						File.separatorChar +
							date_filename.format(date)+ "-" +
							name + ".txt",
						true // Append, yes
						);
			BufferedWriter out = new BufferedWriter(file);
			
			out.newLine();
			out.write(date_log.format(date) + " "+ event + "\n");
			out.close();
		}
		catch (Exception e) {
			// Do nothing
			System.out.println("Log.saveLog - Exception: " + e);
		}
	}
}
