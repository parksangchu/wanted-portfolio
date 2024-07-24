package com.wanted.portfolio.member.model;

import com.wanted.portfolio.global.model.BaseModel;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseModel {

    private String email;

    private String phoneNumber;

    private String name;

    private String password;
}
