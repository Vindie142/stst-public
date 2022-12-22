package com.stst.head.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.stst.head.models.CityEnum;
import com.stst.head.models.DepartEnum;
import com.stst.head.models.Employee;
import com.stst.head.models.TimeTracking;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.TimeTrackingRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;

@Controller
public class TimeController {
	
	@Autowired
	private Access access;
	@Autowired
	private EmployeeRepo employeeRepo;
	@Autowired
	private TimeTrackingRepo timeTrackingRepo;
	
	@GetMapping("/time/start-stop")
	public String getTimeStartStop(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		
		TimeTracking.validatingForCorrectness(Long.valueOf(cId), timeTrackingRepo, employeeRepo);		
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( Long.valueOf(cId) );
		if (oTime.isEmpty()) { // for the first time
			TimeTracking newTime = new TimeTracking(employeeRepo.findById2( Long.valueOf(cId) ).get());
			timeTrackingRepo.save(newTime);
			return "redirect:/who";
		}
		TimeTracking time = oTime.get();
		
		if (time.isOpen()) {
			time.endTime(Long.valueOf(cId), timeTrackingRepo, employeeRepo);
			timeTrackingRepo.save(time);
		} else {
			TimeTracking newTime = new TimeTracking(employeeRepo.findById2( Long.valueOf(cId) ).get());
			timeTrackingRepo.save(newTime);
		}
		
		return "redirect:/who";
	}
	
	@GetMapping("/time")
	public String getTime(Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/who");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm", "adm2") ) {
					return "error";
				}
		
				
		model.addAttribute("employees", employeeRepo.findAll3());
		model.addAttribute("cities", CityEnum.getMap());
		model.addAttribute("nowCity", employeeRepo.findById(Long.valueOf(cId)).get().getCity().getAbbr());
		return "time/time";
	}
	
	@GetMapping("/time/editTimes")
	public String getEditTimes(HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/time");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return "error";
				}
				
		Long employeeId = Etc.idFromDatalist( request.getParameter("employee").trim() );
		LocalDate dayDate = LocalDate.parse(request.getParameter("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		TimeTracking.validatingForCorrectness(employeeId, timeTrackingRepo, employeeRepo);
		
		List<TimeTracking> times = timeTrackingRepo.findAll2(employeeId, dayDate);
		model.addAttribute("times", times);
		model.addAttribute("employee", employeeRepo.findById2(employeeId).get());
		model.addAttribute("dayDate", dayDate.toString());
		model.addAttribute("workedTime", TimeTracking.getTimeOfTheWholeDay(employeeId, dayDate, true, timeTrackingRepo)+"ч");
		return "time/editTimes";
	}
	
	@PostMapping("/time/editTimes/{id}/edit")
	public RedirectView postEditTime(@PathVariable(value = "id") Long id, HttpServletRequest request, RedirectAttributes attributes,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return new RedirectView("/time/editTimes");
				}
		
		Long employeeId = Long.valueOf( request.getParameter("employeeId") );
		LocalDate dayDate = LocalDate.parse(request.getParameter("dayDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalTime startTime = LocalTime.parse(request.getParameter("time1"), DateTimeFormatter.ISO_LOCAL_TIME);
		LocalTime endTime = request.getParameter("time2") == null || request.getParameter("time2").equals("") ? null : LocalTime.parse(request.getParameter("time2"), DateTimeFormatter.ISO_LOCAL_TIME);
		
		if (endTime.compareTo(startTime) < 0) {
			endTime = startTime;
		}
		
		TimeTracking time = timeTrackingRepo.findById(id).get();
		time.setStartTime(startTime);
		time.setEndTime(endTime);
		timeTrackingRepo.save(time);
		
		attributes.addAttribute("employee", employeeRepo.findById2(employeeId).get().getNormNameWithId());
		attributes.addAttribute("date", dayDate.toString());
		return new RedirectView("/time/editTimes");
	}
	
	@PostMapping("/time/editTimes/new")
	public RedirectView postNewTime(HttpServletRequest request, RedirectAttributes attributes,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm2") ) {
					return new RedirectView("/time/editTimes");
				}
		
		Long employeeId = Long.valueOf( request.getParameter("employeeId") );
		LocalDate dayDate = LocalDate.parse(request.getParameter("dayDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalTime startTime = LocalTime.parse(request.getParameter("time1"), DateTimeFormatter.ISO_LOCAL_TIME);
		LocalTime endTime = LocalTime.parse(request.getParameter("time2"), DateTimeFormatter.ISO_LOCAL_TIME);
		
		TimeTracking newTime = new TimeTracking(employeeRepo.findById2(employeeId).get(), dayDate, startTime, endTime);
		timeTrackingRepo.save(newTime);
		
		attributes.addAttribute("employee", employeeRepo.findById2(employeeId).get().getNormNameWithId());
		attributes.addAttribute("date", dayDate.toString());
		return new RedirectView("/time/editTimes");
	}
	
	@GetMapping("/time/report")
	public String getNewTime(HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/time");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "adm", "adm2") ) {
					return "error";
				}
		
		CityEnum cityEnum = CityEnum.getEnumByAbbr( request.getParameter("city") ).get();
		BigDecimal standartHoursInDay = new BigDecimal( request.getParameter("standartHoursInDay") ).setScale(TimeTracking.NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_HOURS);
		BigDecimal standartHoursInMonth = new BigDecimal( request.getParameter("standartHoursInMonth") ).setScale(TimeTracking.NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_HOURS);
		LocalDate monthDate = LocalDate.parse(request.getParameter("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<Employee> employees = employeeRepo.findAll4(cityEnum);
		
		List<TimeTracking.ToPrint> toPrints = new ArrayList<TimeTracking.ToPrint>();
		for (Employee employee : employees) {
			TimeTracking.validatingForCorrectness(employee.getId(), timeTrackingRepo, employeeRepo);
			
			TimeTracking.ToPrint toPrint = new TimeTracking.ToPrint();
			toPrint.name = employee.getNormNameWithId();
			for (int i = 0; i < 31; i++) {
				String theDateY = monthDate.getYear()+"";
				String theDateM = monthDate.getMonthValue() < 10 ? "0"+monthDate.getMonthValue() : monthDate.getMonthValue()+"";
				String theDateD = i+1 < 10 ? "0"+(i+1) : i+1+"";
				LocalDate theDate = LocalDate.parse(theDateY + "-" + theDateM + "-" + theDateD, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				BigDecimal workedDay = new BigDecimal( TimeTracking.getTimeOfTheWholeDay(employee.getId(), theDate, true, timeTrackingRepo) ).setScale(TimeTracking.NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_HOURS);
				String[] hourAndExtraHour = new String[2];
				hourAndExtraHour[0] = workedDay.compareTo(new BigDecimal(0)) == 0 ? "-" : workedDay.toPlainString();
				hourAndExtraHour[1] = workedDay.compareTo(new BigDecimal(0)) == 0 ? "-" : workedDay.subtract(standartHoursInDay).toPlainString();
				toPrint.hoursAndExtraHours.add(hourAndExtraHour);
				toPrint.allHours = toPrint.allHours.add(workedDay);
			}
			toPrint.allExtraHours = ( toPrint.allHours.compareTo(new BigDecimal(0)) == 0 ? "-" : toPrint.allHours.subtract(standartHoursInMonth).toPlainString());
			toPrints.add(toPrint);
		}
		
		model.addAttribute("toPrints", toPrints);
		model.addAttribute("monthDate", (monthDate.getMonthValue() < 10 ? "0"+monthDate.getMonthValue() : monthDate.getMonthValue()+"") + "." + monthDate.getYear() );
		return "time/timeReportPrint";
	}
}
