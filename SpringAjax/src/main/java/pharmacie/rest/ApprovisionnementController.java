package pharmacie.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;

@RestController
@RequestMapping("/api/approvisionnement")
public class ApprovisionnementController {

    private final ApprovisionnementService approvisionnementService;

    public ApprovisionnementController(ApprovisionnementService approvisionnementService) {
        this.approvisionnementService = approvisionnementService;
    }

    @PostMapping("/lancer")
    public ResponseEntity<List<String>> lancerApprovisionnement() {
        return ResponseEntity.ok(approvisionnementService.traiterReapprovisionnement());
    }
}
