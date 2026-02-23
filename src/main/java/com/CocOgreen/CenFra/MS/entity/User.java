package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name ="users")
@Entity
@Table(name ="user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer userId;

    @Column
    private String userName;

    @Column
    private String password;

    @Column
    private String fullName;

    @Column
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<ExportNote> exportNotes;
    @Column
    private Boolean isActive;

}
