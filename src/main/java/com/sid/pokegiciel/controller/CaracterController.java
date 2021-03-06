package com.sid.pokegiciel.controller;

import com.sid.pokegiciel.model.Caracter;
import com.sid.pokegiciel.model.User;
import com.sid.pokegiciel.repository.CaracterRepository;
import com.sid.pokegiciel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.sid.pokegiciel.controller.AuthenticationController.getCurrentUsername;

@Controller
public class CaracterController {


    @Autowired
    private CaracterRepository caracterRepository;

    @Autowired
    private UserService userService;


    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("caracters", caracterRepository.findAllByUser_Username(getCurrentUsername()));
        model.addAttribute("points", userService.findByUsername(getCurrentUsername()).getPoints());

        return "home";
    }


    @RequestMapping(value = "/caracters/post", method = RequestMethod.POST)
    public String addCaracter(@RequestParam("name") String name, @RequestParam("points") int points) {
        User currentUser = userService.findByUsername(getCurrentUsername());
        int currentUserPoints = currentUser.getPoints();
        if (currentUserPoints > points) {
            currentUser.setPoints(currentUserPoints - points);
            userService.save(currentUser);
            Caracter caracter = new Caracter();
            caracter.setName(name);
            caracter.setPoints(points);
            caracter.setUser(userService.findByUsername(getCurrentUsername()));
            caracterRepository.save(caracter);
        }
        return "redirect:/home";
    }

    @RequestMapping(value = "/caracters/edit", method = RequestMethod.GET)
    public String editCaracter(Model model, @RequestParam("caracterId") Long id) {
        Caracter caracter = caracterRepository.findById(id);
        model.addAttribute("caracter", caracter);
        return "caracters-edit";
    }

    @RequestMapping(value = "/caracter/put", method = RequestMethod.POST)
    public String putCaracter(@ModelAttribute("caracterForm") Caracter editedCaracter) {
        User currentUser = userService.findByUsername(getCurrentUsername());
        int currentUserPoints = currentUser.getPoints();
        int caracterPoints = editedCaracter.getPoints();
        if (currentUserPoints > caracterPoints) {
            int oldCaracterPoints = caracterRepository.findById(editedCaracter.getId()).getPoints();
            if (oldCaracterPoints > caracterPoints) {
                currentUser.setPoints(currentUserPoints + (oldCaracterPoints - caracterPoints));
            } else {
                currentUser.setPoints(currentUserPoints - (caracterPoints - oldCaracterPoints));
            }
            userService.save(currentUser);
            Caracter caracterToEdit = caracterRepository.findById(editedCaracter.getId());
            caracterToEdit.setName(editedCaracter.getName());
            caracterToEdit.setPoints(editedCaracter.getPoints());
            caracterRepository.save(caracterToEdit);
            return "redirect:/home";
        }
        return "redirect:/caracters-edit";
    }

    @RequestMapping(value = "/caracter/delete", method = RequestMethod.POST)
    public String deleteCaracter(@ModelAttribute("caracterForm") Caracter caracter) {
        User currentUser = userService.findByUsername(getCurrentUsername());
        int currentUserPoints = currentUser.getPoints();
        int caracterPoints = caracterRepository.findById(caracter.getId()).getPoints();
        currentUser.setPoints(currentUserPoints + caracterPoints);
        userService.save(currentUser);
        caracterRepository.delete(caracter.getId());
        return "redirect:/home";
    }
}
