package mini.project.dto;

import lombok.Data;
import mini.spring.ioc.annotation.Autowired;
import mini.spring.ioc.annotation.Component;
import mini.spring.ioc.annotation.Qualifier;
import mini.spring.ioc.annotation.Value;

@Data
@Component
public class Tax {
    @Value("SXAF0001")
    private String licensePlate;
    @Value("BYD")
    private String carModal;
    @Autowired
    @Qualifier("PersonalDriver")
    private TaxDriver taxDriver;
}
