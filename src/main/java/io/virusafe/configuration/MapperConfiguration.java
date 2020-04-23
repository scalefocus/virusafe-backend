package io.virusafe.configuration;

import io.virusafe.mapper.LocationGpsMapper;
import io.virusafe.mapper.LocationProximityMapper;
import io.virusafe.mapper.PersonalInformationMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    /**
     * Provide a LocationGpsMapper bean, created from the MapStruct Mappers factory.
     *
     * @return the LocationGpsMapper bean
     */
    @Bean
    public LocationGpsMapper locationGpsMapper() {
        return Mappers.getMapper(LocationGpsMapper.class);
    }

    /**
     * Provide a PersonalInformationMapper bean, created from the MapStruct Mappers factory.
     *
     * @return the PersonalInformationMapper bean
     */
    @Bean
    public PersonalInformationMapper personalInformationMapper() {
        return Mappers.getMapper(PersonalInformationMapper.class);
    }

    /**
     * Provide a LocationProximityMapper bean, created from the MapStruct Mappers factory.
     *
     * @return the LocationProximityMapper bean
     */
    @Bean
    public LocationProximityMapper locationProximityMapper() {
        return Mappers.getMapper(LocationProximityMapper.class);
    }
}
