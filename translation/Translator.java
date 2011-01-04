package translation;

public class Translator {
	
	public static String getNorTranslation (String english)
	{
		if(english.equals("All machines"))
				return "Alle maskiner";
		
		System.out.println("Unknown translation");
		return english;
	}
}
