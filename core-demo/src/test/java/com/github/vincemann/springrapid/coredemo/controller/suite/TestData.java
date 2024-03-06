package com.github.vincemann.springrapid.coredemo.controller.suite;

import com.github.vincemann.springrapid.coredemo.model.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;

@Getter
@Component
/**
 * {@code savedDogPetType} and {@code savedCatPetType} need to be persisted and saved before calling
 * {@code initTestData}.
 */
public class TestData {
    public static final String CONTACT_INFORMATION_SUFFIX = "@guerilla-mail.com";

    public static final String VET_MAX = "Max";
    public static final String VET_POLDI = "Poldi";
    public static final String VET_DICAPRIO = "Dicaprio";

    public static final String MUSCLE = "Muscle";
    public static final String DENTISM = "Dentism";
    public static final String GASTRO = "Gastro";
    public static final String HEART = "Heart";

    public static final String MEIER = "Meier";
    public static final String KAHN = "Kahn";
    public static final String GIL = "Gil";

    public static final String BELLO = "Bello";
    public static final String BELLA = "Bella";
    public static final String KITTY = "Kitty";

    public static final String BALL = "ball";
    public static final String BONE = "bone";
    public static final String RUBBER_DUCK = "rubberDuck";

    Vet vetMax;
    Vet vetPoldi;
    Vet vetDiCaprio;

    Specialty dentism;
    Specialty gastro;
    Specialty heart;
    Specialty muscle;

    Owner meier;
    Owner kahn;
    Owner gil;

    Pet bello;
    Pet kitty;
    Pet bella;

    Illness gastritis;
    Illness teethPain;
    Illness weakHeart;

    PetType savedDogPetType;
    PetType savedCatPetType;


    Visit checkTeethVisit;
    Visit checkHeartVisit;

    ClinicCard clinicCard;
    ClinicCard secondClinicCard;
    Toy rubberDuck;
    Toy ball;
    Toy bone;

    public PetType getDogPetType(){
        return new PetType("Dog");
    }

    public PetType getCatPetType(){
        return new PetType("Cat");
    }
    public void initTestData(){

        rubberDuck = Toy.builder()
                .name(RUBBER_DUCK)
                .build();

        ball = Toy.builder()
                .name(BALL)
                .build();

        bone = Toy.builder()
                .name(BONE)
                .build();

        bello = Pet.builder()
                .petType(savedDogPetType)
                .name(BELLO)
                .birthDate(LocalDate.now())
                .build();

        bella = Pet.builder()
                .petType(savedDogPetType)
                .name(BELLA)
                .birthDate(LocalDate.now())
                .build();

        kitty = Pet.builder()
                .petType(savedCatPetType)
                .name(KITTY)
                .birthDate(LocalDate.now())
                .build();

        meier = Owner.builder()
                .firstName("Max")
                .lastName(MEIER)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("0123456789")
                .build();

        kahn = Owner.builder()
                .firstName("Olli")
                .lastName(KAHN)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("0176456789")
                .build();

        gil = Owner.builder()
                .firstName("dessen")
                .lastName(GIL)
                .address("dessen address")
                .city("n2 city")
                .telephone("0176567110")
                .build();

        dentism = Specialty.builder()
                .description(DENTISM)
                .build();

        heart = Specialty.builder()
                .description(HEART)
                .build();

        muscle = Specialty.builder()
                .description(MUSCLE)
                .build();

        gastro = Specialty.builder()
                .description(GASTRO)
                .build();

        vetMax = Vet.builder()
                .firstName("Max")
                .lastName(VET_MAX)
                .specialtys(new HashSet<>())
                .build();

        vetDiCaprio = Vet.builder()
                .firstName("michael")
                .lastName(VET_DICAPRIO)
                .specialtys(new HashSet<>())
                .build();

        vetPoldi = Vet.builder()
                .firstName("Olli")
                .lastName(VET_POLDI)
                .specialtys(new HashSet<>())
                .build();

        checkHeartVisit = Visit.builder()
                .date(LocalDate.now())
                .pets(new HashSet<>())
                .reason("heart problems")
                .build();

        checkTeethVisit = Visit.builder()
                .date(LocalDate.now())
                .pets(new HashSet<>())
                .reason("teeth hurt")
                .build();

        clinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("stationary pet treatment")
                .build();

        secondClinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("ambulant pet treatment")
                .build();
    }
}
