package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.springrapid.acldemo.Illness;
import com.github.vincemann.springrapid.acldemo.PetType;
import com.github.vincemann.springrapid.acldemo.Specialty;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.acldemo.vet.Vet;
import com.github.vincemann.springrapid.acldemo.visit.Visit;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Component
public class TestData {
    public static final String CONTACT_INFORMATION_SUFFIX = "@guerilla-mail.com";

    public static final String ADMIN = "admin";
    public static final String ADMIN_PASSWORD = "AdminPassword123!";
    public static final String ADMIN_CONTACT_INFORMATION = "admin@example.com";

    public static final String VET_MAX = "Max";
    public static final String VET_MAX_PASSWORD = "MaxPassword123?";
    public static final String VET_MAX_EMAIL = VET_MAX +CONTACT_INFORMATION_SUFFIX;

    public static final String VET_POLDI = "Poldi";
    public static final String VET_POLDI_PASSWORD = "PoldiPassword123?";
    public static final String VET_POLDI_EMAIL = VET_POLDI+CONTACT_INFORMATION_SUFFIX;

    public static final String VET_DICAPRIO = "Dicaprio";
    public static final String VET_DICAPRIO_PASSWORD = "DicaprioPassword123?";
    public static final String VET_DICAPRIO_EMAIL = VET_DICAPRIO+CONTACT_INFORMATION_SUFFIX;

    public static final String OWNER_MEIER = "Meier";
    public static final String OWNER_MEIER_PASSWORD = "MeierPassword123?";
    public static final String OWNER_MEIER_EMAIL = OWNER_MEIER+CONTACT_INFORMATION_SUFFIX;

    public static final String OWNER_KAHN = "Kahn";
    public static final String OWNER_KAHN_PASSWORD = "KahnPassword123?";
    public static final String OWNER_KAHN_EMAIL = OWNER_KAHN+CONTACT_INFORMATION_SUFFIX;

    public static final String MUSCLE = "Muscle";
    public static final String DENTISM = "Dentism";
    public static final String GASTRO = "Gastro";
    public static final String HEART = "Heart";



    public static final String BELLO = "Bello";
    public static final String BELLA = "Bella";
    public static final String KITTY = "Kitty";

    public static final String GASTRITIS = "Gastritis";
    public static final String TEETH_PAIN = "teeth pain";
    public static final String WEAK_HEART = "wek heart";

    Vet vetMax;
    Vet vetPoldi;
    Vet vetDiCaprio;

    Specialty dentism;
    Specialty gastro;
    Specialty heart;
    Specialty muscle;

    Owner meier;
    Owner kahn;

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

    public PetType getDogPetType(){
        return new PetType("Dog");
    }

    public PetType getCatPetType(){
        return new PetType("Cat");
    }


    public void initTestData(){

        gastritis = Illness.builder()
                .name(GASTRITIS)
                .build();

        teethPain = Illness.builder()
                .name(TEETH_PAIN)
                .build();

        weakHeart = Illness.builder()
                .name(WEAK_HEART)
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
                .lastName(OWNER_MEIER)
                .contactInformation(OWNER_MEIER_EMAIL)
                .password(OWNER_MEIER_PASSWORD)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("0123456789")
                .build();

        kahn = Owner.builder()
                .firstName("Olli")
                .lastName(OWNER_KAHN)
                .contactInformation(OWNER_KAHN_EMAIL)
                .password(OWNER_KAHN_PASSWORD)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("1234567890")
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
                .contactInformation(VET_MAX_EMAIL)
                .password(VET_MAX_PASSWORD)
                .build();

        vetDiCaprio = Vet.builder()
                .firstName("michael")
                .lastName(VET_DICAPRIO)
                .contactInformation(VET_DICAPRIO_EMAIL)
                .password(VET_DICAPRIO_PASSWORD)
                .build();

        vetPoldi = Vet.builder()
                .firstName("Olli")
                .lastName(VET_POLDI)
                .contactInformation(VET_POLDI_EMAIL)
                .password(VET_POLDI_PASSWORD)
                .build();

        checkHeartVisit = Visit.builder()
                .date(LocalDate.now())
                .reason("heart problems")
                .build();

        checkTeethVisit = Visit.builder()
                .date(LocalDate.now())
                .reason("teeth hurt")
                .build();
    }
}
