package mini.project.dto;

import lombok.Data;
import mini.spring.ioc.annotation.Component;

@Data
@Component
public class TaxDriver {
    private Integer id;
    private String name;
}
