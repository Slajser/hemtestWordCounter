package mattias.hemtest;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Simple server class that listens to localhost, handles post requests and sends back a parsed
 * string containing words and their occurrences based on the data in the post request.
 * 
 * @author Mattias Viggeborn
 * @version 1.0
 * @since 	2022-07-19
 */
public class API {
    public static void main(String[] args){

        HttpServer server;
		try {
			int port = 3000;
			server = HttpServer.create(new InetSocketAddress(port), 0);// creates server connection on port 3000
			System.out.println("Server started on port "+ port);
		} catch (IOException e) {
			System.err.println("Couldn't start server");
			return;
		} 
		server.createContext("/", (exchange->{ // creates / endpoint (default page endpoint) 
			if (exchange.getRequestMethod().equals("GET")) {
                String responseText = "Hello try sending a post request to /count \n";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                BufferedOutputStream output = new BufferedOutputStream(exchange.getResponseBody());
                try {
                	output.write(responseText.getBytes()); // sends responseText to the client
                	output.flush();
                	output.close();
                }
                catch(IOException ioe) {
                	System.err.println(ioe.getMessage());
                	output.close();
                }
            } 
			else {
            	exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
		}));
        server.createContext("/count", (exchange -> { // creates /count endpoint 	
        	
            if (exchange.getRequestMethod().equals("GET")) {
                String responseText = "Hello try sending a post request here \n";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                BufferedOutputStream output = new BufferedOutputStream(exchange.getResponseBody());
                try {
                	output.write(responseText.getBytes()); // sends responseText to the client
                	output.flush();
                	output.close();
                }
                catch(IOException ioe) {
                	System.err.println(ioe.getMessage());
                	output.close();
                }
            } 
            else if (exchange.getRequestMethod().equals("POST")){
            	
            	StringBuilder sb = new StringBuilder();
            	BufferedInputStream is = new BufferedInputStream(exchange.getRequestBody());
            	
            	int i;// i is set to the read byte, if the amount of bytes read is not -1 it will append the byte cast to a char.
            	try {
            		while ((i = is.read()) != -1) {
            			sb.append((char) i);
            		}  
                }
            	catch(IOException ioe) {
            		System.err.println(ioe.getMessage());
            	}  
            	
            	String responseText = countWords(sb.toString(), 10);
            	try {
            		exchange.sendResponseHeaders(200, responseText.getBytes("UTF-8").length);               
            		BufferedOutputStream output = new BufferedOutputStream(exchange.getResponseBody());
            		output.write(responseText.getBytes("UTF-8"));
            		output.flush();
                	output.close();
                }
            	catch(UnsupportedEncodingException uee) {
            		System.err.println(uee.getMessage());
            	}
            	catch(IOException ioe) {
            		System.err.println(ioe.getMessage());
            	}
            }
            else {
            	exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));

        // makes is so each request is handled in its own thread up to 128 threads
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(128));  
        server.start();
    }
    
    /** 
     * Returns a string on the format {"word1":n1,"word2":n2...} with the most common words and 
     * their occurrences in the string sent in.
     * 
     * @param 	inputString 	The string to be searched for words and their occurrences
     * @param 	commonWords 	Specifies how many of the most common words should be returned.
     * 							Example: commonWords=5 will return the 5 most common words and their occurrences.
     * @return 	A parsed string with the most common words and their occurrences
     */
    private static String countWords(String inputString, int commonWords){
    	List<String> wordList = wordSeparator(inputString); // function to separate the words by space
    	wordList.sort(null); // we want all words that are equal after each other for ease of use.
 
    	List<MyWordPair> pairList = pairWordsWithOccurrence(wordList);
    	Collections.sort(pairList, Comparator.comparing(pair-> -pair.getOccurrence())); // -value to get descending instead of ascending
    	
    	String stringWithResult="{";
    	
    	int counter = 0;
    	while(pairList.size()>0&&counter<commonWords) { 
    		
    		stringWithResult = stringWithResult.concat("\"" + pairList.get(0).getWord() +
    				"\":" + pairList.get(0).getOccurrence() + ",");
    		
    		// removes the first and most popular word after using it in the string, so the loop ends when there is no more words.
    		pairList.remove(0); 
    		counter++;
    	}   	
    	stringWithResult = stringWithResult.substring(0,stringWithResult.length()-1); // removes the extra ',' at the end.
    	stringWithResult = stringWithResult.concat("}");
    	
		return stringWithResult;
    }
    
    /**
    * Separates words in a string by space ' ' and returns a list of those words.
    * 
    * @param 	string	The string that will be used to find words and separate them.
    * @return 	The words that were found in the form of a List<String> 
    */
    private static List<String> wordSeparator(String string){
    	StringBuilder sb = new StringBuilder();
    	List<String> wordList = new ArrayList<String>();
    	for(int a = 0; a < string.length(); a++) {
    		if(string.charAt(a)==' ') { 
    			if(sb.length()>0)
    				wordList.add(sb.toString()); // adds the words to the list
    			sb.setLength(0); // "empties" the builder.
    		}
    		else
    			sb.append(string.charAt(a)); // builds individual words	
    	}
    	wordList.add(sb.toString()); // adds the final word
    	return wordList;
    }
    
    /** 
    * Returns a list of pairs of words matched with how many times that word occurred in the list.
    * 
  	* @param 	wordList	 The list of words as a List<String> that is to be matched with how often they occur.
  	* @return 	The list of pairs		
    */
    private static List<MyWordPair> pairWordsWithOccurrence(List<String> wordList){
    	int[] occurrence = new int[wordList.size()]; 
    	int uniqueWords=0; // will be 1 less than the amount of unique words since the first word starts at 0 due to index
    	int currentWordCounter=1;
    	
    	for(int a = 0;a<wordList.size();a++) {
    		if(a!=wordList.size()-1) {
    			
    			// if this word the same as the next word. remove the next word and add 1 to the counter
    			if(wordList.get(a).equals(wordList.get(a+1))) { 
    				currentWordCounter++;
    				wordList.remove(a+1);
    				a--; // a is reduced so we stay in the same position in the list until we find a new value
    					if(a==wordList.size()-1) // last word if it was not unique
    						occurrence[uniqueWords]=currentWordCounter;
    			}	
    			else {
    				occurrence[uniqueWords]=currentWordCounter;
    				
    				//reset for next word 
    				currentWordCounter=1;
    				uniqueWords++;
    			}
    		}
    		else  // last word if it was unique
    			occurrence[uniqueWords]=currentWordCounter;
    	}
    	
    	List<MyWordPair> pairList = new ArrayList<MyWordPair>();
    	for(int a = 0;a<wordList.size();a++) { // puts the word matched with occurrence into a list.
    		pairList.add(new MyWordPair(wordList.get(a), occurrence[a]));
    	}
    	
		return pairList;
    }
    
}