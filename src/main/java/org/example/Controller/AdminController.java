package org.example.Controller;


import org.example.Model.User;
import org.example.Model.Weather;
import org.example.Service.UserService;
import org.example.Service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/moderation")
    public String moderationPage(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "moderation";
    }

    @GetMapping("/userHistory/{username}")
    public String userHistory(@PathVariable(name = "username") String username, Model model) {
        List<Weather> userWeatherHistory = weatherService.getWeatherHistoryByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("userWeatherHistory", userWeatherHistory);
        return "userHistory";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/moderation";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute User updatedUser) {
        userService.updateUser(updatedUser);
        return "redirect:/admin/moderation";
    }
}
