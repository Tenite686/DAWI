package pe.edu.cibertec.alquilape.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;


public class MapperUtils {

    public static <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

//    public static void main(String[] args) {
//        String admin="pass123";
//        String supervisor="sup123";
//        String asistente="123";
//
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println("Admin = " + encoder.encode(admin));
//        System.out.println("Supervisor = " + encoder.encode(supervisor));
//        System.out.println("Asistente = " + encoder.encode(asistente));
//    }
}
