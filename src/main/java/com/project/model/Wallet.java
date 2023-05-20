package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double balance;

    @JsonIgnore
    @OneToOne(mappedBy = "wallet") // Maps the relationship to the wallet field in User entity
    private User user;

    public void topUp(Double balance) {
        if (balance > 0) {
            this.balance += balance;
        }
    }

}
