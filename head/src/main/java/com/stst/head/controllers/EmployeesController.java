package com.stst.head.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.stst.head.models.CityEnum;
import com.stst.head.models.DepartEnum;
import com.stst.head.models.Employee;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;
import com.stst.head.services.Notifs;
import com.stst.head.services.PasswordHashing;

@Controller
public class EmployeesController {
	
	@Autowired
	private Access access;
	@Autowired
	private Notifs notifs;
	@Autowired
	private EmployeeRepo employeeRepo;
	
	@GetMapping("/employees")
	public String getEmployees(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/"+cDepart);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}				
				
		// location of employees by department
		Map<String, List<Employee>> mapEmployeeAndDeparts = new HashMap<String, List<Employee>>();
		List<Employee> employees = employeeRepo.findAll1();
		for (DepartEnum departEnum : DepartEnum.values()) { 
			mapEmployeeAndDeparts.put(departEnum.getName(), 
					employees.stream().filter(e -> e.getDepart() == departEnum).toList());
		}
		model.addAttribute("employeesAndDeparts", mapEmployeeAndDeparts);
		return "employees/employees";
	}
	
	@GetMapping("/employees/new-employee")
	public String getNewEmployee(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
			
//		employeeRepo.save( Employee.creatingTheFirstUser() ); // Creating the first user
		
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/employees");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("departs", DepartEnum.getMap());
				model.addAttribute("cities", CityEnum.getMap());
				
		return "employees/newEmployee";
	}
	
	@PostMapping("/employees/new-employee")
	public String postNewEmployee(HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/employees");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("departs", DepartEnum.getMap());
				model.addAttribute("cities", CityEnum.getMap());
		
		String name = request.getParameter("name").trim();
		String surname = request.getParameter("surname").trim();
		String secondName = request.getParameter("secondName").trim();
		String phone = request.getParameter("phone");
		Long telegramChatId;
		if (request.getParameter("telegramChatId").equals("")) {
			telegramChatId = null;
		} else {
			telegramChatId = Long.valueOf( request.getParameter("telegramChatId").trim() );
		}
		String nickname = request.getParameter("nickname").trim();
		char[] password = request.getParameter("password").toCharArray();
		String depart = request.getParameter("depart");
		String city = request.getParameter("city");

		// checking for the repetition of the nickname
		if (employeeRepo.findByNickname(nickname).isPresent()) {
			model.addAttribute("info", "?????????????? ??????????????????????!");
			return "employees/newEmployee";
		}
		// checking for the existence of the department
		Optional<DepartEnum> ??ptDepartEnum = DepartEnum.getEnumByAbbr(depart);
		if (??ptDepartEnum.isEmpty()) {
			model.addAttribute("info", "?????????? ???? ????????????????????!");
			return "employees/newEmployee";
		}
		// checking for the existence of the city
		Optional<CityEnum> ??ptCityEnum = CityEnum.getEnumByAbbr(city);
		if (??ptCityEnum.isEmpty()) {
			model.addAttribute("info", "?????????? ???? ????????????????????!");
			return "employees/newEmployee";
		}
		// password hashing
		String hashedPass = PasswordHashing.hashPassword(password);
		Arrays.fill(password, '0');
		
		Employee employee = new Employee (name, surname, secondName, Etc.standardizePhone(phone),
				telegramChatId, nickname, hashedPass, ??ptDepartEnum.get(), ??ptCityEnum.get());
		employeeRepo.save(employee);
		
		model.addAttribute("info", "?????????????? ????????????!");
		return "employees/newEmployee";	
	}
	
	@GetMapping("/employees/edit-employee/{id}")
	public String getEditEmployees(@PathVariable(value = "id") long id, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/employees");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Employee> ??ptEmployee = employeeRepo.findById(id);
				if (??ptEmployee.isEmpty()) {
					return "error";
				}
				model.addAttribute("employee", ??ptEmployee.get());
				model.addAttribute("departs", DepartEnum.getMap());
				model.addAttribute("cities", CityEnum.getMap());
		return "employees/editEmployee";
	}
	
	@PostMapping("/employees/edit-employee/{id}")
	public String postEditEmployees(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/employees");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Employee> ??ptEmployee = employeeRepo.findById(id);
				if (??ptEmployee.isEmpty()) {
					return "error";
				}
				model.addAttribute("employee", ??ptEmployee.get());
				model.addAttribute("departs", DepartEnum.getMap());
				model.addAttribute("cities", CityEnum.getMap());
		
		String name = request.getParameter("name").trim();
		String surname = request.getParameter("surname").trim();
		String secondName = request.getParameter("secondName").trim();
		String phone = request.getParameter("phone");
		Long telegramChatId;
		if (request.getParameter("telegramChatId").equals("")) {
			telegramChatId = null;
		} else {
			telegramChatId = Long.valueOf( request.getParameter("telegramChatId").trim() );
		}
		String nickname = request.getParameter("nickname").trim();
		char[] password = request.getParameter("password").toCharArray();
		String depart = request.getParameter("depart");
		String city = request.getParameter("city");

		// checking for the existence of the department
		Optional<DepartEnum> ??ptDepartEnum = DepartEnum.getEnumByAbbr(depart);
		if (??ptDepartEnum.isEmpty()) {
			model.addAttribute("info", "?????????? ???? ????????????????????!");
			return "employees/editEmployee";
		}
		// checking for the existence of the city
		Optional<CityEnum> ??ptCityEnum = CityEnum.getEnumByAbbr(city);
		if (??ptCityEnum.isEmpty()) {
			model.addAttribute("info", "?????????? ???? ????????????????????!");
			return "employees/editEmployee";
		}
		Employee employee = ??ptEmployee.get();
		// checking for the repetition of the nickname
			if ( !nickname.equals(employee.getNickname()) && employeeRepo.findByNickname(nickname).isPresent()) {
				model.addAttribute("info", "?????????????? ??????????????????????!");
				return "employees/editEmployee";
			}
				// checking whether the user wants to change the password
		if (password.length > 0) {
			String hashedPass = PasswordHashing.hashPassword(password);
			Arrays.fill(password, '0');
			if ( !hashedPass.equals(employee.getPassword()) ) {
				employee.setPassword(hashedPass);
			}
		}
		employee.setName(name);
		employee.setSurname(surname);
		employee.setSecondName(secondName);
		employee.setPhone(Etc.standardizePhone(phone));
		employee.setTelegramChatId(telegramChatId);
		employee.setNickname(nickname);
		employee.setDepart(??ptDepartEnum.get());
		employee.setCity(??ptCityEnum.get());
		employeeRepo.save(employee);
		
		model.addAttribute("info", "?????????????? ??????????????!");
		
		return "employees/editEmployee";
	}
	
	@GetMapping("/employees/delete-employee/{id}")
	public String getDeleteEmployees(@PathVariable(value = "id") long id, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/employees");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Employee> ??ptEmployee = employeeRepo.findById(id);
				if (??ptEmployee.isEmpty()) {
					return "error";
				}
				model.addAttribute("employee", ??ptEmployee.get());
		return "employees/deleteEmployee";
	}
	
	@PostMapping("/employees/delete-employee/{id}")
	public String postDeleteEmployees(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/employees");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Employee> ??ptEmployee = employeeRepo.findById(id);
				if (??ptEmployee.isEmpty()) {
					return "error";
				}
		long id2 = Long.parseLong( request.getParameter("id2") ); // for comparison
		if ( !(id == id2) ) {
			return "error";
		}
		Employee employee = ??ptEmployee.get();
		employee.setDeleted(true);
		employeeRepo.save(employee);
		return "redirect:/employees";
	}
	
	@GetMapping("/employees/test-notif/{id}")
	public String getTestNotif(@PathVariable(value = "id") long id, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Employee> ??ptEmployee = employeeRepo.findById(id);
				if (??ptEmployee.isEmpty()) {
					return "error";
				}
				
		notifs.sendNotif(??ptEmployee.get(), "???????????????? ??????????????????????", null);
		return "redirect:/employees/edit-employee/" + id	;
	}
}
