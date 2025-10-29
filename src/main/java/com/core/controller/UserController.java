package com.core.controller;

import com.core.model.User;
import com.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 컨트롤러
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자 목록 페이지
     */
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        int userCount = userService.getUserCount();

        model.addAttribute("users", users);
        model.addAttribute("userCount", userCount);

        return "users/list";
    }

    /**
     * 사용자 상세 페이지
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users?error=notfound";
        }

        model.addAttribute("user", user);
        return "users/view";
    }

    /**
     * 사용자 등록 폼
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    /**
     * 사용자 등록 처리
     */
    @PostMapping
    public String createUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user);
            return "redirect:/users?success=created";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "users/form";
        }
    }

    /**
     * 사용자 수정 폼
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users?error=notfound";
        }

        model.addAttribute("user", user);
        return "users/form";
    }

    /**
     * 사용자 수정 처리
     */
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, Model model) {
        try {
            user.setId(id);
            userService.updateUser(user);
            return "redirect:/users/" + id + "?success=updated";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "users/form";
        }
    }

    /**
     * 사용자 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/users?success=deleted";
        } catch (IllegalArgumentException e) {
            return "redirect:/users?error=notfound";
        }
    }
}
