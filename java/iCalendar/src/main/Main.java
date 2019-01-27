package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import http.HttpGet;

public class Main {
	
	private static final String baseUrl = "https://stark-escarpment-19574.herokuapp.com/";
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		//Run 4 times with 15 second intervals.
		while(true) {
			LinkedList<MyFile> filesToCheck = new LinkedList<MyFile>();
			HttpGet hg = new HttpGet("https://stark-escarpment-19574.herokuapp.com/unparsedfiles", false);
			String response = hg.get();
			//Remove the last '|' if its there
			if(response.charAt(response.length() - 1) == '#') {
				response = response.substring(0, response.length() - 1);
			}
			String[] individualFiles = response.split("#");
			//Download all files
			int downloaded = 0;
			for(String file : individualFiles) {
				
				String[] fileInfo = file.split(",");
				//Don't do files that have been checked
				if(!fileInfo[1].equals("null")) {
					continue;
				}
				System.out.println("Downloaded " + fileInfo[0]);
				downloaded++;
				
				try {
					//Check exists
					File f = new File(fileInfo[0]);
					if(f.exists()) {
						f.delete();
					}
					//Download the file
					Files.copy(
						    new URL(baseUrl + fileInfo[0]).openStream(),
						    Paths.get(fileInfo[0]));
					filesToCheck.add(new MyFile(fileInfo[0], fileInfo[2]));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			System.out.println("Downloaded " + downloaded + " files.");
			//Check every file
			while(!filesToCheck.isEmpty()) {
				HashMap<String, LinkedList<CalendarElement>> courseDays = new HashMap<String, LinkedList<CalendarElement>>();
				HashMap<String, Course> courseCorresponding = new HashMap<>();
				MyFile fileToCheck = filesToCheck.poll();
				try(
					BufferedReader in = new BufferedReader(new FileReader(fileToCheck))	
						) {
					String inputLine;
					while((inputLine = in.readLine()) != null) {
						//Only print date start
						if(inputLine.startsWith("SUMMARY:")) {
							//Ensure the DTSTART has a corresponding DTEND; and SUMMARY
							String summary = inputLine;
							//Then skip 4 lines for dtend
							for(int i = 0; i < 4; i++)
								in.readLine();					
							//in.readLine(); because the next one MUST be DTEND;
							String dtstart = in.readLine();
							//Get summary
							String dtend = in.readLine();
							
							//IF neither are null, 
							if(dtstart != null && dtend != null && summary != null) {
								CalendarElement ce = new CalendarElement(dtstart, dtend, summary);
								Course c = new Course(summary, ce.getSemester());
								courseCorresponding.put(c.getFullCourse(), c);
								//If not yet in hashmap, initialize.
								if(!courseDays.containsKey(c.getFullCourse()))
									courseDays.put(c.getFullCourse(), new LinkedList<CalendarElement>());
								courseDays.get(c.getFullCourse()).add(ce);
							}
						}
					}
					
				} catch(IOException e) {
					System.out.println(e.getMessage());
				}
				
				//From Jan to April is Sem 2, otherwise Sem 1
				int currentSemester = LocalDateTime.now().getMonthValue();
				if(currentSemester > 0 && currentSemester < 5) {
					currentSemester = 2;
				} else {
					currentSemester = 1;
				}
				
//				for(String course : courseDays.keySet()) {
//					//Only for courses of the current semester
//					Course c = courseCorresponding.get(course);
//					if(c.getSemester() == currentSemester) {
//						LinkedList<CalendarElement> events = courseDays.get(c.getFullCourse());
//						System.out.println(c.getSemester() + ":" + "COURSE:" + c.getFullCourse());
//						while(!events.isEmpty()) {
//							CalendarElement ce = events.poll();
//							if(ce.getSemester() == currentSemester) {
//								System.out.println("START:" + ce.getStartDay() + "|" + ce.getStartTime());
//								System.out.println("END:" + ce.getEndDay() + "|" + ce.getEndTime());
//							}
//						}				
//					}
//				}
				
				//GENERATE course json AND create time schedule by DAY
				HashMap<String, PriorityQueue<CalendarElement>> timesByDay = new HashMap<>();
				StringBuilder jsonResult = new StringBuilder();
				//Start two array brackets as we will add another array with the free times
				jsonResult.append("[[");
				for(String course : courseDays.keySet()) {
					Course c = courseCorresponding.get(course);
					if(c.getSemester() == currentSemester) {
						//Append course json
						jsonResult.append("{\"course\":");
						jsonResult.append("\"" + c.getFullCourse() + "\",");
						jsonResult.append("\"term\":\"" + c.getSemester() + "\"");
						jsonResult.append("},");
						//Build Day -> Times schedule to later use for calculating free times
						LinkedList<CalendarElement> events = courseDays.get(c.getFullCourse());
						//For every calendar event
						while(!events.isEmpty()) {
							CalendarElement ce = events.poll();
							//Put the day and add to the list of elements of that day
							if(!timesByDay.containsKey(ce.getStartDay())) {
								timesByDay.put(ce.getStartDay(), new PriorityQueue<CalendarElement>());
							}
							timesByDay.get(ce.getStartDay()).offer(ce);
						}				
					}
				}
				//Remove the last ','
				jsonResult.deleteCharAt(jsonResult.length()-1);
				jsonResult.append("],");
				
//				//Test print events
//				for(String day : timesByDay.keySet()) {
//					System.out.println("Day: " + day);
//					
//					PriorityQueue<CalendarElement> times = timesByDay.get(day);
//					while(!times.isEmpty()) {
//						CalendarElement ce = times.poll();
//						System.out.println(ce.getStartTime() + "-" + ce.getEndTime());
//					}
//				}
				FreeTimeCalculator ftc = new FreeTimeCalculator(timesByDay);
				HashMap<String, LinkedList<String>> freeTimes = ftc.calculateFreeTimes();
				
				
				//Test print free times
//				for(String day : timesByDay.keySet()) {
//					System.out.println("Day: " + day);
//					LinkedList<String> times = freeTimes.get(day);
//					while(!times.isEmpty()) {
//						System.out.println(times.poll());
//					}
//				}
				
				jsonResult.append("[");
				for (String day : timesByDay.keySet()) {
					jsonResult.append("{\"day\":\"" + day + "\",");
					jsonResult.append("\"free_times\":\"");
					LinkedList<String> times = freeTimes.get(day);
					while (!times.isEmpty()) {
						jsonResult.append(times.poll() + "|");
					}
					//Delete the last "|"
					jsonResult.deleteCharAt(jsonResult.length() -1);
					jsonResult.append("\"");
					jsonResult.append("},");
				}
				//Remove the last ','
				jsonResult.deleteCharAt(jsonResult.length()-1);
				jsonResult.append("]]");
				//System.out.println(jsonResult.toString());
				System.out.println("Parsed and returning json to server for " + fileToCheck.getName());
				
				fileToCheck.delete();
				
				String responseContent = "id=" + fileToCheck.getId() + "&parsed=" + URLEncoder.encode(jsonResult.toString(), "UTF-8");
					
				//System.out.println(baseUrl + "parsedfile?" + responseContent);
				hg = new HttpGet(baseUrl + "parsedfile?" + responseContent,  false);
				System.out.println(hg.get());
				
				System.out.println("Returned to server.");
			}
			//Sleep for 5 seconds
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {

				System.out.println("failed to sleep thread for 5s");
			}			
		}		
	}
}
