package com.stst.head.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import com.stst.head.models.Sobject;
import com.stst.head.models.Task;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.SobjectRepo;
import com.stst.head.repos.TaskRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;
import com.stst.head.services.Notifs;

@Controller
public class TaskController {
	
	@Autowired
	private Access access;
	@Autowired
	private Notifs notifs;
	@Autowired
	private SobjectRepo objectRepo;
	@Autowired
	private EmployeeRepo employeeRepo;
	@Autowired
	private TaskRepo taskRepo;
	
	@GetMapping("/tasks")
	public String getTasks(Model model,		
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
		model.addAttribute("userId", cId);
		model.addAttribute("tasks", taskRepo.findAll1(Long.valueOf(cId)).stream().sorted().toList());
		model.addAttribute("employees", employeeRepo.findAll3());
		model.addAttribute("objects", objectRepo.findAll3());
		return "tasks/tasks";
	}
	
	@PostMapping("/tasks/new-task")
	public String postNewTask(HttpServletRequest request,	
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		
		String text = request.getParameter("text").trim();
		Long objectId = Etc.idFromDatalist( request.getParameter("object") );
		Long employeeTo = Etc.idFromDatalist( request.getParameter("employeeTo") );
		
		Employee sender = employeeRepo.findById(Long.valueOf(cId)).get();
		Employee owner = employeeTo == null ? employeeRepo.findById(Long.valueOf(cId)).get() : employeeRepo.findById(Long.valueOf(employeeTo)).get();
		Sobject object = objectId == null ? null : objectRepo.findById(objectId).orElse(null);
		
		if (owner.getId() == sender.getId()) {
			sender = null;
		}
		
		Task task = new Task(owner, sender, object, text);
		taskRepo.save(task);
		
		
		if (sender != null && owner.getId() != sender.getId()) {
			notifs.sendNotif(owner, "Новая задача от " + sender.getNormName() + ": " + text, "/tasks");
		}
		return "redirect:" + request.getParameter("back");
	}
	
	@GetMapping("/tasks/transfer/{id}")
	public String getTasksTransfer(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask = taskRepo.findById(id);
				if (oTask.isEmpty()) {
					return "error";
				}
				Task task = oTask.get();
				if (task.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}

		model.addAttribute("task", task);
		model.addAttribute("employees", employeeRepo.findAll3());
		model.addAttribute("back", request.getParameter("back"));
		return "tasks/transferTask";
	}
	
	@PostMapping("/tasks/transfer/{id}")
	public String postTasksTransfer(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask = taskRepo.findById(id);
				if (oTask.isEmpty()) {
					return "error";
				}
				Task task = oTask.get();
				if (task.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}
		
		Long employeeTo = Etc.idFromDatalist( request.getParameter("employeeTo") );
		Employee newOwner = employeeRepo.findById(Long.valueOf(employeeTo)).get();
		Employee oldOwner = task.getOwner();
		
		task.setOwner(newOwner);
		task.setSender(oldOwner);
		taskRepo.save(task);
		
		notifs.sendNotif(newOwner, "Вам передана задача от " + oldOwner.getNormName() + ": " + task.getText(), "/tasks");
		
		return "redirect:" + request.getParameter("back");
	}
	
	@GetMapping("/tasks/edit/{id}")
	public String getTasksEdit(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask = taskRepo.findById(id);
				if (oTask.isEmpty()) {
					return "error";
				}
				Task task = oTask.get();
				if (task.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}

		model.addAttribute("task", task);
		model.addAttribute("objects", objectRepo.findAll3());
		model.addAttribute("back", request.getParameter("back"));
		return "tasks/editTask";
	}
	
	@PostMapping("/tasks/edit/{id}")
	public String postTasksEdit(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask = taskRepo.findById(id);
				if (oTask.isEmpty()) {
					return "error";
				}
				Task task = oTask.get();
				if (task.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}
		
		String text = request.getParameter("text").trim();
		Long objectId = Etc.idFromDatalist( request.getParameter("object") );
				
		Sobject object = objectId == null ? null : objectRepo.findById(objectId).orElse(null);
				
		task.setText(text);
		task.setObject(object);
		taskRepo.save(task);
		
		return "redirect:" + request.getParameter("back");
	}
	
	@PostMapping("/tasks/done/{id}")
	public String postTasksDone(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask = taskRepo.findById(id);
				if (oTask.isEmpty()) {
					return "error";
				}
				Task task = oTask.get();
				if (task.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}
				
		if (task.getSender() != null) {
			String byObject = task.getObject() == null ? "" : ". По объекту: " + task.getObject().getNormName();
			notifs.sendNotif(task.getSender(), task.getOwner().getNormName() + " выполнил вашу задачу: " + task.getText() + byObject + ". Через " + ChronoUnit.DAYS.between(task.getBirthdate(), LocalDate.now()) + "д", null);
		}
		taskRepo.delete(task);
		
		return "redirect:" + request.getParameter("back");
	}
	
	@GetMapping("/tasks/message/{id}")
	public String getAsk(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask = taskRepo.findById(id);
				if (oTask.isEmpty()) {
					return "error";
				}
				Task task = oTask.get();
				if (task.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}

		model.addAttribute("task", task);
		model.addAttribute("back", request.getParameter("back"));
		return "tasks/message";
	}
	
	@PostMapping("/tasks/message/{id}")
	public String postAsk(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "error";
				}
		// Prefix
				Optional<Task> oTask1 = taskRepo.findById(id);
				if (oTask1.isEmpty()) {
					return "error";
				}
				Task task1 = oTask1.get();
				if (task1.getOwner().getId() != Long.valueOf(cId)) { // checking if user is owner of the task
					return "error";
				}
		
		String text = request.getParameter("text");
		if (request.getParameter("message") != null) {
			Task task2 = new Task(task1.getSender(), task1.getOwner(), task1.getObject(), text, true);
			taskRepo.save(task2);
			if (task1.isMessage() == true) {
				taskRepo.delete(task1);
			}
			notifs.sendNotif(task2.getOwner(), "Сообщение от " + task1.getOwner().getNormName()+ ": " + text, "/tasks");
		} else if (request.getParameter("refuse") != null) {
			taskRepo.delete(task1);
			notifs.sendNotif(task1.getSender(), "Отказ выполнять задачу: " + task1.getText() + ". По причине: " + text + ". От " + task1.getOwner().getNormName(), "/tasks");
		}
		
		return "redirect:" + request.getParameter("back");
	}
	
}
