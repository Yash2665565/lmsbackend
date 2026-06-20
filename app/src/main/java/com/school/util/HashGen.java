package com.school.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashGen {
    public static void main(String[] args) {
        String pwd = args.length > 0 ? args[0] : "admin123";
        System.out.println(new BCryptPasswordEncoder().encode(pwd));
    }
}
