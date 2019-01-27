package main;

import java.io.File;

public class MyFile extends File {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	
	public MyFile(String pathname, String id) {
		super(pathname);
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}

}
