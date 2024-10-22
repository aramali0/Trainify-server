package e_learning.controllers;

import e_learning.services.ServiceImpl.ExcelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class ExcelController {

    private final ExcelService excelService;


    @GetMapping("/generate-template")
    public ResponseEntity<byte[]> generateTemplate() throws IOException {
        return excelService.generateExcelTemplate();
    }


    @GetMapping("/template")
    public ResponseEntity<byte[]> generateResponsableTemplate() throws IOException {
        return excelService.generateExcelTemplate();
    }
}
