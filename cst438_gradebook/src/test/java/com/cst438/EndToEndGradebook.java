package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@SpringBootTest
public class EndToEndGradebook {
	
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/bohnny/Downloads/chromedriver_mac_arm64";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // in milliseconds.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment 438";
	public static final String TEST_ASSIGNMENT_DATE = "3-6-2023";
	public static final int TEST_COURSE_ID = 40443;
	

	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private AssignmentRepository assignmentRepository;
	
	@Test
	public void createAssignmentTest() throws Exception {
		
		//Deletes existing assignments in the database.
		Assignment assignment = null;
		do {
			assignment = assignmentRepository.findByCourseIdAndName1(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			if (assignment != null)
				assignmentRepository.delete(assignment);
		} while (assignment != null);
		
		// set the driver location and start driver
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		
		// Set implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		// Open  URL
		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		try {
			// Click "New Assignment" button
			driver.findElement(By.xpath("//a[last()]")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Enter course ID, assignment name, and due date
			WebElement courseIDField = driver.findElement(By.xpath("//input[@name='courseName']"));
			courseIDField.sendKeys(String.valueOf(TEST_COURSE_ID));
			WebElement assignmentNameField = driver.findElement(By.xpath("//input[@name='assignmentName']"));
			assignmentNameField.sendKeys(TEST_ASSIGNMENT_NAME);
			WebElement dueDateField = driver.findElement(By.xpath("//input[@name='dueDate']"));
			dueDateField.sendKeys(TEST_ASSIGNMENT_DATE);
			
			// Click "Submit" button
			driver.findElement(By.xpath("//input[@name='submit']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Verify if the assignment has been created
			assignment = assignmentRepository.findByCourseIdAndName(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			assertNotNull(assignment); 
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			// Clean up
			assignment = assignmentRepository.findByCourseIdAndName(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			if (assignment != null)
				assignmentRepository.delete(assignment);
			driver.quit();
		}
	}
}
