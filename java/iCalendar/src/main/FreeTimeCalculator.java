package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class FreeTimeCalculator {
	
	private static final int lowerLimit = 8; //8am free earliest
	private static final int upperLimit = 21; //9pm free latest
	
	private String timeToCheckFrom;
	
	HashMap<String, PriorityQueue<CalendarElement>> timesByDay;
	
	public FreeTimeCalculator(HashMap<String, PriorityQueue<CalendarElement>> timesByDay) {
		this.timesByDay = timesByDay;
		//Start checking from 8am
		this.timeToCheckFrom = lowerLimit + ":00";
	}
	
	public HashMap<String, LinkedList<String>> calculateFreeTimes() {
		
		HashMap<String, LinkedList<String>> freeTimes = new HashMap<>();
		for(String day : timesByDay.keySet()) {
			this.timeToCheckFrom = lowerLimit + ":00";
			LinkedList<String> sorted = new LinkedList<>();
			freeTimes.put(day, sorted);
			PriorityQueue<CalendarElement> sce = timesByDay.get(day);
			while(!sce.isEmpty()) {
				CalendarElement ce = sce.poll();
				
				if(!timeToCheckFrom.equals(ce.getStartTime())) {
					sorted.add(timeToCheckFrom + "-" + ce.getStartTime());
					timeToCheckFrom = ce.getEndTime();
				}
			}
			
			if(!timeToCheckFrom.equals(upperLimit + ":00")) {
				sorted.add(timeToCheckFrom + "-" + upperLimit + ":00");
			}
		}
//		HashMap<String, LinkedList<String>> freeTimes = new HashMap<>();
//		//Iterate over each day and get the CalendarElements on that dya
//		for(String day : timesByDay.keySet()) {
//			//Reset timeToCheckFrom
//			this.timeToCheckFrom = lowerLimit + ":00";
//			//The new formed sce
//			LinkedList<String> sorted = new LinkedList<>();
//			freeTimes.put(day, sorted);
//			PriorityQueue<CalendarElement> sce = timesByDay.get(day);
//			//
//			int firstHalf = 0;
//			while(!sce.isEmpty()) {
//				//Get the calendar element
//				CalendarElement ce = sce.poll();
//				//Split to get hour and minute components
//				String[] split = ce.getStartTime().split(":");
//				String[] splitFrom = timeToCheckFrom.split(":");
//				//Get hours to compare
//				firstHalf = Integer.parseInt(split[0]);
//				int firstHalfCheckFrom = Integer.parseInt(splitFrom[0]);
//				//If the hour counts are bigger, we know its definitely bigger, so we add a range.
//				if(firstHalf > firstHalfCheckFrom) {
//					sorted.add(timeToCheckFrom + "-" + ce.getStartTime());
//					//Update time to check from
//					timeToCheckFrom = ce.getEndTime();
//				} else {
//					//Check minutes
//					if(Integer.parseInt(split[1]) > Integer.parseInt(splitFrom[1])) {
//						sorted.add(timeToCheckFrom + "-" + ce.getStartTime());
//						//Update time to check from
//						timeToCheckFrom = ce.getEndTime();
//					}
//				}
//			}
//			if(!timeToCheckFrom.equals(upperLimit + ":00")) {
//				sorted.add(timeToCheckFrom + "-" + upperLimit + ":00");
//			}
//		}
		return freeTimes;
		
	}

}
