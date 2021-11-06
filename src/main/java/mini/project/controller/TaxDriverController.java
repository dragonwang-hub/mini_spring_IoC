package mini.project.controller;

import lombok.Data;
import mini.project.service.TaxDriverService;
import mini.spring.ioc.annotation.Autowired;
import mini.spring.ioc.annotation.Component;

@Data
@Component
public class TaxDriverController {
    @Autowired
    private TaxDriverService taxDriverService;
}
