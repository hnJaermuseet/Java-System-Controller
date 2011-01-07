package translation;

public class Translator {
	
	public static String getNorTranslation (String english)
	{
		if(english.equals("All machines"))
				return "Alle maskiner";
		else if(english.equals("All is online"))
			return "Alle er p�";
		else if(english.equals("is online"))
			return "er p�";
		else if(english.equals("All is offline"))
			return "Alle er av";
		else if(english.equals("is offline"))
			return "er av";
		else if(english.equals("All is restarting"))
			return "Alle restarter";
		else if(english.equals("is restarting"))
			return "restarter";
		else if(english.equals("All is shuting down"))
			return "Alle sl�s av";
		else if(english.equals("is shuting down"))
			return "sl�s av";
		else if(english.equals("All is starting up"))
			return "Alle starter opp";
		else if(english.equals("is starting up"))
			return "starter opp";
		else if(english.equals("All has an error"))
			return "Alle har en feil";
		else if(english.equals("has an error"))
			return "har en feil";
		else if(english.equals("Turn off"))
			return "Sl� av";
		else if(english.equals("Turn on"))
			return "Sl� p�";
		else if(english.equals("Show all groups"))
			return "Vis alle grupper";
		
		System.out.println("Unknown translation for \""+english+"\"");
		return english;
	}
}
