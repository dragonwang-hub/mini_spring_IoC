package mini.project.dto;

import lombok.Data;
import mini.spring.ioc.annotation.Component;

@Data
@Component
public class Tax {
    private String licensePlate;
    private String carModal;
}
