package main;

public class Course {
	
	private int semester;
	
	public String getCourseCode() {
		return courseCode;
	}

	public String getCourseNumber() {
		return courseNumber;
	}
	
	public String getFullCourse() {
		return this.courseCode + " " + this.courseNumber;
	}

	private String courseCode, courseNumber;
	
	public Course(String s, int semester) {
		this.semester = semester;
		//Try special case
		try {
			//Remove crap in from the start
			s = s.substring(s.indexOf("SUMMARY:DeptKey[departmentCode=") + "SUMMARY:DeptKey[departmentCode=".length());
			//Get the course code
			this.courseCode = s.substring(0, s.indexOf("\\"));
			//Chop off again
			s = s.substring(s.indexOf("[courseNumber=") + "[courseNumber=".length());
			//Get the course number
			this.courseNumber = s.substring(0, s.indexOf("\\"));
		} catch(StringIndexOutOfBoundsException e) {
			//Normal case
			s = s.substring(s.indexOf("SUMMARY:") + "SUMMARY:".length());
			String[] split = s.split(" ");
			this.courseCode = split[0];
			this.courseNumber = split[1];
		}

	}
	
	public int getSemester() {
		return this.semester;
	}
	
	@Override
	public int hashCode() {
		return this.getFullCourse().hashCode();
	}

}
