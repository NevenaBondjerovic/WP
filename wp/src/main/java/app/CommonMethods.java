package app;

public class CommonMethods {


	public CommonMethods() {
		super();
	}

	public String setText(String text){
		String returnValue=text;
		
		if(returnValue.contains("\n")){
			String newText="";
			for(char oneLetter : returnValue.toCharArray()){
				if(oneLetter=='\n'){
					newText+="{EOL}";
				}else{
					newText+=oneLetter;
				}
			}
			returnValue=newText;
		}
		
		if(returnValue.contains(",")){
			String newText="";
			for(char oneLetter : returnValue.toCharArray()){
				if(oneLetter==','){
					newText+="{COMMA}";
				}else{
					newText+=oneLetter;
				}
			}
			returnValue=newText;
		}
		
		
		return returnValue;
	}
	
	public String getText(String text){
		if(text.contains("{")){
			String newText="";
			Integer currentCharacter=0;
			while(currentCharacter<text.length()){
				char currentLetter=text.charAt(currentCharacter);
				if(currentLetter=='{'){
					String secondPart=text.substring(currentCharacter);
					if(secondPart.contains("}")){
						if(text.substring(currentCharacter+1, 
								currentCharacter+secondPart.indexOf("}")).equals("EOL")){
							newText+="\n";
							currentCharacter=currentCharacter+secondPart.indexOf("}")+1;
						}else if(text.substring(currentCharacter+1, 
								currentCharacter+secondPart.indexOf("}")).equals("COMMA")){
							newText+=",";
							currentCharacter=currentCharacter+secondPart.indexOf("}")+1;
						}else{
							newText+=currentLetter;
							currentCharacter+=1;
						}
					}else{
						newText+=currentLetter;
						currentCharacter+=1;
					}
				}else{
					newText+=currentLetter;
					currentCharacter+=1;
				}
			}
			return newText;
		}else{
			return text;
		}
	}
	
}
