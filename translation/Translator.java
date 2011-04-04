package translation;

public class Translator {
	
	public static String getNorTranslation (String english)
	{
		if(english.equals("All machines"))
				return "Alle maskiner";
		else if(english.equals("All is online"))
			return "Alle er på";
		else if(english.equals("is online"))
			return "er på";
		else if(english.equals("All is offline"))
			return "Alle er av";
		else if(english.equals("is offline"))
			return "er av";
		else if(english.equals("All is restarting"))
			return "Alle restarter";
		else if(english.equals("is restarting"))
			return "restarter";
		else if(english.equals("All is shuting down"))
			return "Alle slås av";
		else if(english.equals("is shuting down"))
			return "slås av";
		else if(english.equals("All is starting up"))
			return "Alle starter opp";
		else if(english.equals("is starting up"))
			return "starter opp";
		else if(english.equals("All has an error"))
			return "Alle har en feil";
		else if(english.equals("has an error"))
			return "har en feil";
		else if(english.equals("Turn off"))
			return "Slå av";
		else if(english.equals("Turn on"))
			return "Slå på";
		else if(english.equals("Show all groups"))
			return "Vis alle grupper";
		else if(english.equals("is on in"))
			return "er på om";
		else if(english.equals("is off in"))
			return "er av om";
		else if(english.equals("seconds"))
			return "sekunder";
		else if(english.equals("Save"))
			return "Lagre";
		else if(english.equals("Type"))
			return "Type";
		else if(english.equals("Name"))
			return "Navn";
		else if(english.equals("Restart"))
			return "Restart";
		else if(english.equals("New projector"))
			return "Ny prosjektør";
		else if(english.equals("IP"))
			return "IP";
		else if(english.equals("Add new projector"))
			return "Legg til ny prosjektør";
		else if(english.equals("User information"))
			return "Brukerinformasjon";
		else if(english.equals("all groups"))
			return "alle grupper";
		else if(english.equals("IP"))
			return "IP";
		else if(english.equals("View more"))
			return "Vis mer";
		
		
		System.out.println("Unknown translation for \""+english+"\"");
		return english;
	}
}
