package com.stst.head.controllers;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.stst.head.models.Employee;
import com.stst.head.models.User;
import com.stst.head.repos.EmployeeRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Notifs;
import com.stst.head.services.PasswordHashing;

@Controller
public class LoginController {
	
	@Autowired
	private Access access;
	@Autowired
	private EmployeeRepo employeeRepo;
	
	@GetMapping("/login")
	public String getLogin() {
		return "login/login";
	}
	
	@PostMapping("/login")
    public String postLogin(HttpServletResponse response, HttpServletRequest request, Model model){
		String nickname = request.getParameter("nickname").trim();
		char[] password = request.getParameter("password").toCharArray();
		String hashedPass = PasswordHashing.hashPassword(password);
		Arrays.fill(password, '0');
		
    	// if there is no such user
		Optional<Employee> оptEmployee = employeeRepo.findByNickname(nickname);
    	if (оptEmployee.isEmpty()) {
    		model.addAttribute("info","Такого пользователя нет!");
        	return "login/login";
		}
    	Employee employee = оptEmployee.get();
    	// if the password does not fit
    	if (!hashedPass.equals( employee.getPassword() )) {
			model.addAttribute("info","Неправильный пароль!");
			model.addAttribute("nickname",nickname);
			return "login/login";
		}
    	// everything is successful
    	Integer twoFactorKey = new Random().nextInt(9000) + 1000;
    	employee.setTwoFactorKey(twoFactorKey);
    	employeeRepo.save(employee);
    	boolean allRight = Notifs.sendTg(twoFactorKey + "", employee);
    	
    	if (!allRight) {
    		model.addAttribute("info", "Ошибка при отправке кода!");
    		return "login/login";
		}
    	
    	int nowTime = LocalTime.now().getHour();
    	String greeting = "Здравствуй";
    	if (nowTime >= 6 && nowTime < 12) {
    		greeting = "Доброе утро";
		} else if (nowTime >= 12 && nowTime < 18) {
			greeting = "Добрый день";
		} else if (nowTime >= 18 && nowTime < 22) {
			greeting = "Добрый вечер";
		} else if (nowTime >= 22 || nowTime < 6) {
			greeting = "Доброй ночи";
		}
    	model.addAttribute("info", greeting + ", " + employee.getName());
    	
    	model.addAttribute("id", employee.getId());
	    return "login/login2";
    }
	
	@PostMapping("/login2")
    public String postLogin2(HttpServletResponse response, HttpServletRequest request, Model model){
		Integer twoFactorKey = Integer.valueOf( request.getParameter("twoFactorKey").trim() );
		Long id = Long.valueOf( request.getParameter("id") );

		Employee employee = employeeRepo.findById(id).get();
    	if (twoFactorKey == null || employee.getTwoFactorKey() == null || !twoFactorKey.equals(employee.getTwoFactorKey()) ) {
			model.addAttribute("info","Неправильный код! Зайдите снова");
			model.addAttribute("nickname", employee.getNickname());
			employee.setTwoFactorKey(null);
	    	employeeRepo.save(employee);
			return "login/login";
		}
    	// everything is successful
    	employee.setTwoFactorKey(null);
    	employeeRepo.save(employee);
    	
		Cookie cookie1 = new Cookie("id", Long.toString(id));
		Cookie cookie2 = new Cookie("password", employee.getPassword());
		Cookie cookie3 = new Cookie("secretnum", employee.getSecretnum() + "");
		Cookie cookie4 = new Cookie("depart", employee.getDepart().getAbbr());
		cookie1.setPath("/");
		cookie2.setPath("/");
		cookie3.setPath("/");
		cookie4.setPath("/");
		cookie1.setMaxAge(1814400); // 3 weeks
		cookie2.setMaxAge(1814400); // 3 weeks
		cookie3.setMaxAge(1814400); // 3 weeks
		cookie4.setMaxAge(1814400); // 3 weeks
		response.addCookie(cookie1);
		response.addCookie(cookie2);
		response.addCookie(cookie3);
		response.addCookie(cookie4);
		
	    return "redirect:/"+employee.getDepart().getAbbr();
    }
	
	@GetMapping("/")
    public String logout(HttpServletResponse response){
    	// reset cookies
		Cookie cookie1 = new Cookie("id", "");
		Cookie cookie2 = new Cookie("password", "");
		Cookie cookie3 = new Cookie("secretnum", "");
		Cookie cookie4 = new Cookie("depart", "");
		cookie1.setPath("/");
		cookie2.setPath("/");
		cookie3.setPath("/");
		cookie4.setPath("/");
        cookie1.setMaxAge(0);
        cookie2.setMaxAge(0);
        cookie3.setMaxAge(0);
        cookie4.setMaxAge(0);
        response.addCookie(cookie1);
		response.addCookie(cookie2);
		response.addCookie(cookie3);
		response.addCookie(cookie4);
    	return "redirect:/login";
    }
	
	@GetMapping("/who")
    public String logout(Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) throws Exception {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "All") ) {
					return "redirect:/";
				}
		return "redirect:/"+cDepart;
    }
	
}
