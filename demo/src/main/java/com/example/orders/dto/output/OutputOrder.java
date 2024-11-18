package com.example.orders.dto.output;

import com.example.orders.models.OrderLine;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@TypeAlias("Order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("orders")
public class OutputOrder {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    private Long userId;
    private List<OrderLine> orderLines;
    private Integer totalItems = 0;
    private Double total = 0.0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }
}
