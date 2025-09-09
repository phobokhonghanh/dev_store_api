package dev.dev_store_api.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "third_party")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String link;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

    public ThirdParty(Long id) {
        this.id = id;
    }
}
