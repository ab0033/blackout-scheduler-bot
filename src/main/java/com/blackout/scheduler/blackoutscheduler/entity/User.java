package com.blackout.scheduler.blackoutscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    private Long id;
    @Column
    private String streetName;
    @Column
    private String houseNumber;
    @Column
    private String status;
}
