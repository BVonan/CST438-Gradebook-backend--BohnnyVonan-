package com.cst438.services;
import java.util.Optional;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import antlr.collections.List;
public class RegistrationServiceMQ extends RegistrationService {
	@Autowired
	EnrollmentRepository enrollmentRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}
	// ----- configuration of message queues
	@Autowired
	Queue registrationQueue;
	// ----- end of configuration of message queue
	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
	
	    Course course = courseRepository.findById(enrollmentDTO.course_id)
	            .orElseThrow(() -> new RuntimeException("Course not found"));
	   
	    Enrollment enrollment = new Enrollment();
	    enrollment.setCourse(course);
	    enrollment.setStudentEmail(enrollmentDTO.studentEmail);
	    enrollment.setStudentName(enrollmentDTO.studentName);
	    enrollmentRepository.save(enrollment);
	  
	}
	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		
		courseDTO.course_id = course_id;
		this.rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
        System.out.println("Sending Grades for: " + courseDTO);
		
	}
}














