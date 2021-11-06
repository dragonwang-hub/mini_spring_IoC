package mini.project.dto;

import lombok.Data;
import mini.spring.ioc.annotation.Component;
import mini.spring.ioc.annotation.Value;

@Data
@Component("PersonalDriver")
public class TaxDriver {
    @Value("9537")
    private Integer id;
    @Value("Joey")
    private String name;
//    @Autowired
    private Tax electricTax;
}
