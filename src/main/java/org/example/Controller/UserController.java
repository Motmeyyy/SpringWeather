package org.example.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.Model.User;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user) {

        if (userService.findByUsername(user.getUsername()) != null) {
            return "Пользователь с таким именем уже существует!";
        }

        userService.saveUser(user);

        return "redirect:/api/users/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; //
    }

    @PostMapping("/login")
    public String loginUser(User loginUser, HttpSession session) {
        User user = userService.findByUsername(loginUser.getUsername());

        // Проверяем наличие пользователя
        if (user == null) {
            return "login_failed";
        }

        // Проверяем соответствие пароля
        if (user.getPassword().equals(loginUser.getPassword())) {
            //Устанавливаем атрибут сессии - юзернейм пользователя
            session.setAttribute("username", user);

            return "redirect:/weather";
        } else {
            return "login_failed";
        }
    }
}
