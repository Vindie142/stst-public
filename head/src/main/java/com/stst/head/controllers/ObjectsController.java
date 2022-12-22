package com.stst.head.controllers;

import java.io.IOException;
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

import com.stst.head.models.CityEnum;
import com.stst.head.models.DepartEnum;
import com.stst.head.models.Employee;
import com.stst.head.models.Sobject;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.SobjectRepo;
import com.stst.head.repos.TaskRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;
import com.stst.head.services.Notifs;
import com.stst.head.services.YandexDisk;
import com.stst.head.services.YandexDisk.YandexDiskException;

@Controller
public class ObjectsController {

	@Autowired
	private Access access;
	@Autowired
	private Notifs notifs;
	@Autowired
	private YandexDisk yandexDisk;
	@Autowired
	private EmployeeRepo employeeRepo;
	@Autowired
	private SobjectRepo objectRepo;
	@Autowired
	private TaskRepo taskRepo;
	
	@GetMapping("/my-objects")
	public String getMyObjects(Model model,		
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
		model.addAttribute("objects", objectRepo.findAll4(Long.valueOf(cId)).stream().sorted().toList());
		model.addAttribute("objects2", objectRepo.findAll3());
		model.addAttribute("employees", employeeRepo.findAll2(DepartEnum.getEnumsByAbbrs("lead", "mast", "pto", "sales")));
		return "objects/myObjects";
	}
	
	@GetMapping("/all-objects")
	public String getAllObjects(Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", cDepart.equals("pto") || cDepart.equals("mast") ? "/my-objects" : "/who");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		CityEnum employeeCity =	employeeRepo.findById(Long.valueOf(cId)).get().getCity();
		model.addAttribute("city", employeeCity);		
		model.addAttribute("cities", CityEnum.getMap());
		model.addAttribute("objects", objectRepo.findAll2(null, null, employeeCity).stream().sorted().toList());
		model.addAttribute("objects2", objectRepo.findAll3());
		model.addAttribute("employees", employeeRepo.findAll2(DepartEnum.getEnumsByAbbrs("lead", "mast", "pto", "sales")));
		return "objects/allObjects";
	}
	
	@GetMapping("/objects/new-object")
	public String getNewObject(HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/my-objects");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "pto") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("masters", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("mast").get()));
				model.addAttribute("saleses", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("sales").get()));
				
		return "objects/NewObject";
	}
	
	@PostMapping("/objects/new-object")
	public String postNewObject(HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/my-objects");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "pto") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("departs", DepartEnum.getMap());
				model.addAttribute("cities", CityEnum.getMap());	
				
		String street = request.getParameter("street").trim();
		String district = request.getParameter("district").trim();
		String building = request.getParameter("building").trim();
		String city = request.getParameter("city").trim();
		Byte status = Byte.valueOf( request.getParameter("status") );
		
		String custName = request.getParameter("custName").trim();
		String custPhone = request.getParameter("custPhone").trim();
		String custEmail = request.getParameter("custEmail").trim();
		String note = request.getParameter("note").trim();
		String works = request.getParameter("works").trim();
		
		Long master = Etc.idFromDatalist(request.getParameter("master"));
		Long salesman = Etc.idFromDatalist(request.getParameter("salesman"));

		// checking for the existence of the city
		Optional<CityEnum> оptCityEnum = CityEnum.getEnumByAbbr(city);
		if (оptCityEnum.isEmpty()) {
			model.addAttribute("info", "Город не существует!");
			return "employees/newEmployee";
		}
		Employee Epto = employeeRepo.findById(Long.valueOf(cId)).get();
		Employee Emaster = master == null ? null : employeeRepo.findById(master).orElse(null);
		Employee Esalesman = salesman == null ? null : employeeRepo.findById(salesman).orElse(null);
		Sobject object = new Sobject(оptCityEnum.get(), street, district, building, custName, Etc.standardizePhone(custPhone), custEmail, note, works,
					Epto, Emaster, Esalesman, status);
		object.addAction("Статус " + status + " - " + employeeRepo.findById2(Long.valueOf(cId)).get().getNormName());
		objectRepo.save(object);
		model.addAttribute("info", "Успешно создан!");
		
		if (Emaster != null) { // if the master has created, then we notify him
			notifs.sendNotif(Epto, "Вы назначены на объект " + object.getNormName() + ". Инженер ПТО: " + object.getPto().getNormName(), "/objects/object/" + object.getId());
		}
		try {
			yandexDisk.newFolder(object.getNormName());
			try {
				yandexDisk.publishFolder(object.getNormName());
			} catch (YandexDiskException ye) {
				ye.printStackTrace();
				object.setNote(object.getNote() + "<<Объект создан, папка на Я.Диск создана, но не опубликована: " + ye.getMessage() +">>");
				Notifs.sendTgToAdminChat("<<" + employeeRepo.findById(Long.valueOf(cId)).get().getNormName() + ">> <<Объект " + object.getNormName() + " создан, папка на Я.Диск создана, но не опубликована: " + ye.getMessage() +">>");
				objectRepo.save(object);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return "error";
			}
		} catch (YandexDiskException ye) {
			ye.printStackTrace();
			object.setNote(object.getNote() + "<<Объект создан, но при создании папки в Я.Диск произошла ошибка: " + ye.getMessage() +">>");
			Notifs.sendTgToAdminChat("<<" + employeeRepo.findById(Long.valueOf(cId)).get().getNormName() + ">> <<Объект " + object.getNormName() + " создан, но при создании папки в Я.Диск произошла ошибка: " + ye.getMessage() + ">>");
			objectRepo.save(object);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "error";
		}
		
		return "redirect:/objects/object/" + object.getId();	
	}
	
	@GetMapping("/objects/object/{id}")
	public String getObject(@PathVariable(value = "id") Long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", cDepart.equals("pto") || cDepart.equals("mast") ? "/my-objects" : "/all-objects");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Sobject> oObject = objectRepo.findById11(id);
				if (oObject.isEmpty()) {
					return "error";
				}
				Sobject object = oObject.get();
				model.addAttribute("arrayStatus", object.getStatuses());
		
		model.addAttribute("tasks", taskRepo.findAll2( object.getId(), Long.valueOf(cId) ).stream().sorted().toList());
		model.addAttribute("employees", employeeRepo.findAll3());
		model.addAttribute("object", object);
		return "objects/object";
	}
	
	@PostMapping("/objects/object/{id}")
	public String postObject(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/my-objects");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "pto", "sales", "mast", "buh") ) {
					return "error";
				}
		// Prefix
				Optional<Sobject> oObject = objectRepo.findById11(id);
				if (oObject.isEmpty()) {
					return "error";
				}
				Sobject object = oObject.get();
				
		switch ( request.getParameter("typeForm") ) {
			case "action" -> { // adding an action
				String action = request.getParameter("action");
				object.addAction(action);
				objectRepo.save(object);
			    break;
			}
			case "status" -> { // status changing
				if (!object.getStatuses().containsKey( Byte.valueOf(request.getParameter("012")) )) {
					object.addStatus(request.getParameter("012") == null ? null : Byte.valueOf(request.getParameter("012")), LocalDate.now());
				}
				
				for (Byte i = 3; i < 8; i++) {
					String checked = request.getParameter(Byte.toString(i));
					if (checked != null && !object.getStatuses().containsKey(i) && object.getStatuses().containsKey( Byte.valueOf(i-1+"") )) {
						object.addStatus(i, LocalDate.now());
					} else if (checked == null && object.getStatuses().containsKey(i) && !object.getStatuses().containsKey( Byte.valueOf(i+1+"") )) {
						object.addStatus(i, null);
					}
				}
				for (Byte i = 8; i < 10; i++) {
					String checked = request.getParameter(Byte.toString(i));
					if (checked != null && !object.getStatuses().containsKey(i) ) {
						object.addStatus(i, LocalDate.now());
					} else if (checked == null && object.getStatuses().containsKey(i) ) {
						object.addStatus(i, null);
					}
				}
				if ( !object.getCurrStatus().equals(objectRepo.findById11(id).get().getCurrStatus()) ) {
					object.addAction("Статус " + object.getCurrStatus() + " - " + employeeRepo.findById2(Long.valueOf(cId)).get().getNormName());
				}
				objectRepo.save(object);
				break;
			}
		};
		
		
		model.addAttribute("object", object);
		return "redirect:" + "/objects/object/" + id;
	}
	
	@GetMapping("/objects/object/{id}/print")
	public String getObjectPrint(@PathVariable(value = "id") Long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Sobject> oObject = objectRepo.findById11(id);
				if (oObject.isEmpty()) {
					System.out.println("oObject.isEmpty()");
					return "error";
				}
				Sobject object = oObject.get();
				model.addAttribute("arrayStatus", object.getStatuses());
		
		model.addAttribute("tasks", taskRepo.findAll2( object.getId(), Long.valueOf(cId) ).stream().sorted().toList());
		model.addAttribute("employees", employeeRepo.findAll3());
		model.addAttribute("object", object);
		return "objects/objectPrint";
	}
	
	@GetMapping("/objects/object/{id}/to-yandex")
	public String getObjectToYandex(@PathVariable(value = "id") Long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Sobject> oObject = objectRepo.findById11(id);
				if (oObject.isEmpty()) {
					return "error";
				}
				Sobject object = oObject.get();
				
		try {
			return "redirect:" + yandexDisk.getFolderLink(object.getNormName());
		} catch (IOException | InterruptedException | YandexDiskException e) {
			model.addAttribute("linkToYandexDisk", "");
			System.err.println("Ошибка при попытке получить ссылку на Я.Диск на объект: " + object.getNormName());
			Notifs.sendTgToAdminChat("<<" + employeeRepo.findById(Long.valueOf(cId)).get().getNormName() + ">> <<Ошибка при попытке получить ссылку на Я.Диск на объект: " + object.getNormName() + e.getMessage() + ">>");
			e.printStackTrace();
			return "error";
		}
	}
	
	@GetMapping("/objects/object/edit/{id}")
	public String getEditObject(@PathVariable(value = "id") long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/objects/object/"+id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "pto") ) {
					return "error";
				}
		// Prefix
				Optional<Sobject> oObject = objectRepo.findById2(id);
				if (oObject.isEmpty()) {
					return "error";
				}
				Sobject object = oObject.get();
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("arrayStatus", object.getStatuses());
				model.addAttribute("ptos", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("pto").get()));
				model.addAttribute("masters", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("mast").get()));
				model.addAttribute("saleses", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("sales").get()));
		
		model.addAttribute("object", object);
		return "objects/editObject";
	}
	
	@PostMapping("/objects/object/edit/{id}")
	public String postEditObject(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/objects/object/"+id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "pto") ) {
					return "error";
				}
		// Prefix
				Optional<Sobject> oObject = objectRepo.findById2(id);
				if (oObject.isEmpty()) {
					return "error";
				}
				Sobject object = oObject.get();
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("arrayStatus", object.getStatuses());
				model.addAttribute("ptos", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("pto").get()));
				model.addAttribute("masters", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("mast").get()));
				model.addAttribute("saleses", employeeRepo.getByDepart1(DepartEnum.getEnumByAbbr("sales").get()));
		
		String street = request.getParameter("street").trim();
		String district = request.getParameter("district").trim();
		String building = request.getParameter("building").trim();
		String city = request.getParameter("city").trim();
		
		String custName = request.getParameter("custName").trim();
		String custPhone = request.getParameter("custPhone").trim();
		String custEmail = request.getParameter("custEmail").trim();
		String note = request.getParameter("note").trim();
		String works = request.getParameter("works").trim();
		
		Long pto = Etc.idFromDatalist(request.getParameter("pto"));
		Long master = Etc.idFromDatalist(request.getParameter("master"));
		Long salesman = Etc.idFromDatalist(request.getParameter("salesman"));

		// checking for the existence of the city
		Optional<CityEnum> оptCityEnum = CityEnum.getEnumByAbbr(city);
		if (оptCityEnum.isEmpty()) {
			model.addAttribute("info", "Город не существует!");
			return "objects/editObject";
		}
		Employee Epto = employeeRepo.findById(pto).get();
		Employee Emaster = master == null ? null : employeeRepo.findById(master).orElse(null);
		Employee Esalesman = salesman == null ? null : employeeRepo.findById(salesman).orElse(null);
		
		model.addAttribute("info", "Успешно изменён!");
		if (!object.getStreet().equals(street) || !object.getBuilding().equals(building)) {
			try {
				yandexDisk.editFolderName(object.getNormName(), Sobject.getNormName(object.getId(), street, building));
			} catch (YandexDiskException ye) {
				ye.printStackTrace();
				object.setNote(object.getNote() + "<<При изменении имени папки в Я.Диск произошла ошибка:>>");
				model.addAttribute("info", "<<При изменении имени папки в Я.Диск произошла ошибка: " + ye.getMessage() +">>");
				objectRepo.save(object);
				Notifs.sendTgToAdminChat("<<" + employeeRepo.findById(Long.valueOf(cId)).get().getNormName() + ">> <<При изменении имени папки у объекта " + object.getNormName() + " в Я.Диск произошла ошибка: " + ye.getMessage() +">>");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return "error";
			}
		}
		
		if (Epto != null && object.getPto().getId() != Epto.getId()) { // if the pto has changed, then we notify him
			notifs.sendNotif(Epto, "Вам передан объект " + object.getNormName() + " от " + object.getPto().getNormName(), "/objects/object/" + object.getId());
		}
		if ( Emaster != null && (object.getMaster() == null || object.getMaster().getId() != Emaster.getId()) ) { // if the master has changed, then we notify him
			notifs.sendNotif(Epto, "Вы назначены на объект " + object.getNormName() + ". Инженер ПТО: " + object.getPto().getNormName(), "/objects/object/" + object.getId());
		}
		
		object.setStreet(street);
		object.setDistrict(district);
		object.setBuilding(building);
		object.setCustName(custName);
		object.setCustEmail(custEmail);
		object.setCustPhone(Etc.standardizePhone(custPhone));
		object.setNote(note);
		object.setWorks(works);
		object.setCity(оptCityEnum.get());
		object.setPto(Epto);
		object.setMaster(Emaster);
		object.setSalesman(Esalesman);
		objectRepo.save(object);
		
		
		model.addAttribute("object", object);
		return "objects/editObject";
	}
	
	@PostMapping("/objects/find-objects")
	public String postFindObjects(HttpServletRequest request) {
		return "redirect:/objects/object/" + Etc.idFromDatalist(request.getParameter("object"));
	}
}
