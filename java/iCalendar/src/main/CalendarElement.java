package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarElement implements Comparable<CalendarElement>{
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	private Date dtstart, dtend; 
	private int semester;
	
	
	public int getSemester() {
		return semester;
	}

	@SuppressWarnings("deprecation")
	public CalendarElement(String dtstart, String dtend, String summary) {
		try {
			this.dtstart = format.parse(cleanDtstart(dtstart));
			this.dtend = format.parse(cleanDtend(dtend));	
		} catch(ParseException e) {
			System.out.println(e.getMessage());
		}
		//8 means September
		if(this.dtstart.getMonth() == 8) this.semester = 1;
		else if(this.dtstart.getMonth() == 0 || this.dtstart.getMonth() == 11) this.semester = 2;
	}
	public Date getStartDate() {
		return this.dtstart;
	}
	
	public Date getEndDate() {
		return this.dtend;
	}
	
	@SuppressWarnings("deprecation")
	public String getStartTime() {
		String time = this.dtstart.getHours() + ":" + this.dtstart.getMinutes();
		if(time.endsWith(":0")) {
			return time + "0";
		}
		return time;
	}
	public String getStartDay() {
		return new SimpleDateFormat("EE").format(this.dtstart);
	}
	
	@SuppressWarnings("deprecation")
	public String getEndTime() {
		String time = this.dtend.getHours() + ":" + this.dtend.getMinutes();
		if(time.endsWith(":0")) {
			return time + "0";
		}
		return time;
	}
	public String getEndDay() {
		return new SimpleDateFormat("EE").format(this.dtend);
	}	
	
	private String cleanDtstart(String dtstart) {
		return dtstart.split(":")[1];
	}
	
	private String cleanDtend(String dtend) {
		return dtend.split(":")[1];
	}

	@Override
	public int compareTo(CalendarElement ce) {
		//Compare the first number in the time to the first number in the other calendar element.
		return Integer.parseInt(this.getStartTime().split(":")[0]) - Integer.parseInt(ce.getStartTime().split(":")[0]);
	}
	
	
}
