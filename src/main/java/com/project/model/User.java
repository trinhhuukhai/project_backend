package com.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String username;
    private String password;

    private String image;

    private String address;

    private String phone;

    private String email;

    @Column(name = "create_date")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "updated")
    @UpdateTimestamp
    private Date updateDate;

    @OneToOne(cascade = CascadeType.ALL) // Defines a one-to-one relationship with Wallet entity
    @JoinColumn(name = "wallet_id") // Specifies the foreign key column
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL) // Defines a one-to-one relationship with Wallet entity
    @JoinColumn(name = "shop_id") // Specifies the foreign key column
    private Shop shop;

    @Column(name = "reset_token")
    private String resetToken;

    @Enumerated(EnumType.STRING)
    private Role role;

//    public void createWallet() {
//        this.wallet = new Wallet();
//        this.wallet.setBalance(0.0);
//    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
