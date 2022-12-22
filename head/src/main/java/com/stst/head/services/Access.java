package com.stst.head.services;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stst.head.models.Employee;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;

@Service
public class Access {
	
	@Autowired
	private EmployeeRepo employeeRepo;
	
	// checking access by role
	//							 the repository for connecting to the DB, the user, who can log in
	public boolean check(User user, String... departs) {
		if ( (Arrays.stream(departs).anyMatch(d -> d.equals("All"))) && checkDB(user) ) { // if everyone has access
			return true;
		}
		if (user.getDepart() == null) { // if there are no cookies
			return false;
		}
		if ( ( Arrays.stream(departs).anyMatch(d -> d.equals(user.getDepart())) || user.getDepart().equals("lead") ) && checkDB(user) ) {
			return true;
		}
		
		return false;
	}
	
	// checking access by the presence of a user in the database
	private boolean checkDB(User user) {
		Optional<Employee> оptEmployee = employeeRepo.findById1( Long.valueOf(user.getId()) ); // id, Password, Secretnum && only isDeleted() == false
		if (оptEmployee.isEmpty()) {
			return false;
		}
		Employee employee = оptEmployee.get();

		if (!user.getPassword().equals( employee.getPassword() ) || !user.getSecretnum().equals( Integer.toString(employee.getSecretnum()) )) {
			return false;
		}
		return true;
	}
}
