package mini.project.service;

import lombok.Data;
import mini.project.dto.Tax;
import mini.project.dto.TaxDriver;
import mini.spring.ioc.annotation.Autowired;
import mini.spring.ioc.annotation.Component;

@Data
@Component
public class TaxDriverService {
    @Autowired
    private TaxDriver taxDriver;
    @Autowired
    private Tax tax;
}
