package com.springapp.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/rc")
public class RecipeController {
    private Logger log = Logger.getLogger("RecipeController");
    @Autowired
    private RecipeService recipeService;

    @RequestMapping("/getAllRecipes")
    @ResponseBody
    public List<String> getAllRecipes(@RequestParam String category, @RequestParam Boolean isAdmin) {
		return recipeService.getAllRecipes(category, isAdmin);
	}

    @RequestMapping("/getCategories")
    @ResponseBody
    public List<String> getCategories() {
		return recipeService.getCategories();
	}

    @RequestMapping("/findRecipe")
    @ResponseBody
    public List<RecipeDetails> findRecipe(@RequestParam Boolean isadmin, @RequestParam Map<String, String> config) {
		return recipeService.findRecipe(isadmin, config);
	}

    @RequestMapping("/getRecipeNames")
    @ResponseBody
    public List<String> getRecipeNamesStartingWith(@RequestParam String recipe) {
		return recipeService.getRecipeNamesStartingWith(recipe);
	}

    @RequestMapping("/getRecipeNamesForCategory")
    @ResponseBody
    public List<String> getRecipeNamesForCategory(@RequestParam String category) {
		return recipeService.getRecipeNamesForCategory(category);
	}

    public String checkSession() {
        log.info("Starting checkSession");
        String user = null;
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        HttpSession session = request.getSession();
        if (!session.isNew()) {
            user = (String) session.getAttribute("user");
            log.info("Session exists for user " + user);
        }
        log.info("Finished findRecipe");
        return user;
    }

    public void logout() {
        log.info("Starting logout");
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        HttpSession session = request.getSession();
        session.invalidate();
        log.info("Finished logout");
    }

    public String login(String user, String password) {
        boolean valid = recipeService.login(user, password);
        String message = null;
        if ( valid ) {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = sra.getRequest();
            request.getSession().setAttribute("user", user);
            message = request.getSession().getId();
            log.info(user + "logging in");
        }
        else {
            message = "Wrong Username or Password.";
            log.info(user + "Bad username/password");
        }
        return message;

    }

}