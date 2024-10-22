package e_learning.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("responsableFormation")
public class ResponsableFormation extends UserApp {
    @ManyToOne
    private Entreprise entreprise;


}
