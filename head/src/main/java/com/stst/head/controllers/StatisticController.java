package com.stst.head.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.stst.head.models.CityEnum;
import com.stst.head.models.DepartEnum;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.SobjectRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;

@Controller
public class StatisticController {
	
	@Autowired
	private Access access;
	@Autowired
	private SobjectRepo objectRepo;
	@Autowired
	private EmployeeRepo employeeRepo;
	
	@GetMapping("/statistics")
	public String getStatistics(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/who");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
				
		model.addAttribute("cities", CityEnum.getMap());
		model.addAttribute("employees", employeeRepo.findAll2(DepartEnum.getEnumsByAbbrs("lead", "mast", "pto", "sales")));
		return "statistics/statistics";
	}
	
	@GetMapping("/statistics/objects")
	public String getObjects(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back") == null ? "/who" : request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		
		Byte status = request.getParameter("status") == null || request.getParameter("status") == "" ? null : Byte.valueOf( request.getParameter("status") );
		Long employeeId = Etc.idFromDatalist(request.getParameter("employee"));
		String city = request.getParameter("city");
		
		model.addAttribute("status", status);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("city", city);
		
		model.addAttribute("objects", objectRepo.findAll2(employeeId, status, CityEnum.getEnumByAbbr(city).orElse(null)).stream().sorted().toList());
		return "statistics/objects";
	}
	
	@GetMapping("/statistics/objects/print")
	public String getObjectsPrint(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		
		Byte status = request.getParameter("status") == null || request.getParameter("status") == "" ? null : Byte.valueOf( request.getParameter("status") );
		Long employeId = request.getParameter("employeeId") == null || request.getParameter("employeeId") == "" ? null : Long.valueOf( request.getParameter("employeeId") );
		String city = request.getParameter("city");
		
		model.addAttribute("status", status);
		model.addAttribute("employee", employeId == null ? null : employeeRepo.findById2(employeId).orElse(null));
		model.addAttribute("city", city.equals("") ? "все" : CityEnum.getEnumByAbbr(city).get().getName());
		
		model.addAttribute("objects", objectRepo.findAll2(employeId, status, CityEnum.getEnumByAbbr(city).orElse(null)).stream().sorted().toList());
		return "statistics/objectsPrint";
	}
	
}
