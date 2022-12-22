package com.stst.head.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.stst.head.models.DepartEnum;
import com.stst.head.models.TimeTracking;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.SnabRequestRepo;
import com.stst.head.repos.TimeTrackingRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Notifs;

@Controller
public class MainController {
	
	@Autowired
	private Access access;
	@Autowired
	private Notifs notifs;
	@Autowired
	private SnabRequestRepo snabRequestRepo;
	@Autowired
	private TimeTrackingRepo timeTrackingRepo;
	@Autowired
	private EmployeeRepo employeeRepo;
	
	@GetMapping("/lead")
	public String getLead(Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "lead", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));
		
		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		// snab requests
		boolean redButtonInRequest = snabRequestRepo.findAll5(Long.valueOf(cId));
		model.addAttribute("redButtonInRequest", redButtonInRequest);
		return "main/lead";
	}
	
	@GetMapping("/pto")
	public String getPto(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "pto", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		// snab requests
		boolean redButtonInRequest = snabRequestRepo.findAll4(Long.valueOf(cId));
		model.addAttribute("redButtonInRequest", redButtonInRequest);
		return "main/pto";
	}
	
	@GetMapping("/snab")
	public String getSnab(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		// snab requests
		boolean redButtonInRequest = snabRequestRepo.findAll3(Long.valueOf(cId));
		model.addAttribute("redButtonInRequest", redButtonInRequest);
		snabRequestRepo.findAll3(Long.valueOf(cId));
		return "main/snab";
	}
	
	@GetMapping("/sales")
	public String getSales(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "sales", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		return "main/sales";
	}
	
	@GetMapping("/buh")
	public String getBuh(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "buh", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		return "main/buh";
	}
	
	@GetMapping("/adm")
	public String getAdm(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		return "main/adm";
	}
	@GetMapping("/adm2")
	public String getAdm2(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		return "main/adm2";
	}
	
	@GetMapping("/mast")
	public String getMast(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "adm2") ) {
					return "error";
				}
		model.addAttribute("notifs", notifs.getNotifs(cId));

		// time tracking
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
		model.addAttribute("open", false);
			model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
		} else {
			TimeTracking time = oTime.get();
			model.addAttribute("open", time.isOpen());
			if (time.isOpen()) {
				model.addAttribute("workedTimeForJS", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			} else {
				model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(Long.valueOf(cId), LocalDate.now(), false, timeTrackingRepo).toString());
			}
		}
		
		return "main/mast";
	}
	
	@PostMapping("/{dep}/del-notif/{id}")
	public String postLead(@PathVariable(value = "id") long id, @PathVariable(value = "dep") String dep, Model model,	
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		
		notifs.dellNotif(id);	
		return "redirect:/"+dep;
	}
	
	@GetMapping("/info")
	public String getInfo(Model model,
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
				
		
		return "main/info";
	}	
}
