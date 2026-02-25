package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Fournisseur;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "fournisseurs", path = "fournisseurs")
public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {
}
