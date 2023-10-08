package io.madipalli.prabhu.SpringWithReact.dal.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Entity(name = "positions")
@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Document("positions")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String positionId;
    private Instant creationTime;
    private Instant updateTime;
    private String name;
}
