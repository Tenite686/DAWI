package pe.edu.cibertec.alquilape.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper mapper(){
        ModelMapper modelMapper = new ModelMapper();
        // Cambiamos la estrategia a STRICT para evitar ambigüedades con los IDs
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}
