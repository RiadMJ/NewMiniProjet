package pharmacie.rest;

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
    public ResponseEntity<String> lancerApprovisionnement() {
        approvisionnementService.traiterReapprovisionnement();
        return ResponseEntity.ok("Processus de réapprovisionnement lancé. Les emails ont été envoyés aux fournisseurs.");
    }
}
