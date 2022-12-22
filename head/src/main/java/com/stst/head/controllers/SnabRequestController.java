package com.stst.head.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.stst.head.models.DepartEnum;
import com.stst.head.models.Employee;
import com.stst.head.models.SnabPoint;
import com.stst.head.models.SnabRequest;
import com.stst.head.models.Sobject;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.PriceMatRepo;
import com.stst.head.repos.SnabPointRepo;
import com.stst.head.repos.SnabRequestRepo;
import com.stst.head.repos.SobjectRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;
import com.stst.head.services.Notifs;

@Controller
public class SnabRequestController {
	
	@Autowired
	private Access access;
	@Autowired
	private Notifs notifs;
	@Autowired
	private EmployeeRepo employeeRepo;
	@Autowired
	private SnabRequestRepo snabRequestRepo;
	@Autowired
	private SnabPointRepo snabPointRepo;
	@Autowired
	private SobjectRepo objectRepo;
	@Autowired
	private PriceMatRepo priceMatRepo;
	
	@GetMapping("/objects/object/{id}/snab-requests")
	public String getSnabRequestsInObject(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/objects/object/" + id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab" ) ) {
					return "error";
				}
				
				Optional<Sobject> oObject = objectRepo.findById3(id);
				if (oObject.isEmpty()) {
					System.out.println("oObject.isEmpty()");
					return "error";
				}
				Sobject object = oObject.get();
				model.addAttribute("object", object);
				
		model.addAttribute("snabRequests", snabRequestRepo.findAll6(id).stream().sorted().toList());
		model.addAttribute("ptos", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("pto").get()));
		model.addAttribute("snabs", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("snab").get()));
		model.addAttribute("back2", "/objects/object/" + id + "/snab-requests");
		return "snabRequest/snabRequestsInObject";
	}
	
	@GetMapping("/snab-requests")
	public String getSnabRequests(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/who");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab" ) ) {
					return "error";
				}
				
		model.addAttribute("snabRequests", snabRequestRepo.findAll2(Long.valueOf(cId)).stream().sorted().toList());
		model.addAttribute("ptos", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("pto").get()));
		model.addAttribute("snabs", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("snab").get()));
		model.addAttribute("back2", "/snab-requests");
		return "snabRequest/snabRequests";
	}
	
	@GetMapping("/snab-requests/new-snab-request")
	public String getNewSnabRequest(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto") ) {
					return "error";
				}
		
		model.addAttribute("objects", objectRepo.findAll5( (byte)4 ));
		return "snabRequest/newSnabRequest";
	}
	
	@PostMapping("/snab-requests/new-snab-request")
	public String postNewSnabRequest(HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto") ) {
					return "error";
				}
		
		Long objectId = Etc.idFromDatalist(request.getParameter("object"));
		LocalDate date = request.getParameter("date") == null || request.getParameter("date").equals("") ? null : LocalDate.parse(request.getParameter("date"));
		String note = request.getParameter("note");
		
		Sobject object = objectRepo.findById(objectId).get();
		SnabRequest snabRequest = new SnabRequest(employeeRepo.findById(Long.valueOf(cId)).get(), object, date, note);
		
		Employee me = employeeRepo.findById(Long.valueOf(cId)).get();
		if (me.getDepart() == DepartEnum.PTO || me.getDepart() == DepartEnum.LEAD) {
			Employee checker = me;
			snabRequest.setChecker(checker);
			snabRequest.setChecked(true);
			if (me.getDepart() == DepartEnum.LEAD) {
				Employee approver = me;
				snabRequest.setApprover(approver);
				snabRequest.setApproved(true);
			}
		}
		snabRequestRepo.save(snabRequest);
		
		return "redirect:" + request.getParameter("back");	
	}
	
	@GetMapping("/snab-requests/snab-request/{id}")
	public String getSnabRequest(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back2") == null ? "/snab-requests" : request.getParameter("back2"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
		// Prefix
				SnabRequest snabRequest = snabRequestRepo.findById1(id).get();
				model.addAttribute("back2", request.getParameter("back2"));
				model.addAttribute("snabRequest", snabRequest);
				model.addAttribute("priceMats", priceMatRepo.findAll2(snabRequest.getObject().getCity()));
				model.addAttribute("ptos", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("pto").get()));
				model.addAttribute("snabs", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("snab").get()));
				
		return "snabRequest/snabRequest";	
	}
	
	@GetMapping("/snab-requests/snab-request/{id}/print")
	public String getSnabRequestPrint(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
		// Prefix
				SnabRequest snabRequest = snabRequestRepo.findById1(id).get();
				model.addAttribute("snabRequest", snabRequest);
		return "snabRequest/snabRequestPrint";	
	}
	
	@PostMapping("/snab-requests/snab-request/{id}/new-snab-point")
	public String postNewSnabPoint(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
		// Prefix
				SnabRequest snabRequest = snabRequestRepo.findById1(id).get();
				
		Long priceMatId = Etc.idFromDatalist(request.getParameter("mats"));
		BigDecimal amount = new BigDecimal(request.getParameter("amount")).setScale(SnabPoint.NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_AMOUNT);
		String note = request.getParameter("note");
		
		snabRequest.addSnabPoint(priceMatRepo.findById(priceMatId).get(), amount, note);
		snabRequestRepo.save(snabRequest);
		
		return "redirect:/snab-requests/snab-request/" + id;	
	}
	
	@GetMapping("/snab-requests/snab-request/{id}/edit")
	public String getEditSnabRequest(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
		// Prefix
				SnabRequest snabRequest = snabRequestRepo.findById(id).get();
				model.addAttribute("snabRequest", snabRequest);
		return "snabRequest/editSnabRequest";
	}
	
	@PostMapping("/snab-requests/snab-request/{id}/edit")
	public String postEditSnabRequest(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
			
		LocalDate date = request.getParameter("date") == null || request.getParameter("date").equals("") ? null : LocalDate.parse(request.getParameter("date"));
		String note = request.getParameter("note");
		
		SnabRequest snabRequest = snabRequestRepo.findById(id).get();
		snabRequest.setDateTo(date);
		snabRequest.setNote(note);
		snabRequestRepo.save(snabRequest);
				
		return "redirect:/snab-requests/snab-request/" + id;
	}
	
	@PostMapping("/snab-requests/snab-request/{id}/delete-snab-point/{snabPointId}")
	public String postDeleteSnabPoint(@PathVariable(value = "id") Long id, @PathVariable(value = "snabPointId") Long snabPointId, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto") ) {
					return "error";
				}
		// Prefix
				SnabPoint snabPoint = snabPointRepo.findById(snabPointId).get();
				snabPointRepo.delete(snabPoint);
		return "redirect:/snab-requests/snab-request/" + id;
	}
	
	@GetMapping("/snab-requests/snab-request/{id}/edit-snab-point/{snabPointId}")
	public String getEditSnabPoint(@PathVariable(value = "id") Long id, @PathVariable(value = "snabPointId") Long snabPointId, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/snab-requests/snab-request/" + id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto") ) {
					return "error";
				}
		// Prefix
				SnabPoint snabPoint = snabPointRepo.findById(snabPointId).get();
				model.addAttribute("snabPoint", snabPoint);
				model.addAttribute("priceMats", priceMatRepo.findAll2(snabPoint.getSnabRequest().getObject().getCity()));
		return "snabRequest/editSnabPoint";
	}
	
	@PostMapping("/snab-requests/snab-request/{id}/edit-snab-point/{snabPointId}")
	public String postEditSnabPoint(@PathVariable(value = "id") Long id, @PathVariable(value = "snabPointId") Long snabPointId, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
		// Prefix
				SnabPoint snabPoint = snabPointRepo.findById1(snabPointId).get();
				
		Long priceMatId = Etc.idFromDatalist(request.getParameter("mats"));
		BigDecimal amount = new BigDecimal(request.getParameter("amount")).setScale(SnabPoint.NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_AMOUNT);
		String note = request.getParameter("note");
		
		snabPoint.setAmount(amount);
		snabPoint.setNote(note);
		snabPoint.setPriceMat(priceMatRepo.findById(priceMatId).get());
		snabPointRepo.save(snabPoint);
		
		return "redirect:/snab-requests/snab-request/" + id;	
	}
	
	@PostMapping("/snab-requests/snab-request/{id}/send")
	public String postSendSnabRequest(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "mast", "pto", "snab") ) {
					return "error";
				}
		// Prefix
				SnabRequest snabRequest = snabRequestRepo.findById1(id).get();
			
		switch ( request.getParameter("action") ) {
			case "to_checker" -> {
				Employee checker = snabRequest.getObject().getPto();
				snabRequest.setChecker(checker);
				snabRequestRepo.save(snabRequest);
				
				Employee me = employeeRepo.findById(Long.valueOf(cId)).get();
				notifs.sendNotif(checker, me.getNormName() + " отправил на проверку заявку по объекту: " + snabRequest.getObject().getNormName(), "/snab-requests/snab-request/" + snabRequest.getId());
			    break;
			}
			case "check" -> {
				Employee checker = employeeRepo.findById(Long.valueOf(cId)).get();
				snabRequest.setChecker(checker);
				snabRequest.setChecked(true);
				snabRequestRepo.save(snabRequest);
			    break;
			}
			case "to_approver" -> {
				Long approverId = Etc.idFromDatalist(request.getParameter("approver"));
				Employee approver = employeeRepo.findById(approverId).get();
				snabRequest.setApprover(approver);
				snabRequestRepo.save(snabRequest);
				
				Employee me = employeeRepo.findById(Long.valueOf(cId)).get();
				notifs.sendNotif(approver, me.getNormName() + " отправил на утверждение заявку по объекту: " + snabRequest.getObject().getNormName(), "/snab-requests/snab-request/" + snabRequest.getId());
			    break;
			}
			case "approve" -> {
				Employee approver = employeeRepo.findById(Long.valueOf(cId)).get();
				snabRequest.setApprover(approver);
				snabRequest.setApproved(true);
				snabRequestRepo.save(snabRequest);

				notifs.sendNotif(snabRequest.getObject().getPto(), "Утверждена заявка по объекту: " + snabRequest.getObject().getNormName(), "/snab-requests/snab-request/" + snabRequest.getId());
			    break;
			}
			case "to_snab" -> {
				Long snabId = Etc.idFromDatalist(request.getParameter("snab"));
				Employee snab = employeeRepo.findById(snabId).get();
				snabRequest.setSnab(snab);
				snabRequestRepo.save(snabRequest);

				Employee me = employeeRepo.findById(Long.valueOf(cId)).get();
				notifs.sendNotif(snab, "Новая заявка по объекту: " + snabRequest.getObject().getNormName() + ". От: " + me.getNormName(), "/snab-requests/snab-request/" + snabRequest.getId());
			    break;
			}
			case "accept" -> {
				Employee snab = employeeRepo.findById(Long.valueOf(cId)).get();
				snabRequest.setSnab(snab);
				snabRequest.setAccepted(true);
				snabRequest.setDateFromNow();
				snabRequestRepo.save(snabRequest);

				notifs.sendNotif(snabRequest.getObject().getPto(), "Принята заявка по объекту: " + snabRequest.getObject().getNormName(), "/snab-requests/snab-request/" + snabRequest.getId());
			    break;
			}
			case "done" -> {
				Employee snab = employeeRepo.findById(Long.valueOf(cId)).get();
				snabRequest.setSnab(snab);
				snabRequest.setDone(true);
				snabRequestRepo.save(snabRequest);
				break;
			}
		};
				
		return "redirect:" + request.getParameter("back");
	}
	
}
