package e_learning.controllers;

import e_learning.entity.ContactInfo;
import e_learning.services.ServiceImpl.ContactInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact-info")
@AllArgsConstructor
public class ContactInfoController {
    private ContactInfoService contactInfoService;

    @GetMapping
    public ResponseEntity<ContactInfo> getContactInfo() {
        return ResponseEntity.ok(contactInfoService.getContactInfo());
    }

    @PutMapping
    public ResponseEntity<ContactInfo> updateContactInfo(@RequestBody ContactInfo contactInfo) {
        return ResponseEntity.ok(contactInfoService.updateContactInfo(contactInfo));
    }
}
