package pharmacie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

@Service
public class ApprovisionnementService {

    private final MedicamentRepository medicamentRepository;
    private final JavaMailSender emailSender;

    public ApprovisionnementService(MedicamentRepository medicamentRepository, JavaMailSender emailSender) {
        this.medicamentRepository = medicamentRepository;
        this.emailSender = emailSender;
    }

    @Transactional(readOnly = true)
    public void traiterReapprovisionnement() {
        // 1. Identifier les médicaments à réapprovisionner
        List<Medicament> medicamentsACommander = medicamentRepository.findAll().stream()
                .filter(m -> m.getUnitesEnStock() < m.getNiveauDeReappro())
                .collect(Collectors.toList());

        if (medicamentsACommander.isEmpty()) {
            return;
        }

        // Structure : Fournisseur -> (Categorie -> Liste de Médicaments)
        Map<Fournisseur, Map<Categorie, List<Medicament>>> mapFournisseurMedicaments = new HashMap<>();

        for (Medicament med : medicamentsACommander) {
            Categorie categorie = med.getCategorie();
            // On récupère les fournisseurs pour cette catégorie
            List<Fournisseur> fournisseurs = categorie.getFournisseurs();
            if (fournisseurs != null) {
                for (Fournisseur fournisseur : fournisseurs) {
                    mapFournisseurMedicaments
                            .computeIfAbsent(fournisseur, k -> new HashMap<>())
                            .computeIfAbsent(categorie, k -> new ArrayList<>())
                            .add(med);
                }
            }
        }

        // 3. Envoyer les emails
        for (Map.Entry<Fournisseur, Map<Categorie, List<Medicament>>> entry : mapFournisseurMedicaments.entrySet()) {
            Fournisseur fournisseur = entry.getKey();
            Map<Categorie, List<Medicament>> medicamentsParCategorie = entry.getValue();
            envoyerEmailApprovisionnement(fournisseur, medicamentsParCategorie);
        }
    }

    private void envoyerEmailApprovisionnement(Fournisseur fournisseur, Map<Categorie, List<Medicament>> medicamentsParCategorie) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(fournisseur.getEmail());
        message.setSubject("Demande de devis pour réapprovisionnement");

        StringBuilder text = new StringBuilder();
        text.append("Bonjour ").append(fournisseur.getNom()).append(",\n\n");
        text.append("Veuillez nous fournir un devis pour les médicaments suivants :\n\n");

        for (Map.Entry<Categorie, List<Medicament>> catEntry : medicamentsParCategorie.entrySet()) {
            text.append("=== Catégorie : ").append(catEntry.getKey().getLibelle()).append(" ===\n");
            for (Medicament med : catEntry.getValue()) {
                text.append("- ").append(med.getNom())
                        .append(" (Réf: ").append(med.getReference()).append(")")
                        .append(" : Quantité en stock = ").append(med.getUnitesEnStock())
                        .append(", Niveau réappro = ").append(med.getNiveauDeReappro())
                        .append("\n");
            }
            text.append("\n");
        }

        text.append("Cordialement,\nLa Pharmacie");

        message.setText(text.toString());
        emailSender.send(message);
    }
}
