import java.util.*;
import edu.duke.*;
import java.io.File;

// Author: Stipan Madzar 

public class VigenereBreaker {
    
    private Scanner scan = new Scanner(System.in);
    private FileResource fr;
    
    public VigenereBreaker() {
        fr = new FileResource();
    }
    
    public String sliceString(String message, int whichSlice, int totalSlices) {
        String characters = "";
        
        for(int i = whichSlice; i < message.length(); i += totalSlices) {
            characters += message.charAt(i);
        }
        return characters;
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker cc = new CaesarCracker(mostCommon);
        
        for(int i = 0; i < klength; i++) {
            String slice = sliceString(encrypted, i, klength);
            int dKey =  cc.getKey(slice);
            key[i] = dKey;
        }
        return key;
    }
    
    public HashSet<String> readDictionary(FileResource fr) {
        
        HashSet<String> dictionary = new HashSet<String>();
        
        for(String word : fr.lines()) {
            word = word.toLowerCase();
            dictionary.add(word);
        }
        return dictionary;
    }
    
    public int countWords(String message, HashSet<String> dictionary) {
        int count = 0;
        
        for(String word : message.split("\\W")) {
            word = word.toLowerCase();
            
            if(dictionary.contains(word)) {
                count++;
            }
        }
        return count;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary) {
        int equalWords = 0;
        String decMessage = "";
        
        for(int i = 1; i < 101; i++) {
            int[] key = tryKeyLength(encrypted, i, mostCommonCharIn(dictionary));
            VigenereCipher vc = new VigenereCipher(key);
            String decrypted = vc.decrypt(encrypted);
            if(countWords(decrypted, dictionary) > equalWords) {
                equalWords = countWords(decrypted, dictionary);
                decMessage = decrypted;
            }
        }
        return decMessage;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary) {
        HashMap<Character, Integer> letters = new HashMap<Character, Integer>();
        
        for(String word : dictionary) {
            for(int i = 0; i < word.length(); i++) {
                if(!letters.containsKey(word.charAt(i))) {
                    letters.put(word.charAt(i), 1);
                }
                else {
                    letters.put(word.charAt(i), letters.get(word.charAt(i))+1);
                }
            }
        }
        
        int size = 0;
        char mostCommon = ' ';
        
        for(char ch : letters.keySet()) {
            if(letters.get(ch) > size) {
                size = letters.get(ch);
                mostCommon = ch;
            }
        }
        return mostCommon;
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages) {
        String language = "";
        String message = "";
        int count = 0;
        
        for(String lang : languages.keySet()) {
            String decrypted = breakForLanguage(encrypted, languages.get(lang));
            
            if(countWords(decrypted, languages.get(lang)) > count) {
                language = lang;
                message = decrypted;
                count = countWords(decrypted, languages.get(lang));
            }
        }
        System.out.println("\nLanguage of the message: " + language + "\n");
        System.out.println("Decrypted message: \n\n" + message);
    }
    
    public void breakVigenere () {
        String message = fr.asString();
        System.out.println("File with decrypted message processed!\n");
        HashMap<String, HashSet<String>> languages = new HashMap<String, HashSet<String>>();
        System.out.println("Please select language dictionaries: \n");
        DirectoryResource dr = new DirectoryResource();
        
        for(File f : dr.selectedFiles()) {
            FileResource fr = new FileResource(f);
            languages.put(f.getName(), readDictionary(fr));
            System.out.println(f.getName() + " dictionary processed!");
        }
    
        breakForAllLangs(message, languages);
    }
    
}
