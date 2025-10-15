//package com.example.authsystem.security;
//
//import lombok.experimental.FieldDefaults;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@Service
//@FieldDefaults(level = PRIVATE, makeFinal = true)
//public class JwtBlacklistService {
//    Map<String, Long> blacklist = new ConcurrentHashMap<>();
//
//    public void blacklist(String token, long expiryTimestamp) {
//        blacklist.put(token, expiryTimestamp);
//    }
//
//    public boolean isBlacklisted(String token) {
//        Long exp = blacklist.get(token);
//        if (exp == null) return false;
//
//        if (System.currentTimeMillis() > exp) {
//            blacklist.remove(token);
//            return false;
//        }
//        return true;
//    }
//}