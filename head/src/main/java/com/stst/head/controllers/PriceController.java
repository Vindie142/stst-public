package com.stst.head.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
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
import com.stst.head.models.MeasurementEnum;
import com.stst.head.models.PriceMat;
import com.stst.head.models.PriceWork;
import com.stst.head.models.Section;
import com.stst.head.models.Subsection;
import com.stst.head.models.User;
import com.stst.head.repos.PriceMatRepo;
import com.stst.head.repos.PriceWorkRepo;
import com.stst.head.repos.SectionRepo;
import com.stst.head.repos.SubsectionRepo;
import com.stst.head.services.Access;
import com.stst.head.services.Etc;

@Controller
public class PriceController {
	
	@Autowired
	private Access access;
	@Autowired
	private SectionRepo sectionRepo;
	@Autowired
	private SubsectionRepo subsectionRepo;
	@Autowired
	private PriceWorkRepo priceWorkRepo;
	@Autowired
	private PriceMatRepo priceMatRepo;
	
	@GetMapping("/price-list")
	public String getPriceList(Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/who");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		model.addAttribute("cities", CityEnum.getMap());	
		return "priceList/sections/menu";
	}
	
	
	
	
	
	
	
	
	// sections
	
	@GetMapping("/price-list/sections")
	public String getSections(Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		List<Section> sections = new ArrayList<Section>( sectionRepo.findAll1() );
		if (sections.size() > 0) { Collections.sort(sections); }
		model.addAttribute("sections", sections);
		return "priceList/sections/sections";
	}
	
	@PostMapping("/price-list/sections/new-section")
	public String postNewNewSection(HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		String name = request.getParameter("name").trim();

		Section section = new Section(name);
		section.setOrderNumByRightWay(-999, sectionRepo, true);
		sectionRepo.save(section);
		return "redirect:/price-list/sections";	
	}
	
	@GetMapping("/price-list/sections/section/{id}/delete")
	public String getDeleteSection(@PathVariable(value = "id") long id, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				Optional<Section> оptSection = sectionRepo.findById(id);
				if (оptSection.isEmpty()) {
					return "error";
				}
				model.addAttribute("section", оptSection.get());
		return "priceList/sections/deleteSection";
	}
	
	@PostMapping("/price-list/sections/section/{id}/delete")
	public String postDeleteSubsection(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				Optional<Section> оptSection = sectionRepo.findById(id);
				if (оptSection.isEmpty()) {
					return "error";
				}
		long id2 = Long.parseLong( request.getParameter("id2") ); // for comparison
		if ( !(id == id2) ) {
			return "error";
		}
		Section section = оptSection.get();
		if (subsectionRepo.findAll2(section.getId()).size() > 0) {
			model.addAttribute("section", section);
			model.addAttribute("info", "Невозможно удалить, так как есть подразделы!");
			return "priceList/sections/deleteSection";
		}
		sectionRepo.delete(section);
		return "redirect:/price-list/sections";
	}
	
	@GetMapping("/price-list/sections/section/{id}/edit")
	public String getEditSection(@PathVariable(value = "id") long id, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Section> оptSection = sectionRepo.findById(id);
				if (оptSection.isEmpty()) {
					return "error";
				}
				model.addAttribute("section", оptSection.get());
		return "priceList/sections/editSection";
	}
	
	@PostMapping("/price-list/sections/section/{id}/edit")
	public String postEditSection(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Section> оptSection = sectionRepo.findById(id);
				if (оptSection.isEmpty()) {
					return "error";
				}
		String name = request.getParameter("name");
		int orderNum = Integer.valueOf(request.getParameter("orderNum"));
		
		Section section = оptSection.get();
		section.setName(name);
		section.setOrderNumByRightWay(orderNum, sectionRepo, false);
		sectionRepo.save(section);
		return "redirect:/price-list/sections";
	}
	
	
	
	
	
	
	
	
	
	
	// subsections
	
	@GetMapping("/price-list/sections/section/{id}")
	public String getSubsections(@PathVariable(value = "id") long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		List<Subsection> subsections = new ArrayList<Subsection>( subsectionRepo.findAll1(id) );
		if (subsections.size() > 0) { Collections.sort(subsections); }
		model.addAttribute("subsections", subsections);
		model.addAttribute("id", id);	
		return "priceList/sections/subsections";
	}
	
	@PostMapping("/price-list/sections/section/{id}/new-subsection")
	public String postNewSubsection(@PathVariable(value = "id") long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		String name = request.getParameter("name").trim();

		Subsection subsection = new Subsection(sectionRepo.findById(id).get(), name);
		subsection.setOrderNumByRightWay(-999, subsectionRepo, true);
		subsectionRepo.save(subsection);
		return "redirect:/price-list/sections/section/" + id;	
	}
	
	@GetMapping("/price-list/sections/section/{id}/subsection/{subId}/delete")
	public String getDeleteSubsection(@PathVariable(value = "id") long id, @PathVariable(value = "subId") long subId, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections/section/" + id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Subsection> оptSubsection = subsectionRepo.findById(subId);
				if (оptSubsection.isEmpty()) {
					return "error";
				}
				model.addAttribute("subsection", оptSubsection.get());
				model.addAttribute("id", id);
		return "priceList/sections/deleteSubsection";
	}
	
	@PostMapping("/price-list/sections/section/{id}/subsection/{subId}/delete")
	public String postDeleteSubsection(@PathVariable(value = "id") long id, @PathVariable(value = "subId") long subId, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections/section/" + id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Subsection> оptSubsection = subsectionRepo.findById(subId);
				if (оptSubsection.isEmpty()) {
					return "error";
				}
		long subId2 = Long.parseLong( request.getParameter("id2") ); // for comparison
		if ( !(subId == subId2) ) {
			return "error";
		}
		Subsection subsection = оptSubsection.get();
		if (priceWorkRepo.findAll3(subsection.getId()).size() > 0) {
			model.addAttribute("subsection", subsection);
			model.addAttribute("info", "Невозможно удалить, так как есть позиции прайса!");
			return "priceList/sections/deleteSubsection";
		}
		subsectionRepo.delete(subsection);
		return "redirect:/price-list/sections/section/" + id;
	}
	
	@GetMapping("/price-list/sections/section/{id}/subsection/{subId}/edit")
	public String getEditSubsection(@PathVariable(value = "id") long id, @PathVariable(value = "subId") long subId, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/sections/section/" + id);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Subsection> оptSubsection = subsectionRepo.findById(subId);
				if (оptSubsection.isEmpty()) {
					return "error";
				}
				model.addAttribute("subsection", оptSubsection.get());
		
		model.addAttribute("sections", sectionRepo.findAll());
		model.addAttribute("id", id);
		return "priceList/sections/editSubsection";
	}
	
	@PostMapping("/price-list/sections/section/{id}/subsection/{subId}/edit")
	public String postEditSubsection(@PathVariable(value = "id") long id, @PathVariable(value = "subId") long subId, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				// checking for the existence of an employee
				Optional<Subsection> оptSubsection = subsectionRepo.findById(subId);
				if (оptSubsection.isEmpty()) {
					return "error";
				}
		String name = request.getParameter("name");
		int orderNum = Integer.valueOf(request.getParameter("orderNum"));
		Long sectionId = Long.valueOf( request.getParameter("section") );
		Optional<Section> oSection = sectionRepo.findById(sectionId);
		if (oSection.isEmpty()) {
			return "error";
		}
		Subsection subsection = оptSubsection.get();
		subsection.setName(name);
		subsection.setSection(oSection.get());
		subsection.setOrderNumByRightWay(orderNum, subsectionRepo, false);
		subsectionRepo.save(subsection);
		return "redirect:/price-list/sections/section/" + id;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// works
	
	@GetMapping("/price-list/works/{city}/sections")
	public String getSectionsInCityWorks(@PathVariable(value = "city") String city, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		List<Section> sections = new ArrayList<Section>( sectionRepo.findAll1() );
		if (sections.size() > 0) { Collections.sort(sections); }
		model.addAttribute("sections", sections);
		model.addAttribute("allPriceWorks", priceWorkRepo.findAll5(CityEnum.getEnumByAbbr(city).orElse(null)));
		return "priceList/works/priceListSections";
	}
	
	@GetMapping("/price-list/works/{city}/print")
	public String getInCityWorksPrint(@PathVariable(value = "city") String city, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/works/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		Map<String, List<PriceWork>> mapSectionsAndPriceWorks = new HashMap<String, List<PriceWork>>();
		List<PriceWork> priceWorks = priceWorkRepo.findAll6(CityEnum.getEnumByAbbr(city).orElse(null));
		List<Section> sections = sectionRepo.findAll1();
		if (sections.size() > 0) { Collections.sort(sections); }
		for (Section section : sections) { 
			mapSectionsAndPriceWorks.put(section.getName(), priceWorks.stream().filter(p -> p.getSubsection().getSection() != null && p.getSubsection().getSection().getId() == section.getId()).toList());
		}
		
		model.addAttribute("mapSectionsAndPriceWorks", mapSectionsAndPriceWorks);
		return "priceList/works/print";
	}
	
	@GetMapping("/price-list/works/{city}/to-edit")
	public String getToEditInCityWorks(@PathVariable(value = "city") String city, HttpServletRequest request,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
				String back = request.getParameter("back");
		Long id = Etc.idFromDatalist(request.getParameter("priceWork"));
		return "redirect:" + URI.create("/price-list/works/" + city + "/edit-price-work/" + id + "?back="  + back).toString();
	}
	
	@GetMapping("/price-list/works/{city}/sections/section/{id}")
	public String getSubsectionsInCityWorks(@PathVariable(value = "city") String city, @PathVariable(value = "id") long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/works/" + city + "/sections");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		List<Subsection> subsections = new ArrayList<Subsection>( subsectionRepo.findAll1(id) ); // id, name, section(id) and by section
		if (subsections.size() > 0) { Collections.sort(subsections); }
		model.addAttribute("sectionId", id);
		model.addAttribute("sectionName", sectionRepo.findById(id).get().getName());
		model.addAttribute("subsections", subsections);
		return "priceList/works/priceListSubsections";
	}
	
	@GetMapping("/price-list/works/{city}/sections/section/{sId}/subsection/{id}")
	public String getSubsectionPriceWorksInCity(@PathVariable(value = "city") String city, @PathVariable(value = "sId") long sectionId, @PathVariable(value = "id") long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/works/" + city + "/sections" + "/section/" + sectionId);
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		List<PriceWork> priceWorks = new ArrayList<PriceWork>( priceWorkRepo.findAll4(id, CityEnum.getEnumByAbbr(city).orElse(null)) );
		if (priceWorks.size() > 0) { Collections.sort(priceWorks, new PriceWork.PriceWorkComparatorByOrderNum()); }
		model.addAttribute("sectionId", sectionId);
		model.addAttribute("sectionName", sectionRepo.findById(sectionId).get().getName());
		model.addAttribute("subsectionId", id);
		model.addAttribute("subsectionName", subsectionRepo.findById(id).get().getName());
		model.addAttribute("priceWorks", priceWorks);
		return "priceList/works/priceListWorks";
	}
	
	@GetMapping("/price-list/works/{city}/new-price-work")
	public String getNewPriceWork(@PathVariable(value = "city") String city, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", city);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				model.addAttribute("priceMats", priceMatRepo.findAll5(CityEnum.getEnumByAbbr(city).orElse(null)));
				
		if (request.getParameter("section") != null && !request.getParameter("section").equals("-1")) { // if it's a request for a list of subcategories
			Long sectionId = Long.valueOf( request.getParameter("section") );
			model.addAttribute("subsections", subsectionRepo.findAll1(sectionId)); // id, name, section(id) and by section
			model.addAttribute("sectionId", sectionId);
			if (request.getParameter("subsection") != null && !request.getParameter("subsection").equals("-1")) {
				Long subsectionId = Long.valueOf( request.getParameter("subsection") );
				model.addAttribute("subsectionId", subsectionId);
			}
		}
		return "priceList/works/newPriceWork";
	}
	
	@PostMapping("/price-list/works/{city}/new-price-work")
	public String postNewPriceWork(@PathVariable(value = "city") String cityP, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", cityP);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				model.addAttribute("priceMats", priceMatRepo.findAll5(CityEnum.getEnumByAbbr(cityP).orElse(null)));
		
		String subsectionId = request.getParameter("subsection");
		String city = request.getParameter("city");
		String name = request.getParameter("name").trim();
		BigDecimal price = new BigDecimal( request.getParameter("price") );
		String measurement = request.getParameter("measurement");
		Long priceMatId = Etc.idFromDatalist(request.getParameter("priceMat"));
		
		PriceMat priceMat = priceMatId == null ? null : priceMatRepo.findById(priceMatId).get();
		
		// checking for the existence of the city
		Optional<CityEnum> оptCityEnum = CityEnum.getEnumByAbbr(city);
		Optional<MeasurementEnum> measurement2 = MeasurementEnum.getEnumByAbbr(measurement);
		
		PriceWork priceWork = new PriceWork(оptCityEnum.orElse(null), subsectionRepo.findById(Long.valueOf(subsectionId)).get(), name, price, measurement2.get(), priceMat);
		priceWork.setOrderNumByRightWay(-999, priceWorkRepo, true);
		priceWorkRepo.save(priceWork);
		
		model.addAttribute("info", "Успешно добавлен!");
		return "priceList/works/newPriceWork";	
	}
	
	
	@GetMapping("/price-list/works/{city}/edit-price-work/{id}")
	public String getEditPriceWork(@PathVariable(value = "city") String city, @PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", city);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				PriceWork priceWork = priceWorkRepo.findById(id).get();
				model.addAttribute("priceWork", priceWork);
				model.addAttribute("priceMats", priceMatRepo.findAll5(CityEnum.getEnumByAbbr(city).orElse(null)));
			
		if (request.getParameter("section") != null) { // if it's a request for a list of subcategories
			Long sectionId = Long.valueOf( request.getParameter("section") );
			model.addAttribute("editedSection", sectionId != priceWork.getSubsection().getSection().getId() ? true : false); // при выборе другого раздела
			model.addAttribute("subsections", subsectionRepo.findAll1(sectionId)); // id, name, section(id) and by section
			model.addAttribute("sectionId", sectionId);
			if (request.getParameter("subsection") != null) {
				Long subsectionId = Long.valueOf( request.getParameter("subsection") );
				model.addAttribute("subsectionId", subsectionId);
			}
		} else {
			model.addAttribute("editedSection", false); // при выборе другого раздела
			Long sectionId = priceWork.getSubsection().getSection().getId();
			model.addAttribute("subsections", subsectionRepo.findAll1(sectionId)); // id, name, section(id) and by section
		}
			
		return "priceList/works/editPriceWork";
	}
	
	@PostMapping("/price-list/works/{city}/edit-price-work/{id}")
	public String postEditPriceWork(@PathVariable(value = "city") String cityP, @PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", cityP);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				model.addAttribute("priceWork", priceWorkRepo.findById(id).get());
				model.addAttribute("priceMats", priceMatRepo.findAll5(CityEnum.getEnumByAbbr(cityP).orElse(null)));
		
		String subsectionId = request.getParameter("subsection");
		String city = request.getParameter("city");
		String name = request.getParameter("name").trim();
		BigDecimal price = new BigDecimal( request.getParameter("price") );
		String measurement = request.getParameter("measurement");
		boolean deleted = request.getParameter("deleted") == null ? false : true;
		int orderNum = Integer.valueOf(request.getParameter("orderNum"));
		Long priceMatId = Etc.idFromDatalist(request.getParameter("priceMat"));
		
		PriceMat priceMat = priceMatId == null ? null : priceMatRepo.findById(priceMatId).get();

		
		// checking for the existence of the city
		Optional<CityEnum> оptCityEnum = CityEnum.getEnumByAbbr(city);
		Optional<MeasurementEnum> measurement2 = MeasurementEnum.getEnumByAbbr(measurement);
		
		PriceWork priceWork = priceWorkRepo.findById(id).get();
		
		priceWork.setCity(оptCityEnum.orElse(null));
		priceWork.setSubsection(subsectionRepo.findById(Long.valueOf(subsectionId)).get());
		priceWork.setName(name);
		priceWork.setPrice(price);
		priceWork.setPriceMat(priceMat);
		priceWork.setMeasurement(measurement2.get());
		priceWork.setDeleted(deleted);
		priceWork.setOrderNumByRightWay(orderNum, priceWorkRepo, false);
		
		priceWorkRepo.save(priceWork);
		
		model.addAttribute("info", "Успешно изменён!");
		return "redirect:/price-list/works/" + cityP + "/sections/section/" + priceWork.getSubsection().getSection().getId() + "/subsection/" + priceWork.getSubsection().getId();	
	}
	
	
	
	
	
	
	
	
	
	
	// mats
	
	@GetMapping("/price-list/mats")
	public String getPriceListMats(Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		model.addAttribute("cities", CityEnum.getMap());	
		return "priceList/mats/menuMats";
	}
	
	@GetMapping("/price-list/mats/{city}/sections")
	public String getSectionsInCityMat(@PathVariable(value = "city") String city, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/mats/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		List<Section> sections = new ArrayList<Section>( sectionRepo.findAll1() );
		if (sections.size() > 0) { Collections.sort(sections); }
		model.addAttribute("sections", sections);
		model.addAttribute("allPriceMats", priceMatRepo.findAll6(CityEnum.getEnumByAbbr(city).orElse(null)));
		return "priceList/mats/priceListSections";
	}
	
	@GetMapping("/price-list/mats/{city}/print")
	public String getInCityMatPrint(@PathVariable(value = "city") String city, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/mats/");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		Map<String, List<PriceMat>> mapSectionsAndPriceMats = new HashMap<String, List<PriceMat>>();
		List<PriceMat> priceMats = priceMatRepo.findAll7(CityEnum.getEnumByAbbr(city).orElse(null));
		List<Section> sections = sectionRepo.findAll1();
		if (sections.size() > 0) { Collections.sort(sections); }
		for (Section section : sections) { 
			mapSectionsAndPriceMats.put(section.getName(), priceMats.stream().filter(p -> p.getSection() != null && p.getSection().getId() == section.getId()).toList());
		}
		
		model.addAttribute("mapSectionsAndPriceMats", mapSectionsAndPriceMats);
		return "priceList/mats/print";
	}
	
	@GetMapping("/price-list/mats/{city}/to-edit")
	public String getToEditInCityMat(@PathVariable(value = "city") String city, HttpServletRequest request,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
				String back = request.getParameter("back");
		Long id = Etc.idFromDatalist(request.getParameter("priceMat"));
		return "redirect:" + URI.create("/price-list/mats/" + city + "/edit-price-mat/" + id + "?back="  + back).toString();
	}
	
	@GetMapping("/price-list/mats/{city}/sections/section/{id}")
	public String getSectionPriceMatsInCity(@PathVariable(value = "city") String city, @PathVariable(value = "id") long id, Model model,		
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", "/price-list/mats/" + city + "/sections");
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		
		if (city.equals("common")) {
			model.addAttribute("cityName", "Общий");
			model.addAttribute("cityAbbr", "common");
		} else {
			model.addAttribute("cityName", CityEnum.getEnumByAbbr(city).get().getName());
			model.addAttribute("cityAbbr", CityEnum.getEnumByAbbr(city).get().getAbbr());
		}
		
		List<PriceMat> priceMats = new ArrayList<PriceMat>( priceMatRepo.findAll4(id, CityEnum.getEnumByAbbr(city).orElse(null)) );
		model.addAttribute("sectionId", id);
		model.addAttribute("sectionName", sectionRepo.findById(id).get().getName());
		model.addAttribute("priceMats", priceMats);
		return "priceList/mats/priceListMats";
	}
	
	@GetMapping("/price-list/mats/{city}/new-price-mat")
	public String getNewPriceMat(@PathVariable(value = "city") String city, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", city);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				
		if (request.getParameter("section") != null && !request.getParameter("section").equals("-1")) { // if it's a request for a list of subcategories
			Long sectionId = Long.valueOf( request.getParameter("section") );
			model.addAttribute("sectionId", sectionId);
		}
		return "priceList/mats/newPriceMat";
	}
	
	@PostMapping("/price-list/mats/{city}/new-price-mat")
	public String postNewPriceMat(@PathVariable(value = "city") String cityP, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", cityP);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
		
		String sectionId = request.getParameter("section");
		String city = request.getParameter("city");
		String name = request.getParameter("name").trim();
		BigDecimal price = new BigDecimal( request.getParameter("price") );
		String measurement = request.getParameter("measurement");
		boolean equipment = request.getParameter("equipment") == null ? false : true;

		
		// checking for the existence of the city
		Optional<CityEnum> оptCityEnum = CityEnum.getEnumByAbbr(city);
		Optional<MeasurementEnum> measurement2 = MeasurementEnum.getEnumByAbbr(measurement);
		
		PriceMat priceMat = new PriceMat(оptCityEnum.orElse(null), sectionRepo.findById(Long.valueOf(sectionId)).get(), name, price, measurement2.get(), equipment);
		priceMatRepo.save(priceMat);
		
		model.addAttribute("info", "Успешно добавлен!");
		return "priceList/mats/newPriceMat";	
	}
	
	
	@GetMapping("/price-list/mats/{city}/edit-price-mat/{id}")
	public String getEditPriceMat(@PathVariable(value = "city") String city, @PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", city);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				PriceMat priceMat = priceMatRepo.findById(id).get();
				model.addAttribute("priceMat", priceMat);
			
		if (request.getParameter("section") != null) { // if it's a request for a list of subcategories
			Long sectionId = Long.valueOf( request.getParameter("section") );
			model.addAttribute("editedSection", sectionId != priceMat.getSection().getId() ? true : false); // при выборе другого раздела
			model.addAttribute("sectionId", sectionId);
		} else {
			model.addAttribute("editedSection", false); // при выборе другого раздела
		}
		return "priceList/mats/editPriceMat";
	}
	
	@PostMapping("/price-list/mats/{city}/edit-price-mat/{id}")
	public String postEditPriceMat(@PathVariable(value = "city") String cityP, @PathVariable(value = "id") Long id, HttpServletRequest request, Model model,
			@CookieValue(value = "id", required = false) String cId,
			@CookieValue(value = "password", required = false) String cPassword,
			@CookieValue(value = "secretnum", required = false) String cSecretnum,
			@CookieValue(value = "depart", required = false) String cDepart) {
		// Main prefix
				model.addAttribute("cDepart", DepartEnum.getEnumByAbbr(cDepart));
				model.addAttribute("back", request.getParameter("back"));
				if ( !access.check(new User(cId, cPassword, cSecretnum, cDepart), "snab", "adm2") ) {
					return "error";
				}
		// Prefix
				model.addAttribute("back", request.getParameter("back"));
				model.addAttribute("city", cityP);
				model.addAttribute("cities", CityEnum.getMap());
				model.addAttribute("measurements", MeasurementEnum.getMap());
				model.addAttribute("sections", sectionRepo.findAll());
				model.addAttribute("priceMat", priceMatRepo.findById(id).get());
		
		String sectionId = request.getParameter("section");
		String city = request.getParameter("city");
		String name = request.getParameter("name").trim();
		BigDecimal price = new BigDecimal( request.getParameter("price") );
		String measurement = request.getParameter("measurement");
		boolean deleted = request.getParameter("deleted") == null ? false : true;
		boolean equipment = request.getParameter("equipment") == null ? false : true;
		

		
		// checking for the existence of the city
		Optional<CityEnum> оptCityEnum = CityEnum.getEnumByAbbr(city);
		Optional<MeasurementEnum> measurement2 = MeasurementEnum.getEnumByAbbr(measurement);
		
		PriceMat priceMat = priceMatRepo.findById(id).get();
		
		priceMat.setCity(оptCityEnum.orElse(null));
		priceMat.setSection(sectionRepo.findById(Long.valueOf(sectionId)).get());
		priceMat.setName(name);
		priceMat.setPrice(price);
		priceMat.setMeasurement(measurement2.get());
		priceMat.setDeleted(deleted);
		priceMat.setEquipment(equipment);
		
		priceMatRepo.save(priceMat);
		
		model.addAttribute("info", "Успешно изменён!");
		return "redirect:/price-list/mats/" + cityP + "/sections/section/" + priceMat.getSection().getId();	
	}
	
}
