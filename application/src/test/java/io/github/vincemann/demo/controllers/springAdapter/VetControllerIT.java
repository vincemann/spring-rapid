package io.github.vincemann.demo.controllers.springAdapter;

/*
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class VetControllerIT extends EntityInitializer_ControllerIT<Vet, VetDto, VetService, VetController> {

    private VetDto vetDtoWithoutSpecialty;
    private Vet vetWithoutSpecialty;

    private VetDto vetDtoWithSpecialty;
    private Vet vetWithSpecialty;

    VetControllerIT(@Autowired VetController crudController,
                    @Autowired TestRequestEntity_Factory testRequestEntityFactory,
                    @Autowired PlatformTransactionManager platformTransactionManager,
                    @Autowired CheckIfDbDeletedPlugin checkIfDbDeletedPlugin,
                    @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                testRequestEntityFactory,
                platformTransactionManager,
                checkIfDbDeletedPlugin,
                serviceDeepEqualPlugin
        );
    }

    @Override
    protected void onBeforeProvideEntityBundles() throws Exception {
        super.onBeforeProvideEntityBundles();

        vetDtoWithoutSpecialty = VetDto.builder()
                .firstName("master")
                .lastName("Yoda")
                .build();

        vetWithoutSpecialty= Vet.builder()
                .firstName("master")
                .lastName("Yoda")
                .build();

        vetDtoWithSpecialty= VetDto.builder()
                .firstName("master")
                .lastName("Yoda")
                .specialtyIds(Collections.singleton(getTestSpecialty().getId()))
                .build();
        vetWithSpecialty= Vet.builder()
                .firstName("master")
                .lastName("Yoda")
                .specialties(Collections.singleton(getTestSpecialty()))
                .build();
    }

    @Override
    protected List<SuccessfulCreateIntegrationTestBundle<VetDto>> provideSuccessfulCreateTestEntityBundles() {
        return Arrays.asList(
                new SuccessfulCreateIntegrationTestBundle<>(vetDtoWithoutSpecialty),
                new SuccessfulCreateIntegrationTestBundle<>(vetDtoWithSpecialty)
        );
    }

    @Override
    protected List<SuccessfulUpdateIntegrationTestBundle<Vet, VetDto>> provideSuccessfulUpdateTestEntityBundles() {
        VetDto diffVetsNameUpdate = VetDto.builder()
                .firstName("UPDATED NAME")
                .lastName("Yoda")
                .specialtyIds(Collections.singleton(getTestSpecialty().getId()))
                .build();
        return Arrays.asList(
            new SuccessfulUpdateIntegrationTestBundle<>(vetWithSpecialty,diffVetsNameUpdate)
        );
    }


    @Override
    protected List<FailedCreateIntegrationTestBundle<VetDto,Long>> provideFailingCreateTestBundles() {
        return Arrays.asList(
                new FailedCreateIntegrationTestBundle<>(VetDto.builder()
                        .firstName("master")
                        //no last name
                        //.lastName("Yoda")
                        .build()),
                //Vet with invalid specialty
                new FailedCreateIntegrationTestBundle<>(VetDto.builder()
                        .firstName("master")
                        .lastName("Yoda")
                        .specialtyIds(Collections.singleton(-1L))
                        .build())
        );
    }

    @Override
    protected List<FailedUpdateIntegrationTestBundle<Vet, VetDto,Long>> provideFailedUpdateTestBundles() {
        VetDto noNameUpdate = VetDto.builder()
                .firstName(null)
                .lastName("Yoda")
                .build();
        return Arrays.asList(
                new FailedUpdateIntegrationTestBundle<>(vetWithoutSpecialty,noNameUpdate)
        );
    }
}*/