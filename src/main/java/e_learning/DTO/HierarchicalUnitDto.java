package e_learning.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalUnitDto {
    private String id;
    private String name; // e.g., "Filiale", "Pole", etc.
    private String type; // "Filiale", "Pole", "Direction", etc.
    private String parentId; // For parent reference
    private List<String> childrenIds; // Recursive for child units
    private Long entrepriseId; // Reference to the associated entreprise
}
