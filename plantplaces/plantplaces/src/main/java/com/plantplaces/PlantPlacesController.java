package com.plantplaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.plantplaces.dto.PlantDTO;
import com.plantplaces.dto.SpecimenDTO;
import com.plantplaces.service.ISpecimenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Controller
public class PlantPlacesController {
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ISpecimenService specimenService;

	private List<PlantDTO> allPlants;

	private String firstThreeCharacters;

	@RequestMapping(value="/savespecimen")
	public String saveSpecimen(SpecimenDTO specimenDTO) {
		try {
			specimenService.save(specimenDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("unable to save specimen", e);
			e.printStackTrace();
			return "error";
		}
		return "start";
	}
	
	/**
	 * Handle the /start endpoint.
	 * @return
	 */
	@RequestMapping(value="/start", method=RequestMethod.GET, headers={"content-type=text/json"})
	@ResponseBody
	public SpecimenDTO readJSON(Model model) {
		SpecimenDTO specimenDTO = specimenService.fetchById(43);
		model.addAttribute("specimenDTO", specimenDTO);
		return specimenDTO;
		
	}

	
	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String read(Model model) {
		log.info("User has entered the /start endpoint");
		model.addAttribute("specimenDTO", new SpecimenDTO());
		return "start";
	}
 
	@RequestMapping(value="/addspecimen", method=RequestMethod.GET)
	public String addSpecimen(Model model, @RequestParam(value="latitude", required=false, defaultValue="0.0") String latitude) {
		SpecimenDTO specimenDTO = specimenService.fetchById(43);
		specimenDTO.setLatitude(latitude); 
		model.addAttribute("specimenDTO", specimenDTO);
		return "start";
	}
	
	/**
	 * Handle the /start endpoint.
	 * @return
	 */
	@RequestMapping(value="/start", method=RequestMethod.GET, params= {"loyalty=blue"})
	public String readBlue() {
		return "start";
	}
	
	/**
	 * Handle the /start endpoint.
	 * @return
	 */
	@RequestMapping(value="/start", method=RequestMethod.GET, params= {"loyalty=silver"})
	public ModelAndView readSilver() {
		SpecimenDTO specimenDTO = specimenService.fetchById(43);
		specimenDTO.setSpecimenId(83);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("start");
		modelAndView.addObject("specimenDTO", specimenDTO);
		return modelAndView;
	}
	
	@PostMapping("/start")
	public String create() {
		return "start";
	}
	
	/**
	 * Handle the / endpoint
	 * @return
	 */
	@RequestMapping("/")
	public String index() {
		return "start";
	}
	
	@RequestMapping("/searchPlants")
	public ModelAndView searchPlants(@RequestParam(value="searchTerm", required=false, defaultValue="") String searchTerm) {
		log.debug("entering search plants");
		ModelAndView modelAndView = new ModelAndView();
		List<PlantDTO> plants = new ArrayList<PlantDTO>(); 
		try {
			plants = specimenService.fetchPlants(searchTerm);
			modelAndView.setViewName("plantResults");
			if (plants.size() == 0 ) {
				log.warn("Received 0 results for search string: " + searchTerm);
			}
		} catch (Exception e) {
			log.error("Error happened in searchPlants endpoint", e);
			e.printStackTrace();
			modelAndView.setViewName("error");
		}
		modelAndView.addObject("plants", plants);
		log.debug("exiting search Plants");
		return modelAndView;
	}
	
	
	@RequestMapping("/searchPlantsAll")
	public String searchPlantsAll(@RequestParam Map<String,String> requestParams) {
		int params = requestParams.size();
		requestParams.get("searchTerm");
		return "start";
	}
	
	@RequestMapping("/sustainability")
	public String sustainability () {
		return "sustainability";
	}
	
	@RequestMapping("/showSpecimens")
	public ModelAndView showSpecimens() {
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			Iterable<SpecimenDTO> allSpecimens = specimenService.fetchAllSpecimens();
			modelAndView.setViewName("showSpecimens");
			modelAndView.addObject("allSpecimens", allSpecimens);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Unable to retrieve specimens", e);
			modelAndView.setViewName("error");
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/plantNamesAutocomplete")
	@ResponseBody
	public List<String> plantNamesAutocomplete(@RequestParam(value="term", required = false, defaultValue="") String term)  {
		List<String> suggestions = new ArrayList<String>();
		try {
			// only update when term is three characters.
			if (term.length() == 3) {
				firstThreeCharacters = term;
				allPlants = specimenService.fetchPlants(term);
			}
			
			for (PlantDTO plantDTO : allPlants) {
				if (plantDTO.toString().contains(term)) {
					suggestions.add(plantDTO.toString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Exception in autocomplete", e);
		}
		
		return suggestions;
		
	}
	
	

	
	
}
