package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic(optional = false)
    @NonNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String nom;

    @Basic(optional = false)
    @NonNull
    @NotBlank
    @Email(message = "L'adresse email doit être valide")
    @Size(max = 255)
    @Column(nullable = false)
    private String email;

    @ManyToMany
    @JoinTable(
            name = "FOURNISSEUR_CATEGORIE",
            joinColumns = @JoinColumn(name = "FOURNISSEUR_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORIE_CODE")
    )
    @ToString.Exclude
    private Set<Categorie> categories = new HashSet<>();
}
